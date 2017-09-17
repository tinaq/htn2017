package com.pebble.acceldatastreamandroid;

import android.support.v7.app.ActionBar;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import org.json.JSONObject;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.UUID;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();

    // UUID must match that of the watchapp
    private static final UUID APP_UUID = UUID.fromString("bb039a8e-f72f-43fc-85dc-fd2516c7f328");

    private static final int SAMPLES_PER_UPDATE = 5;   // Must match the watchapp value
    private static final int ELEMENTS_PER_PACKAGE = 3;

    private ActionBar mActionBar;
    private EditText shots;
    int numShots;
    int taken= 0;

    private PebbleKit.PebbleDataReceiver mDataReceiver;
    RequestQueue queue;
    public static class NukeSSLCerts {
        protected static final String TAG = "NukeSSLCerts";

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
        mActionBar.setTitle("AccelDataStream");

        shots = (EditText) findViewById(R.id.shots);
        RelativeLayout rl = (RelativeLayout)findViewById(R.id.outside);
        rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numShots = Integer.parseInt(shots.getText().toString());
                shots.setText(Integer.toString(numShots));
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
                if(taken>numShots) {
                    callUber();
                }
            }

        };
        PebbleKit.registerReceivedDataHandler(getApplicationContext(), mDataReceiver);
    }

    protected void callUber(){
        Log.i("i","exceeded");
//        double lat = 43.472285;
//        double lon = -80.544858;
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("latitude", "43.472285");
        params.put("longitude", "-80.544858");

        JSONObject jsonObject = new JSONObject(params);
        va va = new va(jsonObject);
        queue.add(va.jsonObjectRequest);



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
}
