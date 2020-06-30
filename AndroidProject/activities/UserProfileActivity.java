package jb.dam2.discover.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import jb.dam2.discover.discoverUtilities.DiscoverUtilities;
import jb.dam2.discover.R;
import jb.dam2.discover.volley.RequestQueueSingleton;
import jb.dam2.discover.pojo.ShareInfo;
import jb.dam2.discover.recycler.RecyclerAdapterSelf;

/*
    JB: UserProfileActivity es la actividad que gestiona la informacion de un usuario
*/
public class UserProfileActivity extends AppCompatActivity {

    private String mUsernameProfile;
    private String mSessionId;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<View> mVideoViews;
    private ArrayList<View> mLikesViews;
    private Toolbar mToolbar;
    private ArrayList<String> videoIds;
    private ArrayList<ShareInfo> mSharesInfo;
    private ArrayList<ShareInfo> mLikesInfo;
    private ArrayList<String> mLikesIds;
    private boolean mIsFollowingsSet;
    private boolean mIsLikesSet;
    private ViewFlipper mFlipper;
    private Button mSharesBtn;
    private Button mFollowingBtn;
    private Button mLikesBtn;
    private String SERVER = DiscoverUtilities.getSERVER();
    private Button mFollowButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        //JB: Definicion de campos
        mToolbar = findViewById(R.id.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.userProfile);
        Intent intent = getIntent();


        mUsernameProfile = intent.getStringExtra("username");
        mSessionId = intent.getStringExtra("sessionId");
        mSharesInfo = new ArrayList<>();
        videoIds = new ArrayList<>();
        //
        mLikesInfo = new ArrayList<>();
        mLikesIds = new ArrayList<>();
        mLikesViews = new ArrayList<>();
        //
        mVideoViews = new ArrayList<>();
        mIsFollowingsSet = false;
        mIsLikesSet = false;
        mFlipper = findViewById(R.id.userProfileInfoFlipper);
        mSharesBtn = findViewById(R.id.userProfileSharesBtn);
        mSharesBtn.setOnClickListener(v -> getUserShares());
        mFollowingBtn = findViewById(R.id.userProfileFollowingBtn);
        mFollowingBtn.setOnClickListener(v -> getFollowedUsers());
        mFollowButton = findViewById(R.id.userProfileFollowBtn);
        mFollowButton.setOnClickListener(v -> manageFollow());
        mLikesBtn = findViewById(R.id.userProfileLikesBtn);
        mLikesBtn.setOnClickListener(v -> getUserLikes());

        isSessionUserProfileRequest();

