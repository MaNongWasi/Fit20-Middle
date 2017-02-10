package com.example.vtec.fit20;

import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.github.nkzawa.engineio.client.Transport;
import com.github.nkzawa.engineio.client.transports.WebSocket;
import com.github.nkzawa.socketio.client.Manager;
import com.loopj.android.http.*;

//import org.atmosphere.wasync.Client;
//import org.atmosphere.wasync.ClientFactory;
//import org.atmosphere.wasync.OptionsBuilder;
//import org.atmosphere.wasync.Request;
//import org.atmosphere.wasync.Function;
//import org.atmosphere.wasync.RequestBuilder;
//import org.atmosphere.wasync.Socket;
//import org.atmosphere.wasync.Event;
//import org.atmosphere.wasync.Decoder;
//import org.atmosphere.wasync.Encoder;

//import cz.msebera.android.httpclient.Header;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import static android.R.attr.data;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class test_saujan extends AppCompatActivity {
    private JSONObject jObject;
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
//                getQualityScore(STUDIO_ID, MACHINE_ID);
                    testWebSocket();
                }
            });
        }
    }


    public void setQualityScore(JSONArray exercise_data){
        try {
            // extract the data out
            String exerciseID = (exercise_data.getJSONObject(1)).getString("exercise");
            String qualityScore = (exercise_data.getJSONObject(2)).getString("qs");
            String metronomeVal = (exercise_data.getJSONObject(3)).getString("metronome");
            String steadyVal = (exercise_data.getJSONObject(4)).getString("steady");
            String rangeVal = (exercise_data.getJSONObject(5)).getString("range");

            Log.i("Test", qualityScore);
            Log.i("Test", metronomeVal);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
//
//    public void getQualityScore(int studio,int machine){
//        String gatewayURL = "http://192.168.0.134:8001/fit20/v1.0/studios/"+studio+"/machines/"+machine+"/members/1/userdata";
//        AsyncHttpClient client = new AsyncHttpClient();
//        client.get(gatewayURL, new JsonHttpResponseHandler() {
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
//                setQualityScore(response);
//                Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();
//                System.out.print(response);
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                super.onFailure(statusCode, headers, responseString, throwable);
//                Toast.makeText(getApplicationContext(), "We got an error", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

//    public void getQualityScore(int studio,int machine){
//        String gatewayURL = "http://192.168.0.109:8001/fit20/v1.0/studios/"+studio+"/machines/"+machine;
//        AsyncHttpClient client = new AsyncHttpClient();
//        client.get(gatewayURL, new AsyncHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//                if (statusCode==200){
//                    String result = new String(responseBody);
//                    Toast.makeText(test_saujan.this, "API OK:" + result, Toast.LENGTH_LONG).show();
//                }
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//                Toast.makeText(test_saujan.this, "API ERROR!", Toast.LENGTH_LONG).show();
//            }
//        });
//    }


    // socket
    private String serverIpAddress = "http://192.168.0.134:8001";
//    private String serverIpAddress = "http://192.168.0.134:8001/livedata";
    public static final String EVENT_CONNECT = "connect";

    // socket test
    public void testWebSocket(){
//
//        // Will start websockets here
//        // get a Default client from wasync, works with all framework of websocket
//        try{
//        Client client = ClientFactory.getDefault().newClient();
//
//        RequestBuilder request = client.newRequestBuilder()
//                .method(Request.METHOD.GET)
//                .uri(serverIpAddress)
////                .transport(Request.TRANSPORT.WEBSOCKET)
//                .transport(Request.TRANSPORT.LONG_POLLING);
//
////        OptionsBuilder build = client.newOptionsBuilder();
////        build.requestTimeoutInSeconds(2);
//
//        Socket socket_test = client.create();
//        socket_test.on(Event.OPEN, new Function<String>() {
//            @Override
//            public void on(String t){
//                System.out.println("Socket connected");
//            }
//        }).open(request.build());
//        } catch (Throwable e){
////            e.printStackTrace();
//        }



        // Socket IO  Here ****************************

        try {
            IO.Options opts = createOptions();
            opts.transports = new String[] {WebSocket.NAME};
            Socket test_socket = IO.socket(serverIpAddress, opts);
            test_socket.on(Socket.EVENT_CONNECT, onConnectMsg);
//            test_socket.on(Manager.EVENT_TRANSPORT, new Emitter.Listener() {
//                @Override
//                public void call(Object... args) {
//                    Transport transport = (Transport)args[0];
//                    transport.on(Transport.EVENT_REQUEST_HEADERS, new Emitter.Listener() {
//                        @Override
//                        public void call(Object... args) {
//                            @SuppressWarnings("unchecked")
//                            Map<String, List<String>> headers = (Map<String, List<String>>)args[0];
//                            headers.put("X-SocketIO", Arrays.asList("hi"));
//                        }
//                    }).on(Transport.EVENT_RESPONSE_HEADERS, new Emitter.Listener() {
//                        @Override
//                        public void call(Object... args) {
//                            @SuppressWarnings("unchecked")
//                            Map<String, List<String>> headers = (Map<String, List<String>>)args[0];
//                            List<String> value = headers.get("X-SocketIO");
//                            values.offer(value != null ? value.get(0) : "");
//                        }
//                    });
//                }
//            });
            test_socket.connect();
            test_socket.close();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

        IO.Options createOptions() {
            IO.Options opts = new IO.Options();
            opts.forceNew = true;
            return opts;
        }


    private Emitter.Listener onConnectMsg = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            //TODO Emmitter ? Sockets.
            System.out.println("Emitter invoked");
            System.out.println("onConnect msg : " + args);
        }
    };

}
