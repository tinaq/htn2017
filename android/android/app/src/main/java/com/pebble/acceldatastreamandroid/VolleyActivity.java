package com.pebble.acceldatastreamandroid;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
/**
 * Created by Reimu on 2017-09-16.
 */

public class VolleyActivity {

    String url = "https://ec2-34-214-3-186.us-west-2.compute.amazonaws.com:8080/uber-req/gohome";
    StringRequest postRequest = new StringRequest(Request.Method.POST, url,
            new Response.Listener<String>()
            {
                @Override
                public void onResponse(String response) {
                    // response
                    Log.d("Response", response);
                }
            },
            new Response.ErrorListener()
            {
                @Override
                public void onErrorResponse(VolleyError error ) {
                    // error
                    Log.d("Error.Response", error.toString());
                }
            }
    ) {
        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String,String> headers=new HashMap<String,String>();
            headers.put("Accept","application/json");
            headers.put("Content-Type","application/json");
            return headers;
        }
        @Override
        protected Map<String, String> getParams()
        {
            Map<String, String>  params = new HashMap<String, String>();
            params.put("latitude", "43.472285");
            params.put("longitude", "-80.544858");
            Log.d("a","sending things");
            return params;
        }
    };


}
