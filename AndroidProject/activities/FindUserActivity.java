package jb.dam2.discover.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;

import jb.dam2.discover.discoverUtilities.DiscoverUtilities;
import jb.dam2.discover.R;
import jb.dam2.discover.volley.RequestQueueSingleton;

/*
    //JB: FindUserActivity es la actividad que gestiona la busqueda de otros usuarios y su seguimiento. Ademas es el punto de partida
    previo a ver su perfil en UerProfileActivity
*/
public class FindUserActivity extends AppCompatActivity {

    private String mSessionId;
    private String mFindUser;
    private int mVeces;
    private LinearLayout mFindUserLayout;
    private Button mFollowButton;
    private String SERVER = DiscoverUtilities.getSERVER();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_find_user);

        //JB:Definicion de vistas
        mFindUserLayout = findViewById(R.id.findUserLayout);
        mFollowButton = findViewById(R.id.findUserFollowBtn);
        mFollowButton.setOnClickListener(v -> manageFollow());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.findUser);
        Intent intent = getIntent();
        mSessionId = intent.getStringExtra("sessionId");
        //JB: mVeces es necesario para que la busqueda no se realice 2 veces, un problema interno de Android
        mVeces=1;
    }

/*
    //JB: isSessionUserProfileRequest() Llama a la funcion que verifica si el usuario sesion es el mismo que aquel que se ha buscado
*/
    private void isSessionUserProfileRequest() {
        TextView usernameTextView = (TextView)findViewById(R.id.findUserUsername);
        DiscoverUtilities.isSessionUserProfileRequest(this,mSessionId,mFindUser,mFollowButton,usernameTextView,"fromFindUser");
    }

/*
    //JB: manageFollow() Llama a la funcion que se encarga de gestionar el seguimiento de usuarios
*/
    private void manageFollow() {
        DiscoverUtilities.manageFollow(mFollowButton,this,mSessionId,mFindUser);
    }

    /*
    //JB: onCreateOptionsMenu() Infla el menu de búsqueda
*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    /*
    //JB: onNewIntent() Gestiona nuevas acciones intent. En este caso, el de realizar una busqueda
*/
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    /*
    //JB: handleIntent() Gestiona la barra de busqueda, guardando el termino buscado y dando paso a la funcion que gestiona la busqueda
    La variable mVeces es necesaria, ya que si no, la busqueda se realiza 2 veces. Un problema interno de Android
*/
    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            mFindUser = intent.getStringExtra(SearchManager.QUERY);
            if (mVeces == 1) {
                if (!mFindUser.equals("")) {
                    searchUser();
                }
                mVeces = 0;
            }
        }
        mVeces = 1;
    }

    /*
    //JB: searchUser() realiza la peticion de busqueda del usuario y da paso a las funciones que gestionan la respuesta
*/
    private void searchUser() {
        //JB: Inicializacion de la peticion
        RequestQueue requestQueue = new RequestQueueSingleton(getApplicationContext()).getRequestQueue();

        //JB: Definicion de la URL
        String url = SERVER+"/users/finduser?sessionId="+mSessionId+"&username="+mFindUser;

        //JB: Se establece la peticion GET y sus acciones dependiendo de la respuesta
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    onUserFound();
                    isSessionUserProfileRequest();
                },
                error -> {
                    onUserNotFound();
                }
        );

        //JB: Se establece un tiempo de espera para la respuesta del servidor
        getRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        //JB: Se anade la peticion a la cola de peticiones volley
        requestQueue.add(getRequest);
    }

    /*
    //JB: onUserFound() Muestra las vista correspondientes si el usuario buscado existe
*/
    private void onUserFound(){
        mFindUserLayout.findViewById(R.id.findUserNoUserLayout).setVisibility(View.GONE);
        mFindUserLayout.findViewById(R.id.findUserUserLayout).setVisibility(View.VISIBLE);
        ((TextView)mFindUserLayout.findViewById(R.id.findUserUsername)).setText(mFindUser);
        DiscoverUtilities.setFollowButton(mFollowButton,this,mSessionId,mFindUser);
    }

    /*
    //JB: onUserFound() Muestra las vista correspondientes si el usuario buscado no existe
*/
    private void onUserNotFound(){
        mFindUserLayout.findViewById(R.id.findUserUserLayout).setVisibility(View.GONE);
        mFindUserLayout.findViewById(R.id.findUserNoUserLayout).setVisibility(View.VISIBLE);
    }

    /*
    //JB: goToUserProfileActivity() Abre la actividad de Perfil de Usuario
*/
    public void goToUserProfileActivity(View v){
        Intent intent = new Intent(this, UserProfileActivity.class);
        intent.putExtra("username",mFindUser);
        intent.putExtra("sessionId",mSessionId);
        startActivity(intent);
    }


    /*
    //JB: onSupportNavigateUp() Vuelve a la actividad anterior y elimina esta
*/
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        finish();
        return true;
    }
}