        setUsernameViews();
        ((Button) findViewById(R.id.userProfileSharesBtn)).setEnabled(false);
    }

    /*
    JB: isSessionUserProfileRequest() llama a la funcion que verifica si el perfil es el propio
*/
    private void isSessionUserProfileRequest() {
        Log.d("volley", "on user profile: "+mUsernameProfile);
        TextView usernameTextView = (TextView)findViewById(R.id.userProfileName);
        DiscoverUtilities.isSessionUserProfileRequest(this,mSessionId,mUsernameProfile,mFollowButton,usernameTextView,"fromUserProfile");
    }

    /*
    JB: setUsernameViews() establece las vistas de la actividad al completo
*/
    private void setUsernameViews() {
        setFollowButton();
        getUserShares();
        getFollowedUsers();
        getUserLikes();
    }

    /*
    JB: getFollowedUsers() realiza la peticion para saber los usuarios seguidos.
    Dependiendo de si el usuario esta en su propio se llama a un servicio o a otro
*/
    public void getFollowedUsers() {

        //JB: Si todavia no se ha realizado la peticion se realiza
        if (!mIsFollowingsSet) {
            RequestQueue requestQueue = new RequestQueueSingleton(getApplicationContext()).getRequestQueue();
            String url = "";
            //TODO URL
            if (!mUsernameProfile.equals("")) {
                url = SERVER + "/users/followed/" + mUsernameProfile;
            } else {
                url = SERVER + "/users/followed/self/" + mSessionId;
            }
            JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    response -> manageFollowedUsersInfo(response.toString()),
                    error -> {
                        findViewById(R.id.userFollowingsEmpty).setVisibility(View.VISIBLE);
                        mIsFollowingsSet = true;
                    }
            );

            //JB: Se establece un tiempo de espera para la respuesta
            getRequest.setRetryPolicy(new DefaultRetryPolicy(
                    15000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            //JB: Se anade la peticion a la cola
            requestQueue.add(getRequest);

        } else {
            setFlipper(1);
        }
    }

    /*
    JB: manageFollowedUsersInfo() gestiona las vistas de la informacion de usuarios seguidos
*/
    private void manageFollowedUsersInfo(String response) {
        ArrayList<String> users = new ArrayList<>();
        try {
            JSONObject reader = new JSONObject(response);
            JSONArray shares = reader.getJSONArray("users");
            for (int i = 0; i < shares.length(); i++) {
                JSONObject user = (JSONObject) shares.get(i);
                String username = user.getString("username");
                users.add(username);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //JB: Llamada a la funcion que los muestra
        showInflatedUsers(users);
    }

    /*
    JB: showInflatedUsers() muestra los usuarios seguidos por el usuario sesion
*/
    private void showInflatedUsers(ArrayList<String> users) {
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout followedUsersLayout = findViewById(R.id.userFollowingsLayout);
        for (int i = 0; i < users.size(); i++) {
            View userCard = inflater.inflate(R.layout.layout_user_card, null);
            ((TextView) ((LinearLayout) userCard).findViewById(R.id.findUserUsername)).setText(users.get(i));
            ((Button) ((LinearLayout) userCard).findViewById(R.id.findUserSeeProfileBtn)).setOnClickListener((View.OnClickListener) v -> goToUserProfileActivity(v));
            followedUsersLayout.addView(userCard);
        }
        mIsFollowingsSet = true;
    }

    /*
    JB: getUserShares() gestiona la peticion para la informacion de las publicaciones del usuario
*/
    private void getUserShares() {
        //JB: Si todavia no se ha realizado la peticion se realiza
        if (!mIsFollowingsSet) {
            RequestQueue requestQueue = new RequestQueueSingleton(getApplicationContext()).getRequestQueue();
            String url = "";
            //TODO URL
            if (!mUsernameProfile.equals("")) {
                url = SERVER + "/shares/user/" + mUsernameProfile;
            } else {
                url = SERVER + "/shares/self/" + mSessionId;
            }

            //JB: Se define la peticion GET
            JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    response -> manageUserSharesInfo(response.toString(),"shares"),
                    error -> {
                        //JB: El usuario no tiene publicaciones
                        //DiscoverUtilities.showAlertDialog(UserProfileActivity.this, R.string.shareInfoError + error.getMessage());
                    }
            );

            //JB: Se establece un tiempo de espera para la respuesta
            getRequest.setRetryPolicy(new DefaultRetryPolicy(
                    15000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            //JB: Se anade la peticion a la cola
            requestQueue.add(getRequest);
        } else {
            setFlipper(0);
        }
    }

    /*
    JB: setFlipper() gestiona el viewflipper de la interfaz
*/
    private void setFlipper(int op) {
        switch (op){
            //JB: Publicaciones
            case 0:
                mFlipper.setDisplayedChild(0);
                mSharesBtn.setEnabled(false);
                mFollowingBtn.setEnabled(true);
                mLikesBtn.setEnabled(true);
                break;
            case 1:
                //JB: Seguimiento
                mFlipper.setDisplayedChild(1);
                mFollowingBtn.setEnabled(false);
                mSharesBtn.setEnabled(true);
                mLikesBtn.setEnabled(true);
                break;
            case 2:
                //JB: Likes
                mFlipper.setDisplayedChild(2);
                mLikesBtn.setEnabled(false);
                mSharesBtn.setEnabled(true);
                mFollowingBtn.setEnabled(true);
                break;
        }
    }

    /*
        JB: manageUserSharesInfo() gestiona la respuesta del servidor de la peticion de publicaciones del usaurio
    */
    private void manageUserSharesInfo(String response,String action) {
        try {
            JSONObject reader = new JSONObject(response);
            JSONArray shares = reader.getJSONArray("shares");
            for (int i = 0; i < shares.length(); i++) {
                JSONObject share = (JSONObject) shares.get(i);
                String videoTitle = share.getString("videoTitle");
                String videoId = share.getString("videoId");
                String comment = share.getJSONObject("comment").getString("text");
                String id = share.getString("id");
                String username = share.getJSONObject("user").getString("username");
                if(action.equals("shares")){
                    mSharesInfo.add(ShareInfo.builder().mShareTitle(videoTitle).mShareVideoId(videoId).mShareComment(comment).mShareId(id).mUsername(username).build());
                }else{
                    mLikesInfo.add(ShareInfo.builder().mShareTitle(videoTitle).mShareVideoId(videoId).mShareComment(comment).mShareId(id).mUsername(username).build());
                }
                inflateVideos(action);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        showInflatedShares(action);
    }

    /*
        JB: inflateVideos() infla un video
    */
    private void inflateVideos(String action) {
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View youtubeCard = inflater.inflate(R.layout.youtube_card, null);
        if(action.equals("shares")){
            mVideoViews.add(youtubeCard);
        }else{
            mLikesViews.add(youtubeCard);
        }

    }

    /*
        JB: showInflatedShares() muestra los videos inflados
    */
    public void showInflatedShares(String action) {
        setRecyclerView(action);
    }

    /*
        JB: showInflatedShares() muestra los videos inflados
    */
    private void setRecyclerView(String action) {

        //JB: Definicion del la vista de la lista Recyclerview y su tamano
        if(action.equals("shares")){
            mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        }else{
            mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view_likes);
        }
        mRecyclerView.setHasFixedSize(true);

        //JB: Definicion del Layout Manager
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //JB: Definicion del adaptador
        if(action.equals("shares")){
            mAdapter = new RecyclerAdapterSelf(mVideoViews, UserProfileActivity.this,action);
        }else{
            mAdapter = new RecyclerAdapterSelf(mLikesViews, UserProfileActivity.this,action);
        }
        mRecyclerView.setAdapter(mAdapter);
    }

    /*
    JB: getmSharesInfo() devuelve la informacion de todas las publicaciones
*/
    public ArrayList<ShareInfo> getmSharesInfo() {
        return mSharesInfo;
    }

    /*
    JB: getmLikesInfo() devuelve la informacion de todos las likes
*/
    public ArrayList<ShareInfo> getmLikesInfo() {
        return mLikesInfo;
    }

    /*
    JB: goToReplyActivity() abre la actividad de VER COMENTARIOS
*/
    public void goToReplyActivity(View v) {
        LinearLayout shareContainer = (LinearLayout) v.getParent().getParent();
        String title = ((TextView) shareContainer.findViewById(R.id.youtubeShareTitle)).getText().toString();
        String videoId = ((TextView) shareContainer.findViewById(R.id.hiddenVideoId)).getText().toString();
        String shareId = ((TextView) shareContainer.findViewById(R.id.hiddenShareId)).getText().toString();
        String comment = ((TextView) shareContainer.findViewById(R.id.youtubeShareComment)).getText().toString();
        String username = ((TextView) shareContainer.findViewById(R.id.hiddenUsername)).getText().toString();
        //
        Intent intent = new Intent(this, ReplyActivity.class);
        intent.putExtra("sessionId", mSessionId);
        intent.putExtra("title", title);
        intent.putExtra("videoId", videoId);
        intent.putExtra("shareId", shareId);
        intent.putExtra("comment", comment);
        intent.putExtra("username", username);
        startActivity(intent);
    }

    /*
    JB: goToArtistProfile() abre la actividad de PERFIL ARTISTA
*/
    public void goToArtistProfile(View v) {
        DiscoverUtilities.goToArtistProfile(this, v);
    }

    /*
    JB: getmSessionId() devuelve la sesion
*/
    public String getmSessionId() {
        return mSessionId;
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
    JB: goToUserProfileActivity() abre la actividad de PERFIL USUARIO
*/
    public void goToUserProfileActivity(View v) {
        LinearLayout container = ((View)(v.getParent()).getParent()).findViewById(R.id.findUserLayout);
        String user = ((TextView) container.findViewById(R.id.findUserUsername)).getText().toString();
        Intent intent = new Intent(this, UserProfileActivity.class);
        intent.putExtra("username", user);
        intent.putExtra("sessionId", mSessionId);
        startActivity(intent);
    }

    /*
        JB: setFollowButton() establece el boton de seguir usuario
    */
    private void setFollowButton() {
        DiscoverUtilities.setFollowButton(mFollowButton,this,mSessionId,mUsernameProfile);
    }

    /*
        JB: manageFollow() gestiona el LIKE en una publicacion
    */
    private void manageFollow() {
        DiscoverUtilities.manageFollow(mFollowButton,this,mSessionId,mUsernameProfile);
    }

    /*
        JB: getUserLikes() realiza la peticion para la informacion de los likes de un usuario
    */
    private void getUserLikes() {
        //JB: Si todavia no se ha realizado la peticion se realiza
        if (!mIsLikesSet) {
            RequestQueue requestQueue = new RequestQueueSingleton(getApplicationContext()).getRequestQueue();
            String url = "";
            //TODO URL
            if (!mUsernameProfile.equals("")) {
                url = SERVER + "/likes/user/" + mUsernameProfile;
            } else {
                url = SERVER + "/likes/self/" + mSessionId;
            }

            //JB: Definicion de la peticion GET
            JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    response -> {
                        manageUserSharesInfo(response.toString(),"likes");
                        mIsLikesSet = true;
                    },
                    error -> {
                        //JB: El usuario no tiene likes
                        //DiscoverUtilities.showAlertDialog(UserProfileActivity.this, R.string.likesError + error.getMessage());
                    }
            );

            //JB: Se establece un tiempo de espera para la respuesta
            getRequest.setRetryPolicy(new DefaultRetryPolicy(
                    15000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            //JB: Se anade la peticion a la cola
            requestQueue.add(getRequest);
        } else {
            setFlipper(2);
        }
    }

    /*
        JB: likeBtnPressed() gestion del boton LIKE
    */
    public void likeBtnPressed(View v) {
        DiscoverUtilities.likeBtnPressed(v,this,mSessionId);
    }
}