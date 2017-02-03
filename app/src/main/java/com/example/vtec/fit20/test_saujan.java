package com.example.vtec.fit20;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.loopj.android.http.*;

import cz.msebera.android.httpclient.Header;


public class test_saujan extends AppCompatActivity {

    public static final int REQUEST_CODE = 100;
    public static final int MACHINE_ID = 1;
    public static final int STUDIO_ID = 1;
    public static final int MEMBER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_saujan);
//        setContentView(R.layout.activity_qscore);
        // bind a view button to this activity
        Button btnTest = (Button) findViewById(R.id.test_btn);
        // bind a btn, check if exists
        if (btnTest != null) {
            // set a event listener
            btnTest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // creating intent to go to next activity and view, get the context ? and the class you wanna launch
                    Intent intent = new Intent(getApplicationContext(), QualityScoreActivity.class);

                    // passing data
                    intent.putExtra("machine-id", MACHINE_ID);
                    intent.putExtra("studio-id", STUDIO_ID);
                    intent.putExtra("member-id", MEMBER_ID);


                    // startActivity takes teh intent object
                    startActivityForResult(intent, REQUEST_CODE);
                }
            });
        }

        // bind a view button to this activity
        Button btnGetJSONData = (Button) findViewById(R.id.get_data_btn);
        // bind a btn, check if exists
        if (btnGetJSONData != null) {
            // set a event listener
            btnGetJSONData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                getQualityScore(STUDIO_ID, MACHINE_ID);
                }
            });
        }
    }

    public void getQualityScore(int studio,int machine){
        String gatewayURL = "http://192.168.0.109:8001/fit20/v1.0/studios/"+studio+"/machines/"+machine;
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(gatewayURL, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode==200){
                    String result = new String(responseBody);
                    Toast.makeText(test_saujan.this, "API OK:" + result, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(test_saujan.this, "API ERROR!", Toast.LENGTH_LONG).show();
            }
        });
    }

}
