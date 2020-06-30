package jb.dam2.discover.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jb.dam2.discover.discoverUtilities.DiscoverUtilities;
import jb.dam2.discover.youtubeUtilities.GetSearchVideos;
import jb.dam2.discover.pojo.SpotifyUser;
import jb.dam2.discover.pojo.UserService;
import jb.dam2.discover.R;
import jb.dam2.discover.volley.RequestQueueSingleton;
import jb.dam2.discover.recycler.RecyclerAdapter;

/*
    //JB: ArtistProfileActivity  es la actividad que muestra toda la informacion referente a un artista. Para su uso es necesario
    tener en el dispositivo movil la aplicacion Spotify, ya que se necesita la autorizacion del usuario para realizar modificaciones
*/
public class ArtistProfileActivity extends AppCompatActivity {

    //JB: Las variables finales son privadas. Son dadas por el panel de desarrollador de Spotify
    private final String CLIENT_ID = "";
    private final String CLIENT_SECRET = "";
    private URI REDIRECT_URI;
    private final int REQUEST_CODE = "";
    private final String SCOPES = "user-library-modify,user-read-private,user-follow-read,user-follow-modify";
    private SpotifyAppRemote mSpotifyAppRemote;
    private SharedPreferences.Editor editor;
    private SharedPreferences mSharedPreferences;
    private RequestQueue mQueue;
    private String mToken = "";
    private String mArtistInfo = "";
    private String mArtistName = "";
    private ImageView mArtistProfilePic;
    private Toolbar mToolbar;
    private Button mBioBtn;
    private Button mMusicBtn;
    private String artistId = "";
    private boolean mIsFollowing = false;
    private boolean mIsMusicSet;
    private ViewFlipper mFlipper;
    private ArrayList<String> mVideoIds;
    private ArrayList<View> mVideoViews;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_profile);

        //JB: Se establece Toolbar como la barra de accion de la actividad
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.artistProfile);



        //JB: Se recoge la informacion de la actividad anterior
        Intent intent = getIntent();
        mArtistName = intent.getStringExtra("artistName");
        {
            try {
                REDIRECT_URI = new URI("");
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }

        //JB: Se define el objeto SharedPreferences para la clave
        mSharedPreferences = this.getSharedPreferences("SPOTIFY", 0);

        //JB: Se procede a establecer los parametros de conexion con la API de Spotify y autentificar el usuario
        ConnectionParams connectionParams = new ConnectionParams.Builder(CLIENT_ID).setRedirectUri(REDIRECT_URI.toString()).showAuthView(true).build();
        SpotifyAppRemote.connect(this, connectionParams,
                new Connector.ConnectionListener() {

                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        //DiscoverUtilities.showAlertDialog(ArtistProfileActivity.this,throwable.getMessage());
                    }
                });
        authenticateSpotify();

        //JB: Definicion de campos y acciones

        //JB: Se define la vista que contendra la imagen del artista
        mArtistProfilePic = findViewById(R.id.artistProfilePic);

        //JB: Se define el objeto SharedPreferences para la clave
        mSharedPreferences = this.getSharedPreferences("SPOTIFY", 0);

        mQueue = Volley.newRequestQueue(this);

        mBioBtn = findViewById(R.id.artistProfileBioBtn);
        mBioBtn.setOnClickListener(v -> {
            requestFMBio();
        });
        mMusicBtn = findViewById(R.id.artistProfileMusicBtn);
        mMusicBtn.setOnClickListener(v -> {
            setArtistMusic();
        });
        mBioBtn.setEnabled(false);
        mIsMusicSet = false;
        mFlipper = findViewById(R.id.artistProfileFlipper);
        mVideoIds = new ArrayList<>();
        mVideoViews = new ArrayList<>();

        //JB: Peticion de biografia a la API de LastFM y Peticion a la API de Youtube para que me de los resultados del artista
        requestFMBio();
        setArtistMusic();
    }

    /*
        //JB: setArtistMusic() llama a la busqueda del artista en Youtube o desliza el viewFlipper en caso de que ya se haya realizado la busqueda
     */
    private void setArtistMusic() {
        if(!mIsMusicSet){
            makeArtistSearch();
        }else{
            setFlipper(1);
        }
    }

    /*
        //JB: makeArtistSearch() crea un hilo GetSearch la busqueda del artista en Youtube
     */
    private void makeArtistSearch() {
        new GetSearch(mArtistName).execute();
    }

    /*
        //JB: requestFMBio() usa la API de LastFM para realizar la busqueda del artista y llama a la funcion que gestiona la respuesta
        si ya se ha establecido, se desliza el view flipper
     */
    private void requestFMBio() {
        if(!mIsMusicSet){

            //JB: Inicializacion de la peticion
            RequestQueue requestQueue = new RequestQueueSingleton(getApplicationContext()).getRequestQueue();

            //JB: definicion de la url de la peticion
            String url = "http://ws.audioscrobbler.com/2.0/?method=artist.getinfo&artist="+ mArtistName.trim()+"&api_key=5eaae346c5047733f46c105353ec9bda&format=json";

            //JB: Llamada a la gestion de la respuesta del servicio
            JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null, (JSONObject response) -> {
                manageFMArtistInfo(response.toString());
            }, error -> DiscoverUtilities.showAlertDialog(ArtistProfileActivity.this,getResources().getString(R.string.lastFMConexionError)));

            //JB: Se establece tiempo de espera para la respuesta
            getRequest.setRetryPolicy(new DefaultRetryPolicy(
                    15000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            //JB: Se anade la peticion a la cola volley
            requestQueue.add(getRequest);
        }else{
            setFlipper(0);
        }
    }

    /*
        //JB: manageFMArtistInfo() gestiona la respuesta del servicio de LastFM para despues llamar a la funcion que lo muestre
     */
    private void manageFMArtistInfo(String response) {
        Log.d("lastfm", "en manage respuesta: " + response);
        try {
            JSONObject reader = new JSONObject(response);
            JSONObject artist = reader.getJSONObject("artist");
            String content = artist.getJSONObject("bio").getString("content");
            setArtistBio(content);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*
        //JB: setArtistBio() Muestra la informacion del artista en pantalla, eliminando los caracteres no deseados
     */
    private void setArtistBio(String content) {
        int startIndex = content.indexOf("<");
        int endIndex = content.indexOf("apply.");
        String replacement = "";
        try {
            String toBeReplaced = content.substring(startIndex, endIndex+6);
            runOnUiThread(() -> ((TextView)findViewById(R.id.artistBioText)).setText(content.replace(toBeReplaced, replacement)));
        }catch(IndexOutOfBoundsException e){
            e.printStackTrace();
        }
    }

    /*
        //JB: setFlipper() modifica la interfaz dependiendo de la vista en que se encuente el viewFlipper
     */
    private void setFlipper(int op){
        if(op==0){
            mFlipper.showPrevious();
            mBioBtn.setEnabled(false);
            mMusicBtn.setEnabled(true);
        }else{
            mFlipper.showNext();
            mMusicBtn.setEnabled(false);
            mBioBtn.setEnabled(true);
        }
    }

    /*
        //JB: authenticateSpotify() Autentifica al usuario de Spotify segun el token recibido por la API. Para ello recoge el resultado
        de la actividad de Login de Spotify
     */
    private void authenticateSpotify() {
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI.toString());
        builder.setScopes(new String[]{SCOPES});
        AuthenticationRequest request = builder.build();
        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    /*
        //JB: onActivityResult() recoge el resultado de la actividad Login dee Spotify lanzada anteriormente
        y guarda el token resultado
    */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        //JB: verifica el resultado recibido de la actividad Login de Spotify
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);

            switch (response.getType()) {
                //JB: La peticion ha tenido exito y se ha recibido un token
                case TOKEN:
                    SharedPreferences.Editor editor = getSharedPreferences("SPOTIFY", 0).edit();
                    editor.putString("token", response.getAccessToken());
                    mToken = response.getAccessToken();
                    editor.apply();
                    waitForUserInfo();
                    break;

                //JB: Ha habido un error en la peticion
                case ERROR:
                    break;

                //JB: Se ha cancelado la peticion
                default:
            }
        }
    }

    /*
        //JB: get() realiza la peticion de informacion del artista con la API de Spotify, en la cual se recoge unicamente su imagen
    */
    public void getArtistPic() {
        //JB: Inicializacion de la peticion
        RequestQueue requestQueue = new RequestQueueSingleton(getApplicationContext()).getRequestQueue();

        //JB: Definicion de la URL
        String url = "https://api.spotify.com/v1/search?q=" + mArtistName.trim() + "&type=artist&limit=1";

        //JB: Se establece la peticon GET para la API de Spotify
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null, (JSONObject response) -> {
            mArtistInfo = response.toString();
            manageArtistInfo(mArtistInfo);
        }, error -> DiscoverUtilities.showAlertDialog(ArtistProfileActivity.this,getResources().getString(R.string.spotifyAuthError)+error.getMessage())) {

            //JB: Se establece el header en el que va insertado el token anteriormente recibido
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + mToken);
                return headers;
            }
        };

        //JB: Se establece el tiempo de espera maximo para la respuesta
        getRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        //JB: Se anade la peticion a la cola de Volley
        requestQueue.add(getRequest);
    }

    /*
        //JB: waitForUserInfo() Recoge la informacion del usuario de Spotify de este dispositivo
    */
    private void waitForUserInfo() {
        //JB: Se recoge la informacion del usuario y se guarda en el almacenamiento privado de la aplicacion
        UserService userService = new UserService(mQueue, mSharedPreferences);
        userService.get(() -> {
            SpotifyUser user = userService.getUser();
            SharedPreferences.Editor editor = getSharedPreferences("SPOTIFY", 0).edit();
            editor.putString("userid", user.getId());

            //JB: Envio de la peticion de informacion del artista para recoger la imagen
            getArtistPic();
            //JB: Guardamos la informacion del id del usuario de spotify en la preferencias
            editor.commit();
        });
    }

    /*
        //JB: manageArtistInfo() Gestiona la respuesta de toda la informacion del artista recibida en la peticion
        de la API de Spotify. Unicamente recoge la imagen del artista para despues mostrarla en la interfaz
    */
    private void manageArtistInfo(String response) {
        ArrayList<String> imagesURL = new ArrayList<String>();
        try {
            JSONObject reader = new JSONObject(response);
            JSONObject artist = reader.getJSONObject("artists");
            JSONArray items = artist.getJSONArray("items");
            for (int i = 0; i < items.length(); i++) {
                JSONObject object = items.getJSONObject(0);
                artistId = object.getString("id");
                JSONArray images = object.getJSONArray("images");
                for (int j = 0; j < images.length(); j++) {
                    String url = images.getJSONObject(j).getString("url");
                    imagesURL.add(url);
                }
            }
            //JB: Con el URL de la imagen recogida, la carga en la interfaz
            Picasso.get().load(imagesURL.get(1)).into(mArtistProfilePic);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //JB: Recogida la imagen, se carga el nombre
        ((TextView) findViewById(R.id.artistProfileName)).setText(mArtistName.toUpperCase());

        //JB: Se verfica si el usuario sigue al artista
        verifyFollow();
    }

    /*
        //JB: verifyFollow() Realiza la peticion para verificar si el usuario sigue al artista de la actividad
    */
    private void verifyFollow() {
        //JB: Inicializacion de la peticion
        RequestQueue requestQueue = new RequestQueueSingleton(getApplicationContext()).getRequestQueue();

        //JB: Se establece la URL
        String url = "https://api.spotify.com/v1/me/following/contains?type=artist&ids=" + artistId;
        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null, (JSONArray response) -> {
            try {
                //JB: Llamada a la funcion que maneja la informacion
                manageFollow(response.get(0).toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error ->  DiscoverUtilities.showAlertDialog(ArtistProfileActivity.this,getString(R.string.spotifyFollowError)+error.getMessage())) {
            //
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + mToken);
                return headers;
            }
        };

        getRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(getRequest);
    }

    /*
        //JB: manageFollow() Gestiona la respuesta sobre si el usuario sigue o no al artista. Dependiendo del resultado,
        el texto del boton de SEGUIR ARTISTA, cambia
    */
    private void manageFollow(String response) {
        Button spotifyFollowBtn = (Button) findViewById(R.id.artistProfileFollowBtn);
        //Log.d("spotify", "response en manage follow es: " + response);
        if (response.equals("false")) {
            spotifyFollowBtn.setText(getString(R.string.spotifyFollow));
            mIsFollowing = false;
        } else {
            spotifyFollowBtn.setText(getString(R.string.spotifyUnfollow));
            mIsFollowing = true;
        }

    }

    /*
        //JB: setIsFollowing() llama a la funcion que gestiona el seguimiento a un artista en Spotify
    */
    public void setIsFollowing(View v) {
        modifyFollow(mIsFollowing);
    }


    /*
        //JB: modifyFollow() gestiona el seguimiento de un artista en Spotify mediante la llamada al servicio de sus API
    */
    private void modifyFollow(boolean isFollowing){

        //JB: Inicializacion de la peticion
        RequestQueue requestQueue = new RequestQueueSingleton(getApplicationContext()).getRequestQueue();

        //JB: Definicion de la URL de la peticion
        String url = "https://api.spotify.com/v1/me/following?type=artist&ids=" + artistId;

        JsonObjectRequest getRequest = null;
        if(isFollowing){
            getRequest = new JsonObjectRequest(Request.Method.DELETE, url, null, (JSONObject response) -> {

            }, error -> {
                //DiscoverUtilities.showAlertDialog(ArtistProfileActivity.this, getResources().getString(R.string.spotifyFollowError) + error.getMessage());
            }) {

                //JB: Definicion del Header de la peticion
                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer " + mToken);
                    return headers;
                }
            };

            //JB: Modificacion del texto del boton si no se sigue al artista
            ((Button)findViewById(R.id.artistProfileFollowBtn)).setText(R.string.spotifyFollow);

        }else{
            getRequest = new JsonObjectRequest(Request.Method.PUT, url, null, (JSONObject response) -> {
                //COMENTARIO: RESPUESTA VACIA PERO FUNCION CORRECTA
                //Log.d("spotify", "RESPUESTA FOLLOW ES: " + response);
            }, error -> DiscoverUtilities.showAlertDialog(ArtistProfileActivity.this,getResources().getString(R.string.spotifyFollowError)+error.getMessage())) {

                ////JB: Definicion del Header de la peticion
                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer " + mToken);
                    return headers;
                }
            };
            //JB: Modificacion del texto del boton si se sigue al artista
            ((Button)findViewById(R.id.artistProfileFollowBtn)).setText(R.string.spotifyUnfollow);
        }

        //JB: Definicion del tiempo de espera para la respuesta del servidor
        getRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        //JB: Se anade la peticion a la cola de peticiones
        requestQueue.add(getRequest);
    }


    /*
        //JB: setVideoIds() muestra en la interfaz los videos recibidos del hilo de busqueda en Youtube GetSearch() correspondientes al artista
        Una vez que estan todos definidos
    */
    public void setVideoIds(ArrayList<String> videoIds) {
        this.mVideoIds = videoIds;
        mIsMusicSet =true;
        runOnUiThread(() -> {
            inflateVideoResults();
            setRecycleViewShare();
        });
    }

    /*
        //JB: inflateVideoResults() infla una tarjeta de video por cada uno de los resultados de la busqueda del hilo GetSearch llama a la funcion que lo infla
    */
    private void inflateVideoResults() {
        for (String videoId : mVideoIds) {
            inflateSearchVideos();
        }
    }

    /*
        //JB: inflateSearchVideos() infla una tarjeta de video por cada uno de los resultados de la busqueda del hilo GetSearch
    */
    private void inflateSearchVideos() {
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        View youtubeCard = inflater.inflate(R.layout.youtube_share, null);
        mVideoViews.add(youtubeCard);
    }

    /*
        JB: setRecycleViewShare() Establece la lista que se mostrará en la interfaz de la actividad
    */
    public void setRecycleViewShare() {

        //JB: Definicion del la vista de la lista Recyclerview y su tamano
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view_artist);
        mRecyclerView.setHasFixedSize(true);

        //JB: Definicion del Layout Manager
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //JB: Definicion del adaptador
        mAdapter = new RecyclerAdapter(mVideoViews, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    /*
        //JB: Devulve la lista de los videos de la actividad
    */
    public ArrayList<String> getVideoSearchResult() {
        return mVideoIds;
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
            GetSearchVideos getSearchVideos = new GetSearchVideos(query, ArtistProfileActivity.this);
            getSearchVideos.getSearchResults();
            return "";
        }
    }


    /*
        //JB: onSupportNavigateUp() vuelve a la actividad anterior y elimina esta
    */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        finish();
        return true;
    }
}
