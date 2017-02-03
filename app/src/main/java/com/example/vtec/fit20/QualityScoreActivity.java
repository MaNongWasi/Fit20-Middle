package com.example.vtec.fit20;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class QualityScoreActivity extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Toast.makeText(QualityScoreActivity.this, "WOOHOO", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qscore);

        // grab data from the intent creating activity
        int machineID = getIntent().getIntExtra("machine-id",0);
        int studioID = getIntent().getIntExtra("studio-id",0);
        int memberID = getIntent().getIntExtra("member-id",0);

        // grab and set textviews
        TextView metronome = (TextView) findViewById(R.id.metronome);
        TextView steady = (TextView) findViewById(R.id.steady);
        TextView range = (TextView) findViewById(R.id.range);
        TextView qs_total = (TextView) findViewById(R.id.qs_total);
        metronome.setText(""+machineID);
        steady.setText(""+machineID);
        range.setText(""+machineID);
        qs_total.setText(""+machineID);

        // send results back via setResult
//        Intent intent = new Intent();
//        intent.putExtra("result", "success");
//        setResult(RESULT_OK, intent);
//        // closing this activity and go back to previous activity
//        finish();

    }

    public void getData(){

    }
}
