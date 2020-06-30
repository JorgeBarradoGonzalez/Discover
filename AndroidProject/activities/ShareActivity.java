package jb.dam2.discover.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Pattern;

import jb.dam2.discover.discoverUtilities.DiscoverUtilities;
import jb.dam2.discover.youtubeUtilities.GetSearchVideos;
import jb.dam2.discover.R;
import jb.dam2.discover.volley.RequestQueueSingleton;
import jb.dam2.discover.recycler.RecyclerAdapter;

/*
    JB: ShareActivity es la actividad que gestiona la busqueda de videos que compartir y su envio al servidor
*/
public class ShareActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<String> mVideoIds;
    private ArrayList<View> mVideoViews;
    private int mTimes;
    private String mSessionId;
    private boolean mOkButton;
    private String SERVER = DiscoverUtilities.getSERVER();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        //JB: Definicion de vsitas y campos
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.share));
        Intent intent = getIntent();
        mVideoIds = intent.getStringArrayListExtra("searchResultVideos");
        mSessionId = intent.getStringExtra("sessionId");
        mVideoViews = new ArrayList<>();
        mTimes = 1;
        mOkButton = false;
    }

    /*
    JB: onCreateOptionsMenu() infla la vista de busqueda
*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setBackgroundColor(getResources().getColor(R.color.black));
        return true;
    }

    /*
    JB: onNewIntent() detecta la accion  realizada. En esta actividad, una busqueda
*/
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    /*
    JB: handleIntent() gestiona la barra busqueda, guardando el termino y dando paso a la funcion que se encarga de la busuqeda
*/
    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            if (mTimes == 1) {
                if (!query.equals("")) {
                    getSearchVideos(query);
                }
                mTimes = 0;
            }
        }
        mTimes = 1;
        mVideoIds.clear();
        mVideoViews.clear();
    }

    /*
   JB: getSearchVideos() ejecuta el hilo de busqueda de Youtube
*/
    private void getSearchVideos(String query) {
        new GetSearch(query).execute();
    }

    /*
   JB: setmVideoIds() Establece la informacion de los videos resultado una vez que ha terminado el hilo de busqueda
*/
    public void setmVideoIds(ArrayList<String> mVideoIds) {
        this.mVideoIds = mVideoIds;
        this.runOnUiThread(() -> {
            inflateVideoResults();
            setRecycleViewShare();
        });
    }

    /*
   JB: inflateVideoResults() Llama a la funcion que Infla un video por cada resultado de la busqueda de Youtube
*/
    private void inflateVideoResults() {
        for (String videoId : mVideoIds) {
            inflateSearchVideos();
        }
    }

    /*
   JB: inflateSearchVideos() Infla un video por cada resultado de la busqueda de Youtube
*/
    private void inflateSearchVideos() {
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        View youtubeCard = inflater.inflate(R.layout.youtube_share, null);
        mVideoViews.add(youtubeCard);
    }

    /*
   JB: setRecycleViewShare() Define y muestra la lista Recyclerview de videos
*/
    public void setRecycleViewShare() {

        //JB: Definicion del la vista de la lista Recyclerview y su tamano
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view_share);
        mRecyclerView.setHasFixedSize(true);

        //JB: Definicion del Layout Manager
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //JB: Definicion del adaptador
        mAdapter = new RecyclerAdapter(mVideoViews, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    /*
   JB: getVideoSearchResult() Devuelve la lista de id de videos resultado de la busqueda de Youtube
*/
    public ArrayList<String> getVideoSearchResult() {
        return mVideoIds;
    }

    /*
   JB: setComment() Llama a la funcion de gestion del comentario
*/
    public void setComment(View view) {
        findComment(view);
    }

    /*
   JB: uploadShare() Llama a la funcion encargada de realizar la peticion del envio de la publicacion
*/
    private void uploadShare(View view,String comment){
        LinearLayout linearLayout = (LinearLayout) view.getParent().getParent();
        TextView textView = ((TextView) linearLayout.findViewById(R.id.youtubeShareTitle));
        String videoTitle = textView.getText().toString();
        YouTubePlayerView youTubePlayerView = (YouTubePlayerView) ((LinearLayout) view.getParent().getParent()).findViewById(R.id.youtube_player_view);

        //COMENTARIO: Pattern que no comience por espacio, que siga lo que quiera, pero despues un guion
        Pattern pattern = Pattern.compile("^[^-\\s].*-.*");
        if (pattern.matcher(videoTitle).matches()) {
            String artist = videoTitle.split("-")[0];
            String videoId = getVideoId(linearLayout);
            postShare(videoId,mSessionId,comment,artist.toUpperCase(),videoTitle);
        } else {
            DiscoverUtilities.showAlertDialog(ShareActivity.this,getString(R.string.wrongFormat));
        }
    }

    /*
   JB: getVideoId() Recoge el id del video que se va a enviar
*/
    private String getVideoId(LinearLayout linearLayout) {
        TextView hiddenId = linearLayout.findViewById(R.id.hiddenVideoId);
        return hiddenId.getText().toString();
    }

    /*
   JB: findComment() Crea un dialogo de confirmacion antes de compartir la publicacion
*/
    private void findComment(View view){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.addComment));

        //JB: Definicion del layout
        View customLayout = getLayoutInflater().inflate(R.layout.share_comment, null);
        builder.setView(customLayout);

        //JB: boton de ok
        builder.setPositiveButton(getString(R.string.share), (DialogInterface dialog, int which) -> {
            // send data from the AlertDialog to the Activity
            EditText editText = customLayout.findViewById(R.id.editText);
            String comment = (editText.getText().toString());
            uploadShare(view,comment);
        });

        //JB: boton de cancelar
        builder.setNeutralButton(getString(R.string.cancel), (DialogInterface dialog, int which) -> {

        });

        //JB: separacion de botones
        builder.setNegativeButton("", (DialogInterface dialog, int which) -> {

        });

        //JB: Se muestra el dialogo
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /*
   JB: postShare() Se encarga de la publicacion al servidor de
*/
    private void postShare(String videoId,String sessionId,String comment,String artist,String videoTitle) {

        //JB: Inicializacion de la peticion
        RequestQueue requestQueue = new RequestQueueSingleton(getApplicationContext()).getRequestQueue();

        //JB: Creacion del objeto JSON
        JSONObject object = new JSONObject();
        try {
            object.put("videoId", videoId);
            object.put("sessionId", sessionId);
            object.put("comment", comment);
            object.put("artist", artist);
            object.put("videoTitle", videoTitle);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //JB: definicion de la url
        String url = SERVER+"/shares/upload";

        //JB: Definicion del apeticion POST
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, object,
                response -> DiscoverUtilities.createLongToast(this,getString(R.string.ok)),
                error -> DiscoverUtilities.createLongToast(this,getString(R.string.error)));

        //JB: Se establece tiempo de espera para respuesta
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        //JB: Se anade a la cola de peticiones
        requestQueue.add(jsonObjectRequest);
    }

    /*
        JB:GetSearch es una clase hilo que se ejecuta de manera asincrona y que se encarga de realizar las busquedas de Youtube
    */
    public class GetSearch extends AsyncTask<String, Integer, String> {
        private String query;

        public GetSearch(String query) {
            this.query = query;
        }

        @Override
        protected String doInBackground(String... videoIds) {
            GetSearchVideos getSearchVideos = new GetSearchVideos(query, ShareActivity.this);
            getSearchVideos.getSearchResults();
            return "";
        }
    }

    /*
   JB: onSupportNavigateUp() vuelve a la actividad anterior y elimina esta
*/
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        finish();
        return true;
    }
}
