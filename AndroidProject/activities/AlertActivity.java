package jb.dam2.discover.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
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
import jb.dam2.discover.pojo.AlertInfo;
import jb.dam2.discover.pojo.CustomReplyAlertWrapper;
import jb.dam2.discover.recycler.RecyclerAdapterAlerts;

/*
    JB:AlertActivity es la actividad que gestiona las notificaciones de un usuario
*/
public class AlertActivity extends AppCompatActivity {

    private ArrayList<View> mAlertViews;
    private ArrayList<AlertInfo> mAlertsInfoArray;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private String SERVER = DiscoverUtilities.getSERVER();
    private String mSessionId;
    private String mAlertResponseInfo;
    private ArrayList<String> mAlertsNowSeen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alerts);

        //JB: Definicion de vistas
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.notifications));
        Intent intent = getIntent();
        mSessionId = intent.getStringExtra("sessionId");
        mAlertResponseInfo = intent.getStringExtra("alertsResponse");
        mAlertViews = new ArrayList<>();
        mAlertsInfoArray = new ArrayList<>();
        mAlertsNowSeen = new ArrayList<>();

        //JB: Gestion de respuestas
        manageRepliesInfo();
    }

    /*
    JB:getAlertsInfoArray devuelve la informacion de las alertas
*/
    public ArrayList<AlertInfo> getAlertsInfoArray(){
        return mAlertsInfoArray;
    }

    /*
   JB:updateSeenAlerts() Realiza la peticion al servidor para actualizar las vistas y marcarlas como VISTAS
*/
    private void updateSeenAlerts(){

        //JB: Inicializacion de la peticion
        RequestQueue requestQueue = new RequestQueueSingleton(getApplicationContext()).getRequestQueue();
        CustomReplyAlertWrapper customReplyAlertWrapper = new CustomReplyAlertWrapper(mAlertsNowSeen);
        JSONArray jsonArray = new JSONArray();

        //JB: Creacion del objeto JSON
        JSONObject object = new JSONObject();
        for (String id:mAlertsNowSeen) {
            jsonArray.put(id);
        }
        try {
            object.put("replyAlertIds",jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //JB: Definicion de la URL
        String url = SERVER+"/replyAlerts/update";

        //JB: Definicion de la peticion POST
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, object,
                response -> {
                }, error -> {
        });

        //JB: Se establece un tiempo de espera para la respuesta
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonObjectRequest);
    }

    /*
    JB:manageRepliesInfo gestiona la informacion recibida por el servidor para la informacion de alertas
*/
    private void manageRepliesInfo() {

        try {
            JSONObject reader = new JSONObject(mAlertResponseInfo);
            JSONArray replyAlerts = reader.getJSONArray("replyAlerts");
            for (int i=0;i<replyAlerts.length();i++){
                JSONObject reply = (JSONObject) replyAlerts.get(i);
                String id = reply.getString("id");
                JSONObject share = reply.getJSONObject("share");
                String shareId = share.getString("id");
                String shareTitle = share.getString("videoTitle");
                String userReplied = reply.getJSONObject("userReplied").getString("username");
                String repliedText = reply.getString("repliedText");
                boolean isSeen = reply.getBoolean("seen");
                if(!isSeen){
                    mAlertsNowSeen.add(id);
                }
                mAlertsInfoArray.add(AlertInfo.builder().mId(id).mShareId(shareId).mShareTitle(shareTitle).mUsernameReplied(userReplied).isSeen(isSeen).mRepliedText(repliedText).build());
                inflateAlerts();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //JB: muestra la lista Recyclerview de alertas
        showInflatedAlerts();
    }

    /*
    JB:showInflatedAlerts() muestra la lista Recyclerview de alertas
*/
    private void showInflatedAlerts() {

        //JB: Definicion del la vista de la lista Recyclerview y su tamano
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        //JB: Definicion del Layout Manager
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //JB: Definicion del adaptador
        mAdapter = new RecyclerAdapterAlerts(mAlertViews, AlertActivity.this);
        mRecyclerView.setAdapter(mAdapter);

        //actualiza las vistas para marcarlas como VISTAS
        updateSeenAlerts();
    }

    /*
    JB:inflateAlerts() Infla una alerta
*/
    private void inflateAlerts(){
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View alertLayout = inflater.inflate(R.layout.layout_alert, null);
        mAlertViews.add(alertLayout);
    }

    /*
    JB:goToReplyActivity() Abre la actividad de comentarios de una publicacion
*/
    public void goToReplyActivity(View v){
        LinearLayout alertContainer = (LinearLayout) v.getParent();
        String shareId = ((TextView)alertContainer.findViewById(R.id.alertHiddenShareId)).getText().toString();
        //
        Intent intent = new Intent(this, ReplyActivity.class);
        intent.putExtra("sessionId", mSessionId);
        intent.putExtra("shareId",shareId);
        startActivity(intent);
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