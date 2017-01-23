package com.example.vtec.fit20;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.TransactionTooLargeException;
import android.widget.TextView;

//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.StringRequest;
//import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

/**
 * Created by Cecylia on 12/10/2016.
 */
public class QScoreActivity extends Activity{
    private TextView memname_tv, machinename_tv, qs_tv, metronome_tv, steady_tv, range_tv;
    protected PowerManager.WakeLock mWakeLock;
//    private RequestQueue requestQueue;
//    private String apiHost = "http://192.168.0.138:8001";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qscore);

        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "FIT 20");
        this.mWakeLock.acquire();

        init_ui();
//        requestQueue = Volley.newRequestQueue(getApplicationContext());

//        get_QS();

//        handler.postDelayed(runnable, 60000);
    }

    private void init_ui(){
        memname_tv = (TextView)findViewById(R.id.mem_name);
        machinename_tv = (TextView)findViewById(R.id.machine_name);
        qs_tv = (TextView)findViewById(R.id.qs_total);
        metronome_tv = (TextView)findViewById(R.id.metronome);
        steady_tv = (TextView)findViewById(R.id.steady);
        range_tv = (TextView)findViewById(R.id.range);
    }

//    private void get_QS() {
//        StringRequest request = new StringRequest(Request.Method.GET, apiHost + Config.url_get_qscore, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
////                System.out.println("response -> " + response);
//                try {
//                    JSONArray jsonArray = new JSONArray(response);
//                    JSONObject jObject = null;
//                    jObject = jsonArray.getJSONObject(0);
//                    if (jObject.getString(Config.TAG_STATUS).equals(Config.TAG_SUCCESS)) {
//                        jObject = jsonArray.getJSONObject(1);
//                        qs_tv.setText(jObject.getString(Config.TAG_QS));
//                        metronome_tv.setText(jObject.getString(Config.TAG_MET));
//                        range_tv.setText(jObject.getString(Config.TAG_RANGE));
//                        steady_tv.setText(jObject.getString(Config.TAG_STEADY));
//                    }else{// if(jsonArray.getJSONObject(0).names().get(0).toString().equals(Config.TAG_STATUS)){
//                        System.out.println("Device not Connected");
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                System.out.println(error.getMessage() + " " + error);
//            }
//        });
//        requestQueue.add(request);
//    }


    private Handler handler = new Handler();

    private Runnable runnable = new Runnable() {
        public void run() {
            Intent freeIntent = new Intent(QScoreActivity.this, FreeActivity.class);
            startActivity(freeIntent);
            QScoreActivity.this.finish();
        }
    };
}
