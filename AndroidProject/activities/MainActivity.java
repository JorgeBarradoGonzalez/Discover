package jb.dam2.discover.activities;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import jb.dam2.discover.discoverUtilities.DiscoverUtilities;
import jb.dam2.discover.R;
import jb.dam2.discover.volley.RequestQueueSingleton;
import jb.dam2.discover.pojo.ShareInfo;
import jb.dam2.discover.recycler.RecyclerAdapter;

/*
    //JB: MainActivity Es la clase principal de Discover. En ella se muestran las publicaciones de personas a las que seguimos y
    es el punto de partida desde el que parten todas las actividades y acciones disponibles dentro de la aplicacion
*/
public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<View> mVideoViews;
    private Toolbar mToolbar;
    private ArrayList<String> mVideoIds;
    private String mSessionId;
    private String mUsername;
    private String mSharesInfoJSON;
    private ArrayList<ShareInfo> mSharesInfo;
    private String SERVER = DiscoverUtilities.getSERVER();
    private String mAlertsResponse;
    private boolean isLoaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //JB: Se establece Toolbar como la barra de accion de la actividad
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Discover");

        //JB: Se define el boton floatante y su accion
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            openSearchView();
        });

        //JB: Se define la capacidad de refrescar la actividad deslizando la pantalla en vertical
        SwipeRefreshLayout pullToRefresh = findViewById(R.id.refreshLayout);
        pullToRefresh.setOnRefreshListener(() -> {
            getFollowingShares();
            pullToRefresh.setRefreshing(false);
        });

        //JB: Se define el boton inferior de la interfaz y su accion vinculada
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    goToTop();
                    break;
            }
            return true;
        });


        //JB: Se gestiona el intent recibido por la anterior actividad (LoginActivity)
        Intent intent = getIntent();
        mSessionId = intent.getStringExtra("sessionId");
        mUsername = intent.getStringExtra("username");

        //JB: Definicion de los campos de la clase
        mVideoViews = new ArrayList<View>();
        mVideoIds = new ArrayList<>();
        mSharesInfo = new ArrayList<>();
        mSharesInfoJSON = "";
        mAlertsResponse = "";

        //JB: Peticion GET publicaciones de personas seguidas por el usuario sesión
        getFollowingShares();
        //JB: Peticion GET para comprobar si tiene alertas pendientes
        downloadAlerts();
    }

    /*
        //JB: setRecyclerView() Establece los campos necesarios para la lista Recyclerview de la actividad
     */
    private void setRecyclerView() {

        //JB: setRecyclerView() Establece la vista que usara la lista RecyclerView y si el tamaño del layout será fijo
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        //JB: Especificacion del Layout manager de la lista Recyclerview
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //JB: Especificacion del adaptador de la lista Recyclerview
        mAdapter = new RecyclerAdapter(mVideoViews, MainActivity.this);
        mRecyclerView.setAdapter(mAdapter);
    }


    /*
        //JB: openSearchView() Abre la actividad de búsqueda
     */
    private void openSearchView() {
        Intent intent = new Intent(this, ShareActivity.class);
        intent.putStringArrayListExtra("searchResultVideos", mVideoIds);
        intent.putExtra("sessionId", mSessionId);
        startActivity(intent);
    }


    /*
        //JB: openSearchView() Infla la vista del menu en la esquina superior derecha
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /*
        //JB: onOptionsItemSelected() Establece que action realizara cada elemento del menu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case R.id.findUserItem:
                goToFindUserActivity();
                break;
            case R.id.notificationItem:
                goToNotificationsActivity();
                break;
            case R.id.userProfileItem:
                goToUserProfileActivity();
                break;
            case R.id.endSession:
                endSession();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    /*
        //JB: endSession() Elimina la sesion del dispositivo y abre la actividad de Inicio de sesion
     */
    private void endSession() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("sessionId", "noSession").commit();
        goToLoginActivity();
    }

    /*
        //JB: goToLoginActivity() Abre la actividad de Inicio de Sesion
     */
    private void goToLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    /*
        //JB: goToUserProfileActivity() Abre la actividad de Perfil de Usuario
     */
    private void goToUserProfileActivity() {
        Intent intent = new Intent(this, UserProfileActivity.class);
        intent.putExtra("sessionId", mSessionId);
        intent.putExtra("username", mUsername);
        startActivity(intent);
    }

    /*
        //JB: goToFindUserActivity() Abre la actividad de Encontrar Usuario
     */
    private void goToFindUserActivity() {
        Intent intent = new Intent(this, FindUserActivity.class);
        intent.putExtra("sessionId", mSessionId);
        startActivity(intent);
    }

    /*
        //JB: getFollowingShares() Realiza la peticion GET para recoger las publicaciones de usuarios seguidos por el usuario sesion
     */
    public void getFollowingShares() {
        //JB: Inicializacion de la peticion
        RequestQueue requestQueue = new RequestQueueSingleton(getApplicationContext()).getRequestQueue();

        //JB: Se indica la url del servicio al que se va a hacer la llamada
        String url = SERVER + "/shares/download/" + mSessionId;

        //JB: Si ha habido respuesta, es decir, si se ha conseguido iniciar sesion se guarda en responseString y se gestiona
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    //JB: se recoge la respuesta y se guarda como la informacion de las publicaciones para despues manejarla
                    mSharesInfoJSON = response.toString();
                    manageSharesInfo(mSharesInfoJSON);
                },
                error -> {
                    //JB: El usuario no sigue a nadie
                    //DiscoverUtilities.showAlertDialog(this,R.string.downloadSharesError+error.toString());
                }
        );

        //JB: Se indica un tiempo de espera para la respuesta del servidor
        getRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        //JB: Se anade la peticion a la cola de peticiones de volley
        requestQueue.add(getRequest);
    }

    /*
        //JB: manageSharesInfo() Administra la respuesta de las publicaciones recibidas por el servidor.
        Se verifica si la publicacion esta ya mostrada en la interfaz. Si lo esta, se ignora. Si no lo esta
        se anade a la interfaz en la posicion 0 de la lista
     */
    private void manageSharesInfo(String response) {
        boolean isExisting = false;
        boolean isChanged = false;
        try {
            JSONObject reader = new JSONObject(response);
            JSONArray shares = reader.getJSONArray("shares");
            String videoTitle = "";
            String videoId = "";
            String comment = "";
            String username = "";
            ShareInfo newShare = null;

            //JB: Por cada una de las publicaciones recibidas y si no existen ya en la interfaz se anaden a la interfaz
            for (int i = 0; i < shares.length()&&!isExisting; i++) {
                JSONObject share = (JSONObject) shares.get(i);
                String id = share.getString("id");
                if(isLoaded){
                    for (int j=0;j<mSharesInfo.size()&&!isExisting;j++) {
                        String existingId = mSharesInfo.get(j).getMShareId();
                        if(existingId.equals(id)){
                            isExisting = true;
                        }
                    }
                }
                if(!isExisting){
                    videoTitle = share.getString("videoTitle");
                    videoId = share.getString("videoId");
                    comment = share.getJSONObject("comment").getString("text");
                    username = share.getJSONObject("user").getString("username");
                    newShare = ShareInfo.builder().mShareTitle(videoTitle).mShareVideoId(videoId).mShareComment(comment).mShareId(id).mUsername(username).build();

                    inflarVideos();

                    if(!isLoaded){
                        mSharesInfo.add(newShare);
                    }else{
                        isChanged = true;
                        mSharesInfo.add(0,newShare);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(!isLoaded){
            showInflatedShares();
            isLoaded = true;
        }else{
            if(isChanged){
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    /*
        //JB: inflarVideos() Infla una tarjeta de publicacion
     */
    private void inflarVideos() {
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        View youtubeCard = inflater.inflate(R.layout.youtube_card, null);
        mVideoViews.add(youtubeCard);
    }

    /*
        //JB: getmSharesInfo() da acceso a la informacion de las publicaciones
     */
    public ArrayList<ShareInfo> getmSharesInfo() {
        return mSharesInfo;
    }

    /*
        //JB: getmSessionId() da acceso a la informacion de la sesion
     */
    public String getmSessionId() {
        return mSessionId;
    }

    /*
        //JB: showInflatedShares() muestra las publicaciones de la lista Recyclerview
     */
    public void showInflatedShares() {
        setRecyclerView();
    }

    /*
        //JB: goToArtistProfile() abre la actividad de perfil de artista
     */
    public void goToArtistProfile(View v) {
        DiscoverUtilities.goToArtistProfile(this, v);
    }

    /*
        //JB: goToArtistProfile() abre la actividad de respuestas a una publicacion
     */
    public void goToReplyActivity(View v) {

        LinearLayout shareContainer = (LinearLayout) v.getParent().getParent();
        String shareId = ((TextView) shareContainer.findViewById(R.id.hiddenShareId)).getText().toString();
        //
        Intent intent = new Intent(this, ReplyActivity.class);
        intent.putExtra("sessionId", mSessionId);
        intent.putExtra("shareId", shareId);
        startActivity(intent);
    }

    /*
        //JB: likeBtnPressed() gestiona la pulsacion del boton LIKE de una publicacion
     */
    public void likeBtnPressed(View v) {
        DiscoverUtilities.likeBtnPressed(v,this,mSessionId);
    }


    /*
        //JB: goToTop() hace scroll en la lista hasta llegar a la posicion 0
    */
    public void goToTop() {
        ((NestedScrollView) findViewById(R.id.scrollViewMain)).smoothScrollTo(0, 0);
    }

    /*
        //JB: downloadAlerts() realiza la peticion GET de las alertas del usuario sesion
    */
    private void downloadAlerts() {
        // JB: Inicializacion de la peticion
        RequestQueue requestQueue = new RequestQueueSingleton(getApplicationContext()).getRequestQueue();

        //JB: Se indica la url del servicio al que se va a hacer la llamada
        String url = SERVER + "/replyAlerts/download/" + mSessionId;

        //JB: Si ha habido respuesta, es decir, si se ha conseguido iniciar sesion se guarda en responseString y se gestiona
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    mAlertsResponse = response.toString();
                    //JB: se envia la informacion a la funcion que la maneja
                    manageRepliesInfo(mAlertsResponse);
                },
                error -> {
                    //JB: El usuario no tiene alertas
                    //DiscoverUtilities.showAlertDialog(this,R.string.downloadAlertsError+error.toString());
                }
        );

        //JB: Se indica un tiempo de espera para la respuesta del servidor
        getRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        //JB: Se anade la peticion a la cola de peticiones de volley
        requestQueue.add(getRequest);
    }

    /*
        //JB: manageRepliesInfo() maneja la informacion de las alertas. Si hay alguna marcada como "no vista" se
        mostrara al usuario un mensaje indicando que hay notificaciones sin ver
    */
    private void manageRepliesInfo(String mAlertsInfo) {
        boolean isNewAlert = false;
        try {
            JSONObject reader = new JSONObject(mAlertsInfo);
            JSONArray shares = reader.getJSONArray("replyAlerts");
            for (int i = 0; i < shares.length() && !isNewAlert; i++) {
                JSONObject share = (JSONObject) shares.get(i);
                boolean isSeen = share.getBoolean("seen");
                if (!isSeen) {
                    isNewAlert = true;
                    DiscoverUtilities.showAlertDialog(this, getResources().getString(R.string.newAlertsWarning));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*
        //JB: goToNotificationsActivity() Abre la actividad de Ver Notificaciones
    */
    private void goToNotificationsActivity() {
        Intent intent = new Intent(this, AlertActivity.class);
        intent.putExtra("sessionId", mSessionId);
        intent.putExtra("alertsResponse", mAlertsResponse);
        startActivity(intent);
    }
}