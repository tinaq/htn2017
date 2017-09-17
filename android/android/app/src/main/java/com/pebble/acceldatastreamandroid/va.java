package com.pebble.acceldatastreamandroid;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Reimu on 2017-09-16.
 */

public class va {
    JSONArray jsonArray = new JSONArray();

    JSONObject jsonObject;
    String url = "https://ec2-34-214-3-186.us-west-2.compute.amazonaws.com:8080/uber-req/gohome";




    va(JSONObject object){
        jsonObject = object;
        Log.d("aaa",object.toString());
    }
    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
            new Response.Listener<JSONObject>(){
                @Override
                public void onResponse(JSONObject response) {
                    Log.e("Response", response.toString());
                    try {
                        JSONArray arrData = response.getJSONArray("data");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            },
            new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Error.Response", error.toString());
                    String json = null;
                    NetworkResponse response = error.networkResponse;
                    if(response != null && response.data != null){
                        switch(response.statusCode){
                            case 400:

                                json = new String(response.data);
                                System.out.println(json);
                                break;
                        }
                        //Additional cases
                    }
                }
            })
    {
        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            HashMap<String, String> headers = new HashMap<>();
            headers.put("content-type", "application/json");
            headers.put("Accept","application/json");
            Log.d("headers", headers.toString());
            return headers;
        }



    };

}
