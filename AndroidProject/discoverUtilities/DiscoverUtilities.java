package jb.dam2.discover.discoverUtilities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import jb.dam2.discover.R;
import jb.dam2.discover.activities.ArtistProfileActivity;
import jb.dam2.discover.volley.RequestQueueSingleton;

/*
    JB: DiscoverUtilities es la clase abstracta que contiene los metodos que se repiten varias veces en la aplicacion
*/
public abstract class DiscoverUtilities {

    private final static String SERVER = 

    /*
    JB: createLongToast() crea un Toast con informacion
*/
    public static void createLongToast(Context context,String message){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    /*
    JB: goToArtistProfile() Abre la aplicacion de PERFIL de ARTISTA
*/
    public static void goToArtistProfile(Context context, View v){
        String artistName = getShareArtistName(v);
        Intent intent = new Intent(context, ArtistProfileActivity.class);
        intent.putExtra("artistName",artistName);
        context.startActivity(intent);
    }

    /*
    JB: getShareArtistName() Recoge el nombre de un artista en una publicacion
*/
    private static String getShareArtistName(View view){
        LinearLayout linearLayout = (LinearLayout) view.getParent().getParent().getParent();
        TextView textView = ((TextView) linearLayout.findViewById(R.id.youtubeShareTitle));
        String videoTitle = textView.getText().toString();
        String artist = videoTitle.split("-")[0];
        Log.d("artist","artist name: "+artist);
        return artist;
    }

    /*
    JB: getSERVER() Devuelve la IP del servidor
*/
    public static String getSERVER(){
        return SERVER;
    }

    /*
    JB: manageLike() Realiza la peticion POST que modifica el like en una publicacion
*/
    public static void manageLike(String sessionId,String shareId, LinearLayout selectedShare,Activity activity) {

        //JB: Inicializacion de la peticion
        RequestQueue requestQueue = new RequestQueueSingleton(activity).getRequestQueue();

        //JB: Definicion del objeto JSON
        JSONObject likeInfo = new JSONObject();
        try {
            likeInfo.put("sessionId",sessionId);
            likeInfo.put("shareId",shareId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //JB: Definicion del URL
        String url = DiscoverUtilities.getSERVER()+"/likes/isLiked?sessionId="+sessionId+"&shareId="+shareId;

        //JB: Definicion de la peticion GET y paso a la gestion de la respuesta
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, likeInfo,
                response -> manageLikeResponse(response.toString(),selectedShare,activity),
                error -> DiscoverUtilities.createLongToast(activity,"LIKE ERROR")
        );

        //JB: Se establece un tiempo de espera para la respuesta
        getRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        //JB: Se anade la peticion al servidor
        requestQueue.add(getRequest);
    }

    /*
    JB: manageLikeResponse() Gestiona la respuesta de una publicacion
*/
    private static void manageLikeResponse(String response,LinearLayout selectedShare,Activity activity) {
        try {
            JSONObject reader = new JSONObject(response);
            String isLikedInfo = reader.getString("isLiked");
            boolean isLiked;

            if(isLikedInfo.equals("false")){
                isLiked = false;
            }else{
                isLiked = true;
            }

            //muestra la respuesta en la interfaz
            displayRecentLike(isLiked,selectedShare,activity);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*
    JB: displayRecentLike() Muestra la respuesta del proceso de LIKE
*/
    private static void displayRecentLike(boolean isLiked,LinearLayout selectedShare,Activity activity) {
        activity.runOnUiThread(() -> {
            if(isLiked){
                ((ImageButton)selectedShare.findViewById(R.id.youtubeCardLikeBtn)).setImageResource(R.drawable.ic_favorite_red_24dp);
            }
        });
    }

    public static void setFollowButton(Button followButton,Activity activity,String sessionId,String username) {
        //JB: Inicializacion de la peticion
        RequestQueue requestQueue = new RequestQueueSingleton(activity).getRequestQueue();

        //JB: Se indica la url del servicio al que se va a hacer la llamada
        String url = SERVER+"/users/finduser?sessionId="+sessionId+"&username="+username;

        //JB: Se define que la peticion es POST, se inserta el Objeto JSON creado y gestion de la respuesta
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> manageSearchedUser(response.toString(),followButton),
                error -> DiscoverUtilities.createLongToast(activity,"FOLLOW ERROR")
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
    JB: manageSearchedUser() Gestion de la respuesta del servidor para la busqueda de un usuario
*/
    private static void manageSearchedUser(String response,Button followButton) {
        try {
            JSONObject reader = new JSONObject(response);
            String followingInfo = reader.getString("isFollowing");
            boolean isFollowing;

            //JB: Si devuelve FOLLOW es que el usuario no esta seguido. Si es UNFOLLOW es que ya se sigue
            if(followingInfo.equals("follow")){
                isFollowing = false;
            }else{
                isFollowing = true;
            }

            //JB: Mostrado de la informacion en la interfaz
            displayUserInfo(isFollowing,followButton);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*
    JB: displayUserInfo() Mostrado de la informacion de seguimiento de usuario en la interfaz
*/
    private static void displayUserInfo(boolean isFollowing,Button followButton){
        if(isFollowing){
            ((Button)followButton).setText("UNFOLLOW");
        }else{
            ((Button)followButton).setText("FOLLOW");
        }
    }

    /*
    JB: manageFollow() Gestiona la pulsacion del boton Follow segun se siga o no al usuario
*/
    public static void manageFollow(Button followButton,Activity activity,String sessionId,String username) {
        String followStatus = followButton.getText().toString();
        if(followStatus.equals("UNFOLLOW")){
            unfollowUser(followButton,activity,sessionId,username);
        }else{
            followUser(followButton,activity,sessionId,username);
        }
    }

    /*
    JB: followUser() Es la peticion que se realiza si se pulsa SEGUIR USUARIO y todavia no se le sigue
*/
    private static void followUser(Button followButton,Activity activity,String sessionId,String username) {

        //JB: Inicializacion de la peticion
        RequestQueue requestQueue = new RequestQueueSingleton(activity).getRequestQueue();

        //JB: Definicion del objeto JSON
        JSONObject followInfo = new JSONObject();
        try {
            followInfo.put("sessionId",sessionId);
            followInfo.put("username",username);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //JB: Se indica la url del servicio al que se va a hacer la llamada
        String url = SERVER+"/follow";

        //JB: Definicion de la peticion POST y gestion de la respuesta
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST, url, followInfo,
                response -> followButton.setText("UNFOLLOW"),
                error -> DiscoverUtilities.createLongToast(activity,"ERROR FOLLOWING")
        );

        //JB: Se indica un tiempo de espera para la respuesta del servidor
        getRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        //JB: Se anade la peticion a la cola de peticiones de volley
        requestQueue.add(getRequest);
    }

    /*
   JB: unfollowUser() Es la peticion que se realiza si se pulsa SEGUIR USUARIO y ya se sigue al usuario
*/
    private static void unfollowUser(Button followButton,Activity activity,String sessionId,String username) {

        //JB: Inicializacion de la peticion
        RequestQueue requestQueue = new RequestQueueSingleton(activity).getRequestQueue();

        //JB: Se indica la url del servicio al que se va a hacer la llamada
        String url = SERVER+"/unfollow";

        //JB: Definicion de la peticion POST y gestion de la respuesta
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.DELETE, url, null, (JSONObject response) -> {
            followButton.setText("FOLLOW");
        }, error -> DiscoverUtilities.createLongToast(activity,"ERROR UNFOLLOWING")) {

            //Header necesario ya que Volley no permite ejecutar una peticion DELETE
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("sessionId", sessionId);
                headers.put("username", username);
                return headers;
            }
        };

        //JB: Se indica un tiempo de espera para la respuesta del servidor
        getRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        //JB: Se anade la peticion a la cola de peticiones de volley
        requestQueue.add(getRequest);
    }

    /*
   JB: showAlertDialog()Muestra un dialogo de alerta
*/
    public static void showAlertDialog(Activity activity,String text){
        activity.runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(R.string.warning);
            builder.setMessage(text);
            builder.setPositiveButton(R.string.accept, null);
            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }

    /*
   JB: isSessionUserProfileRequest() Realiza la peticion que se encarga de averiguar si se ha pedido ver un perfil
   que es el del usuario del dispositivo
*/
    public static void isSessionUserProfileRequest(Activity activity,String sessionId,String username,Button followButton,TextView usernameText,String action) {

        //JB: Inicializacion de la peticion
        RequestQueue requestQueue = new RequestQueueSingleton(activity).getRequestQueue();

        //JB: Se indica la url del servicio al que se va a hacer la llamada
        String url = SERVER + "/users/isUser?sessionId="+sessionId+"&username="+username;

        //JB: Se define que la peticion GET y se gestiona la respuesta
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> manageIsUserInfo(response.toString(),username,followButton,usernameText,action),
                error -> DiscoverUtilities.createLongToast(activity,"isUSER ERROR")
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
       JB: manageIsUserInfo() Gestiona la respuesta sobre si el usuario que del que se pide informacion es el mismo que el del dispositivo
*/
    private static void manageIsUserInfo(String response,String searchedUsername, Button followButton,TextView usernameView,String action) {
        try {
            JSONObject reader = new JSONObject(response);
            String isUserInfo = reader.getString("isUser");
            if(isUserInfo.equals("false")){
                usernameView.setText(searchedUsername);
            }else{
                followButton.setVisibility(View.INVISIBLE);
                if(action.equals("fromUserProfile")){
                    usernameView.setText(R.string.myProfile);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*
       JB: likeBtnPressed() Comienza la gestion del pulsado del boton LIKE
*/
    public static void likeBtnPressed(View v,Activity activity, String mSessionId){
        LinearLayout shareContainer = (LinearLayout) v.getParent().getParent().getParent();
        String shareId = ((TextView)shareContainer.findViewById(R.id.hiddenShareId)).getText().toString();
        manageLike(shareId,shareContainer,activity,mSessionId);
    }

    /*
       JB: manageLike() Realiza la peticion para ejecutar la accion de marcar como LIKE o eliminar de LIKES
    */
    private static void manageLike(String shareId,LinearLayout selectedShare,Activity activity,String mSessionId) {

        //JB: Inicializacion de la peticion
        RequestQueue requestQueue = new RequestQueueSingleton(activity).getRequestQueue();

        //JB: Creacion del objeto JSON e inserción de campos
        JSONObject likeInfo = new JSONObject();
        try {
            likeInfo.put("sessionId",mSessionId);
            likeInfo.put("shareId",shareId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //JB: Se indica la url del servicio al que se va a hacer la llamada
        String url = SERVER+"/likes/like";

        //JB: Se define que la peticion POST y se gestiona la respuesta
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST, url, likeInfo,
                response -> manageLikeResponse(response.toString(),selectedShare),
                error -> DiscoverUtilities.createLongToast(activity,"ERROR LIKE")
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
       JB: manageLikeResponse() Gestiona la respuesta del servidor sobre si el like se a anadido o eliminado
    */
    private static void manageLikeResponse(String response,LinearLayout selectedShare) {
        try {
            JSONObject reader = new JSONObject(response);
            String isLikedInfo = reader.getString("isLiked");
            boolean isLiked;

            if(isLikedInfo.equals("false")){
                isLiked = false;
            }else{
                isLiked = true;
            }
            displayRecentLike(isLiked,selectedShare);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*
       JB: displayRecentLike() Muestra el resultado en la interfaz
    */
    private static void displayRecentLike(boolean isLiked,LinearLayout selectedShare) {
        if(isLiked){
            ((ImageButton)selectedShare.findViewById(R.id.youtubeCardLikeBtn)).setImageResource(R.drawable.ic_favorite_white_24dp);
        }else{
            ((ImageButton)selectedShare.findViewById(R.id.youtubeCardLikeBtn)).setImageResource(R.drawable.ic_favorite_red_24dp);
        }
    }

}
