package ca.mcgill.ecse321.treeple.utils;

import android.annotation.SuppressLint;
import android.content.Context;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

// Network controller using Volley to send HTTP requests to the backend
@SuppressLint("StaticFieldLeak")
public class VolleyController {

    private static VolleyController mInstance;
    private RequestQueue mRequestQueue;
    private Context mContext;

    //Backend URL
    public static final String DEFAULT_BASE_URL = "http://ecse321-11.ece.mcgill.ca:8080/";

    private VolleyController(Context context) {
        mContext = context.getApplicationContext();
        mRequestQueue = getRequestQueue();
    }

    public static synchronized VolleyController getInstance(Context context) {
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

    //Parses response from backend in the case of an error
    public static String parseNetworkResponse(VolleyError volleyError) {
        String backendResponse = "";
        NetworkResponse networkResponse = volleyError.networkResponse;
        if (networkResponse != null && networkResponse.data != null) {
            if (networkResponse.statusCode == 500) {
                String response = new String(networkResponse.data);
                try {
                    JSONObject json = new JSONObject(response);
                    backendResponse = json.getString("message");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return backendResponse;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}
