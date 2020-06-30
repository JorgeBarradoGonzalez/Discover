package jb.dam2.discover.volley;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/*
    JB:RequestQueueSingleton Es una clase que gestiona el Singleton de la cola de peticions Volley
*/
public class RequestQueueSingleton {

    private static RequestQueueSingleton requestQueueSingleton;
    private RequestQueue requestQueue;
    private static Context context;

    public RequestQueueSingleton(Context ctx){
        context = ctx;
        requestQueue = getRequestQueue();
    }

    /*
    JB:getInstance() Devuelve el singleton de la cola de peticiones Volley
*/
    public static synchronized  RequestQueueSingleton getInstance(Context context){
        if (requestQueueSingleton == null){
            requestQueueSingleton = new RequestQueueSingleton(context);
        }
        return requestQueueSingleton;
    }

    /*
    JB:getInstance() Devuelve la cola de peticiones Volley
*/
    public RequestQueue getRequestQueue() {
        if (requestQueue == null){
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

}
