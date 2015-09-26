package test.app.javier.reignreader;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * A Pseudo-Singleton class that mannage the Requests.
 * //Based in: https://developer.android.com/training/volley/requestqueue.html
 */
public class RequestQueueSingleton {
    private static RequestQueueSingleton requestQueueSingleton;
    private static RequestQueue requestQueue;
    private static Context context;

    private RequestQueueSingleton(Context context){
        this.context=context;
        requestQueue=getRequestQueue();
    }
    public static synchronized void setInstance(Context context){
       requestQueueSingleton=new RequestQueueSingleton(context);
        requestQueueSingleton.getRequestQueue().start();
    }
    public static synchronized RequestQueueSingleton getInstance(){
        if(requestQueueSingleton==null)
            throw new ExceptionInInitializerError();
        return requestQueueSingleton;
    }
    public RequestQueue getRequestQueue(){
        if(requestQueue==null)
            requestQueue= Volley.newRequestQueue(context.getApplicationContext());
        return  requestQueue;
    }
    public <T> void addToRequestQueue(Request<T> request){
        getRequestQueue().add(request);
    }
}
