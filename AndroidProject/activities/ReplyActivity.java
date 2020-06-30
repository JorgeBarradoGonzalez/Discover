package jb.dam2.discover.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import jb.dam2.discover.discoverUtilities.DiscoverUtilities;
import jb.dam2.discover.youtubeUtilities.YoutubeUIController;
import jb.dam2.discover.pojo.ReplyInfo;
import jb.dam2.discover.R;
import jb.dam2.discover.volley.RequestQueueSingleton;
import jb.dam2.discover.recycler.RecyclerAdapterReplies;

/*
    JB: ReplyActivity es la actividad que gestiona los comentarios dentro de una publicacion.
*/
public class ReplyActivity extends AppCompatActivity {

    private LinearLayout mShare;
    private String mSessionId;
    private String mTitle;
    private String mVideoId;
    private String mShareId;
    private String mShareComment;
    private String mShareUsername;
    private String mRepliesInfo;
    private ArrayList<View> mReplies;
    private ArrayList<ReplyInfo> repliesInfo;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private String SERVER = DiscoverUtilities.getSERVER();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.replyActivity);
        Intent intent = getIntent();
        mSessionId = intent.getStringExtra("sessionId");
        Log.d("volley", "ReplyActivity onCreate - Session es: "+mSessionId);
        mShareId = intent.getStringExtra("shareId");
        mShare = findViewById(R.id.youtubeCard);
        //load video
        getShareInfoRequest();
    }

    /*
    JB: getShareInfoRequest() realiza la peticion de informacion para la publicacion seleccionada
*/
    private void getShareInfoRequest(){

        //JB: Inicializacion de la peticion
        RequestQueue requestQueue = new RequestQueueSingleton(getApplicationContext()).getRequestQueue();

        //JB: Definicion de la URL del servicio
        String url = SERVER+"/shares/get/"+mShareId;

        //JB: Se establece la peticion GET
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> manageShareInfo(response.toString()),
                error -> DiscoverUtilities.showAlertDialog(ReplyActivity.this,getString(R.string.lastFMConexionError))
        );

        //JB: Se establece un tiempo de espera en la respuesta
        getRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        //JB: Se anade la peticion a la cola
        requestQueue.add(getRequest);
    }

    /*
   JB: manageShareInfo() gestiona la informacion de la publicacion recibida por el servidor
*/
    private void manageShareInfo(String shareInfo) {
        try {
            JSONObject reader = new JSONObject(shareInfo);
            mTitle = reader.getString("videoTitle");
            mVideoId = reader.getString("videoId");
            mShareId = reader.getString("id");
            mShareComment = reader.getJSONObject("comment").getString("text");
            mShareUsername = reader.getJSONObject("user").getString("username");

            //JB: Guardados los campos, se guardan dentro de la publicacion
            ((TextView)mShare.findViewById(R.id.youtubeShareTitle)).setText(mTitle);
            ((TextView)mShare.findViewById(R.id.youtubeShareComment)).setText(mShareComment);
            ((TextView)mShare.findViewById(R.id.shareUsername)).setText(mShareUsername);
            ((TextView)mShare.findViewById(R.id.hiddenVideoId)).setText(mVideoId);
            ((TextView)mShare.findViewById(R.id.hiddenShareId)).setText(mShareId);
            findViewById(R.id.replyIcon).setVisibility(View.INVISIBLE);

            //JB: Se carga el video y se muestra la publicacion
            loadViewsAndVideo();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*
   JB: loadViewsAndVideo() carga el video y muestra la publicacion
*/
    private void loadViewsAndVideo(){
        //JB: Carga la vista del video
        YouTubePlayerView playerView = (YouTubePlayerView)findViewById(R.id.youtube_player_view);
        playerView.getYouTubePlayerWhenReady(youTubePlayer -> youTubePlayer.cueVideo(mVideoId,0));
        this.getLifecycle().addObserver(playerView);
        playerView.addFullScreenListener(new YoutubeUIController(ReplyActivity.this,playerView));

        //JB: Gestiona si esa publicacion tiene un like establecido anteriormente
        LinearLayout shareContainer = (LinearLayout) playerView.getParent().getParent().getParent();
        DiscoverUtilities.manageLike(mSessionId,mShareId,shareContainer,this);


        mReplies = new ArrayList<>();
        repliesInfo = new ArrayList<>();
        //JB: Llamada a la funcion que se encarga de las respuestas relacionadas con la publicacion
        downloadReplies();
    }

    /*
   JB: downloadReplies() realiza la peticion que descarga los comentarios de una publicacion
*/
    private void downloadReplies() {

        //JB: Inicializacion de la peticion
        RequestQueue requestQueue = new RequestQueueSingleton(getApplicationContext()).getRequestQueue();

        //JB: Definicion del URL del servicio
        String url = SERVER+"/replies/download/"+mShareId;

        //JB: Se establece la peticion GET
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    //JB: Se llama a la funcion de gestion de la respuesta
                    mRepliesInfo = response.toString();
                    manageRepliesInfo(mRepliesInfo);
                },
                error -> DiscoverUtilities.showAlertDialog(ReplyActivity.this,R.string.lastFMConexionError+error.getMessage())
        );

        //JB: Se establece un tiempo de espera para la respuesta del servidor
        getRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        //JB: Se anade la peticion a la cola de peticiones Volley
        requestQueue.add(getRequest);
    }

    /*
   JB: manageRepliesInfo() realiza la gestion de los comentarios relacionados con la publicacion
*/
    private void manageRepliesInfo(String mRepliesInfo) {
        try {
            JSONObject reader = new JSONObject(mRepliesInfo);
            JSONArray shares = reader.getJSONArray("replies");
            for (int i=0;i<shares.length();i++){
                JSONObject share = (JSONObject) shares.get(i);
                String text = share.getString("text");
                String username = share.getString("username");
                repliesInfo.add(ReplyInfo.builder().mText(text).mUsername(username).build());
                inflateReplies();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        showInflatedReplies();
    }

    /*
   JB: showInflatedReplies() Establece la lista que se mostrará en la interfaz de la actividad
*/
    private void showInflatedReplies() {

        //JB: Definicion del la vista de la lista Recyclerview y su tamano
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        //JB: Definicion del Layout Manager
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //JB: Definicion del adaptador
        mAdapter = new RecyclerAdapterReplies(mReplies, ReplyActivity.this);
        mRecyclerView.setAdapter(mAdapter);
    }

    /*
   JB: inflateReplies() Infla una respuesta
*/
    private void inflateReplies(){
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layoutComment = inflater.inflate(R.layout.layout_comment, null);
        mReplies.add(layoutComment);
    }

    /*
   JB: shareComment() Gestiona el envio de un comentario, con un dialogo previo para confirmar la ccion
*/
    public void shareComment(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Comment "+mShareUsername+"'s post");
        //JB: Se establece el layout
        View customLayout = getLayoutInflater().inflate(R.layout.share_comment, null);
        builder.setView(customLayout);
        //JB: Se anade boton de OK
        builder.setPositiveButton(getString (R.string.commentShare), (DialogInterface dialog, int which) -> {
            //JB: Se envia la respuesta a la funcion de comentar
            EditText editText = customLayout.findViewById(R.id.editText);
            String reply = (editText.getText().toString());
            if(!reply.equals("")){
                postComment(reply);
            }else{
                DiscoverUtilities.createLongToast(this,getResources().getString(R.string.noEmptyComment));
            }
        });

        //JB: Se anade boton de cancelar
        builder.setNeutralButton("CANCEL COMMENT", (DialogInterface dialog, int which) -> {

        });

        //JB: Se crea un espacio entre botones
        builder.setNegativeButton("", (DialogInterface dialog, int which) -> {

        });

        //JB: Se muestra el dialogo creado
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /*
   JB: postComment() Gestiona la peticion del envio de un comentario
*/
    private void postComment(String reply) {
        //JB: Inicializacion de la peticion
        RequestQueue requestQueue = new RequestQueueSingleton(getApplicationContext()).getRequestQueue();

        //JB: Creacion del objeto JSON
        JSONObject object = new JSONObject();
        try {
            //input your API parameters
            object.put("sessionId", mSessionId);
            object.put("shareId", mShareId);
            object.put("text", reply);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //JB: Definicion de la URL del servicio
        String url = SERVER+"/replies/upload";

        //JB: Definicion de la peticion POST
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, object,
                response -> {
                    DiscoverUtilities.createLongToast(this,getString(R.string.ok));
                }, error -> {
            DiscoverUtilities.createLongToast(this,getString(R.string.error));
        });

        //JB: Se establece un tiempo para la respuesta del servidor
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        //JB: Se anade la peticion a la cola de peticiones
        requestQueue.add(jsonObjectRequest);
    }

    /*
   JB: goToArtistProfile() Abre la actividad de PERFIL ARTISTA
*/
    public void goToArtistProfile(View v){
        DiscoverUtilities.goToArtistProfile(this,v);
    }

    /*
   JB: getRepliesInfo() Devuelve la informacion de las respustas
*/
    public ArrayList<ReplyInfo> getRepliesInfo(){
        return repliesInfo;
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

    /*
   JB: likeBtnPressed() llama a la gestion de LIKE
*/
    public void likeBtnPressed(View v) {
        DiscoverUtilities.likeBtnPressed(v,this,mSessionId);
    }
}
