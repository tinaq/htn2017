package com.example.reimu.drinkmypebblecompanionapp.MainActivity;

import android.graphics.Color;
import android.os.Vibrator;
import android.support.v7.app.ActionBar;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.reimu.drinkmypebblecompanionapp.R;
import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import static com.pebble.acceldatastreamandroid.R.id.eta;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();

    // UUID must match that of the watchapp
    private static final UUID APP_UUID = UUID.fromString("bb039a8e-f72f-43fc-85dc-fd2516c7f328");


    private ActionBar mActionBar;
    private EditText shots;
    int numShots;
    int taken= 0;
    TextView cur;
    RelativeLayout rl;
    boolean called = false;

    private PebbleKit.PebbleDataReceiver mDataReceiver;
    RequestQueue queue;
    public class va {
        JSONArray jsonArray = new JSONArray();

        JSONObject jsonObject;
        String url = "https://ec2-34-214-3-186.us-west-2.compute.amazonaws.com:8080/uber-req/gohome";
        String url2 = "https://ec2-34-214-3-186.us-west-2.compute.amazonaws.com:8080/uber-req/ride-eta";

        JsonObjectRequest jsonObjectRequest;
        StringRequest getRequest;
        int eta;


        va(JSONObject object){
            jsonObject = object;
            jsonObjectRequest =  new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                    new Response.Listener<JSONObject>(){
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.e("Response", "aaa" + response.toString());
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
                                        System.out.println("400"+ json);
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
                    //   headers.put("content-type", "application/json");
                    headers.put("Accept","application/json");
                    Log.d("headers", headers.toString());
                    return headers;
                }



            };
            getRequest = new StringRequest(Request.Method.GET, url2,
                    new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response) {
                            // display response
                            Log.d("Response", "hhhhhhhhhhhh "+response);
                            int eta = Integer.parseInt(response);
                            setUberText(eta);
                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("Error.Response", error.toString());
                        }
                    }
            )
            {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    //   headers.put("content-type", "application/json");
                    // headers.put("Accept","application/json");
                    Log.d("headers", headers.toString());
                    return headers;
                }



            };;

        }

    }
    public static class NukeSSLCerts {

        public static void nuke() {
            try {
                TrustManager[] trustAllCerts = new TrustManager[] {
                        new X509TrustManager() {
                            public X509Certificate[] getAcceptedIssuers() {
                                X509Certificate[] myTrustedAnchors = new X509Certificate[0];
                                return myTrustedAnchors;
                            }

                            @Override
                            public void checkClientTrusted(X509Certificate[] certs, String authType) {}

                            @Override
                            public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                        }
                };

                SSLContext sc = SSLContext.getInstance("SSL");
                sc.init(null, trustAllCerts, new SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String arg0, SSLSession arg1) {
                        return true;
                    }
                });
            } catch (Exception e) {
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        queue =  Volley.newRequestQueue(this);
        NukeSSLCerts nuke = new NukeSSLCerts();
        nuke.nuke();

        mActionBar = getSupportActionBar();
        mActionBar.setTitle("Drink My Pebble");
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);

        shots = (EditText) findViewById(R.id.shots);
       rl = (RelativeLayout)findViewById(R.id.outside);
        rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numShots = Integer.parseInt(shots.getText().toString());
                shots.setText(Integer.toString(numShots));
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(rl.getWindowToken(), 0);
                shots.setFocusable(false);
                animate(shots);




            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        mDataReceiver = new PebbleKit.PebbleDataReceiver(APP_UUID) {

            @Override
            public void receiveData(Context context, int transactionId, PebbleDictionary data) {
                PebbleKit.sendAckToPebble(context, transactionId);
                taken++;
                updateText(cur);
                if(taken>=numShots&&!called) {
                    callUber();
                }
            }

        };
        PebbleKit.registerReceivedDataHandler(getApplicationContext(), mDataReceiver);
    }

    protected void callUber() {
        called = true;
        Log.i("i", "exceeded");
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("latitude", "43.472285");
        params.put("longitude", "-80.544858");

        JSONObject jsonObject = new JSONObject(params);
        final va va = new va(jsonObject);

        queue.add(va.jsonObjectRequest);
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (Exception e) {
        }
        queue.add(va.getRequest);
    }
    void setUberText(int eta){
        TextView tv = (TextView) findViewById(R.id.uber);
        tv.setText("Calling an Uber");
        TextView etaText = (TextView) findViewById(R.id.eta);
        etaText.setText("Arriving in " + eta + " min");
    }
    @Override
    protected void onPause() {
        super.onPause();

        if(mDataReceiver != null) {
            try {
                unregisterReceiver(mDataReceiver);
                mDataReceiver = null;
            } catch (Exception e) {
                Log.e(TAG, "Error unregistering receiver: " + e.getLocalizedMessage());
                e.printStackTrace();
            }
        }
    }


    public void animate (View view) {
        Animation a = AnimationUtils.loadAnimation(this, R.anim.targetanimation);
        a.setRepeatCount(-1);
        a.setInterpolator(new AccelerateInterpolator());
        a.setAnimationListener(new Animation.AnimationListener(){

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cur = new TextView(MainActivity.this);
                cur.setText(Integer.toString(taken));
                cur.setTextSize(200);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
                params.setMargins(0,80,0,0);
                cur.setLayoutParams(params);
                rl.addView(cur);
                updateText(cur);
                TextView t = new TextView(MainActivity.this);
                t.setText("Current");
                t.setTextSize(40);
                t.setTextColor(Color.parseColor("#999999"));
                RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params2.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
                params2.setMargins(0,750,0,0);
                rl.addView(t,params2);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        view.setAnimation(a);
    }
    public void updateText(TextView t){
        t.setText(Integer.toString(taken));
        double percent = (double)taken/numShots;

        if(percent<=0.4){
            t.setTextColor(Color.parseColor("#00cc66"));
        }
        else if(percent<0.8){
            t.setTextColor(Color.parseColor("#ffff66"));
        }
        else{
            t.setTextColor(Color.parseColor("#FF4d4d"));
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(1000);
        }
    }
}
