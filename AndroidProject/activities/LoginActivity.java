package jb.dam2.discover.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ViewSwitcher;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jb.dam2.discover.discoverUtilities.DiscoverUtilities;
import jb.dam2.discover.R;
import jb.dam2.discover.volley.RequestQueueSingleton;


public class LoginActivity extends AppCompatActivity {


    private ViewSwitcher mViewSwitcher;
    //SIGN-IN
    private String mUsernameSignin;
    private String mPasswordSignin;
    private EditText mEdUsernameSignin;
    private EditText mEdPasswordSignin;
    private Button mSigninBtn;
    private Button goToSignupBtn;
    //SIGN-UP
    private String mUsernameSignup;
    private String mPasswordSignup;
    private String mEmailSignup;
    private EditText mEdUsernameSignup;
    private EditText mEdPasswordSignup;
    private EditText mEdEmailSignup;
    private Button mSignUpBtnSignup;
    private String SERVER = DiscoverUtilities.getSERVER();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /*JB:
            Las siguientes lineas recogen si existe una sesion iniciada previamente buscando el nombre del usuario que la inicio en el SharePreferences de la aplicacion.
            Dependiendo de si existe la sesión o no, se muestra la actividad de Login o se va directamente a la actividad principal.
         */

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String savedSessionId = preferences.getString("sessionId","noSession");
        if(savedSessionId.equals("noSession")){
            setViews();
        }else{
            mUsernameSignin = preferences.getString("username","");
            goToMainActivity(savedSessionId);
        }
    }

    /*JB:
       setViews() carga las vista de registro e inicio de sesión si se ha confirmado que no hay una sesión iniciada previamente en la aplicación.
    */
    private void setViews() {
        mViewSwitcher = findViewById(R.id.signinSwitcher);
        loadSignInViews();
        loadSignUpViews();
    }

    /*JB:
        goToSignUp() muestra la interfaz de Registro dentro del viewSwitcher que compone InicioSesion y Registro
    */
    private void goToSignUp() {
        mViewSwitcher.showNext();
    }

    /*JB:
        signIn() se encarga de verificar que los campos están rellenos y seguidamente envía una petición de inicio de sesión.
    */
    private void signIn() {

        if (!mUsernameSignin.equals("") && !mPasswordSignin.equals("")) {

            //JB: Inicializacion de la peticion
            RequestQueue requestQueue = new RequestQueueSingleton(getApplicationContext()).getRequestQueue();

            //JB: Creacion del objeto JSON e inserción de campos
            JSONObject object = new JSONObject();

            try {
                object.put("username", mUsernameSignin);
                object.put("password", mPasswordSignin);
                //no es necesario enviar el email pero se necesita la estructura del pojo
                object.put("email", "");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //JB: Se indica la url del servicio al que se va a hacer la llamada
            String url = SERVER+"/signin";

            //JB: Se define que la peticion es POST, se inserta el Objeto JSON creado anteriormente y segun la respuesta recibida por parte del servidor se activan diferentes procesos
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, object,
                    response -> {
                        //JB: Si ha habido respuesta, es decir, si se ha conseguido iniciar sesion se guarda en responseString y se gestiona
                        String responseString = response.toString();
                        try {
                            JSONObject jsonObject = new JSONObject(responseString);
                            String sessionId = jsonObject.getString("id");

                            //JB: se guarda la sesion en el dispositivo
                            saveSessionId(sessionId);

                            //JB: Se dirije al usuario a la actividad principal
                            goToMainActivity(sessionId);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, error -> {
                //JB: En error en la peticion se muestra un dialogo indicandolo
                DiscoverUtilities.showAlertDialog(this,getResources().getString(R.string.usernameOrPasswordIncorrect));
            });

            //JB: Se indica un tiempo de espera para la respuesta del servidor
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    15000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            //JB: Se anade la peticion a la cola de peticiones de volley
            requestQueue.add(jsonObjectRequest);
        } else {
            DiscoverUtilities.showAlertDialog(this,getResources().getString(R.string.fillFields));
        }
    }

    /*JB:
        goToMainActivity() se encarga de ir a la actividad principal, anadiendo al Intent la informacion relevante, como el usuario que ha iniciado sesion.
    */
    private void goToMainActivity(String id) {

        //JB: Se carga el intent con la informacion de Session y nombre de usuario
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("sessionId",id);
        intent.putExtra("username",mUsernameSignin);

        //JB: Se guarda la sesion recibida por el servidor en el almacenamiento privado de la aplicacion
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String savedSessionId = preferences.getString("sessionId","noSession");

        //JB: Se inicia la actividad y se cierra la actual para evitar un RETURN
        startActivity(intent);
        finish();
    }

    /*JB:
        signUp() se encarga de realizar la peticion de registro
    */
    private void signUp() {

        if (!mUsernameSignup.equals("") && !mPasswordSignup.equals("")) {

            //JB: Inicializacion de la peticion
            RequestQueue requestQueue = new RequestQueueSingleton(getApplicationContext()).getRequestQueue();

            //JB: Creacion del objeto JSON e inserción de campos
            JSONObject object = new JSONObject();
            try {
                object.put("username", mUsernameSignup);
                object.put("password", mPasswordSignup);
                //no es necesario enviar el email pero se necesita la estructura del pojo
                object.put("email", mEmailSignup);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //JB: Se indica la url del servicio al que se va a hacer la llamada
            String url = SERVER+"/signup";

            //JB: Se define que la peticion es POST, se inserta el Objeto JSON creado anteriormente y segun la respuesta recibida por parte del servidor se activan diferentes procesos
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, object,
                    response -> {
                        //JB: Ante un registro con exito, se muestra la interfaz de inicio de sesion
                        mViewSwitcher.showPrevious();
                        DiscoverUtilities.showAlertDialog(this,getString(R.string.signUpOK));
                    }, error -> {
                //JB: Si la peticion ha ido mal se muestra
                DiscoverUtilities.showAlertDialog(this,getResources().getString(R.string.usernameExists));
            });

            //JB: Se indica un tiempo de espera para la respuesta del servidor
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    15000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            //JB: Se anade la peticion a la cola de peticiones de volley
            requestQueue.add(jsonObjectRequest);
        } else {
            DiscoverUtilities.showAlertDialog(this,getResources().getString(R.string.fillFields));
        }

    }

    /*JB:
        gatherInfoSignup() Recoge la informacion de los editText del registro
    */
    private void gatherInfoSignup() {
        mUsernameSignup = mEdUsernameSignup.getText().toString();
        mPasswordSignup = mEdPasswordSignup.getText().toString();
        mEmailSignup = mEdEmailSignup.getText().toString();
    }

    /*JB:
        gatherInfoSignin() Recoge la informacion de los editText del inicio de sesion
    */
    private void gatherInfoSignin() {
        mUsernameSignin = mEdUsernameSignin.getText().toString();
        mPasswordSignin = mEdPasswordSignin.getText().toString();
    }

    /*JB:
        onBackPressed() vuelve a la interfaz de inicio de sesion si el usuario se encuentra en la interfaz de registro
    */
    @Override
    public void onBackPressed() {
        if (mViewSwitcher.getDisplayedChild() == 1) {
            mViewSwitcher.showPrevious();
        }
    }

    /*JB:
        loadSignUpViews() Carga las vistas de registro en caso de que el usuario no tenga sesion iniciada
    */
    private void loadSignUpViews(){

        mUsernameSignup = "";
        mPasswordSignup = "";
        mEmailSignup = "";
        mEdUsernameSignup = findViewById(R.id.edUsernameSignup);
        mEdUsernameSignup.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if(!isFieldValid(mEdUsernameSignup.getText().toString())){
                    mEdUsernameSignup.setText("");
                    DiscoverUtilities.showAlertDialog(this,getResources().getString(R.string.usernameLength));
                }
            }
        });
        mEdPasswordSignup = findViewById(R.id.edPasswordSignup);
        mEdPasswordSignup.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if(!isFieldValid(mEdPasswordSignup.getText().toString())){
                    mEdPasswordSignup.setText("");
                    DiscoverUtilities.showAlertDialog(this,getResources().getString(R.string.passwordLength));
                }
            }
        });
        mEdEmailSignup = findViewById(R.id.edEmailSignup);
        mEdEmailSignup.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if(mEdEmailSignup.getText().toString().length()>50){
                    mEdEmailSignup.setText("");
                    DiscoverUtilities.showAlertDialog(this,getResources().getString(R.string.emailLength));
                }
            }
        });
        mSignUpBtnSignup = findViewById(R.id.signupBtn);
        mSignUpBtnSignup.setOnClickListener(v -> {
            gatherInfoSignup();
            if(!mUsernameSignup.equals("")||!mPasswordSignup.equals("")||!mEdEmailSignup.equals("")){
                if(Patterns.EMAIL_ADDRESS.matcher(mEmailSignup).matches()){
                    signUp();
                }else{
                    DiscoverUtilities.showAlertDialog(this,getResources().getString(R.string.enterValidEmail));
                }
            }else{
                DiscoverUtilities.showAlertDialog(this,getResources().getString(R.string.fillFields));
            }

        });
        goToSignupBtn = findViewById(R.id.goToSignupBtn);
        goToSignupBtn.setOnClickListener(v -> {
            goToSignUp();
        });
    }

    /*JB:
        loadSignInViews() Carga las vistas de inicio de sesion en caso de que el usuario no tenga sesion iniciada
    */
    private void loadSignInViews() {
        //SIGN-IN
        mUsernameSignin = "";
        mPasswordSignin = "";
        mEdUsernameSignin = findViewById(R.id.edUsernameSignin);
        mEdPasswordSignin = findViewById(R.id.edPasswordSignin);
        mSigninBtn = findViewById(R.id.signinBtn);
        mSigninBtn.setOnClickListener(v -> {
            gatherInfoSignin();
            signIn();
        });
    }

    /*JB:
        isFieldValid() Verifica que la informacion recogida sea acorde a las especificaciones de estructura de la base de datos
    */
    private boolean isFieldValid(String field){
        boolean matches = false;
        Pattern p = Pattern.compile("^.{6,20}$");
        Matcher m = p.matcher(field);
        if(m.matches()){
            matches=true;
        }
        return matches;
    }

    /*JB:
        saveSessionId() Guarda la sesion recibida por el servidor en el almacenamiento interno de la aplicacion
    */
    private void saveSessionId(String sessionId){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.edit().putString("sessionId",sessionId).commit();
        preferences.edit().putString("username",mUsernameSignin).commit();
    }
}
