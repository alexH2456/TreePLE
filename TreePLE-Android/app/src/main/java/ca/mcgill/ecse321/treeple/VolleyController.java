package ca.mcgill.ecse321.treeple;

import android.annotation.SuppressLint;
import android.content.Context;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

@SuppressLint("StaticFieldLeak")
class VolleyController {

    private static VolleyController mInstance;
    private RequestQueue mRequestQueue;
    private static Context mContext;

    static final String DEFAULT_BASE_URL = "http://ecse321-11.ece.mcgill.ca:8080/";

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

class InputStreamVolleyRequest extends Request<byte[]> {

    private final Response.Listener<byte[]> mListener;
    private Map<String, String> mParams;
    public Map<String, String> responseHeaders;

    public InputStreamVolleyRequest(int method, String mUrl, Response.Listener<byte[]> listener, Response.ErrorListener errorListener, HashMap<String, String> params) {
        super(method, mUrl, errorListener);
        setShouldCache(false);
        mListener = listener;
        mParams = params;
    }

    @Override
    protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
        return mParams;
    }

    @Override
    protected void deliverResponse(byte[] response) {
        mListener.onResponse(response);
    }

    @Override
    protected Response<byte[]> parseNetworkResponse(NetworkResponse response) {
        responseHeaders = response.headers;
        return Response.success(response.data, HttpHeaderParser.parseCacheHeaders(response));
    }

}
