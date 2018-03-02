package ca.mcgill.ecse321.treeple;

import android.annotation.SuppressLint;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

@SuppressLint("StaticFieldLeak")
class VolleyController {

    private static VolleyController mInstance;
    private RequestQueue mRequestQueue;
    private static Context mContext;

    static final String DEFAULT_BASE_URL = "http://00ff2874.ngrok.io/";
//    static final String DEFAULT_BASE_URL = "http://19cd40eb.ngrok.io/";

    private VolleyController(Context context) {
        mContext = context;
        mRequestQueue = getRequestQueue();
    }

    static synchronized VolleyController getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new VolleyController(context);
        }
        return mInstance;
    }

    private RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mRequestQueue;
    }

    <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}
