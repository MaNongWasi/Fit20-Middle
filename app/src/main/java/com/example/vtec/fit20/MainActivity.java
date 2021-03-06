package com.example.vtec.fit20;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.JsonReader;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.engineio.client.transports.WebSocket;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.net.URISyntaxException;

// loopj for RESTful requests
import com.loopj.android.http.*;

import cz.msebera.android.httpclient.Header;

// for crash reports on the package distributer hockeyApp
import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.UpdateManager;

public class MainActivity extends AppCompatActivity {
    protected PowerManager.WakeLock mWakeLock;
    private double refVal = 0.0, memVal = 0.0, timeVal = 0;
    private ProgressBar ref_pb, mem_pb, max_pb, min_pb;
    Drawable pb_normal, pb_slower, pb_slow, pb_fast, pb_faster, pb_min_normal, pb_min_slower, pb_min_slow, pb_min_fast, pb_min_faster, pb_max_normal, pb_max_slower, pb_max_slow, pb_max_fast, pb_max_faster;
    private String state = "", state_pre = "", machineName;
    private TextView time_tv, memname_tv, machinename_tv, cd_tv;
    private TextView memname_qs_tv, machinename_qs_tv, qs_tv, metronome_tv, steady_tv, range_tv;
    private String[] machine_list;
    private SharedPreferences sharedPreferences;
    private int count_down = 0;
    private Socket mSocket;
    private JSONObject jObject, data;
    private String api = "";
    // TODO ? assuming a way to keep track of views via current view
    private String current_view, main_v = "Main", free_v = "Free", qs_v = "QS";
    private myTextView cd_station;

//    int[] pic = new int[]{R.drawable.one, R.drawable.two, R.drawable.three, R.drawable.four, R.drawable.five, R.drawable.six, R.drawable.seven, R.drawable.eight, R.drawable.nine, R.drawable.ten};
//    private int currentRep = 0, totalRep = 0, realVal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "FIT 20");
        this.mWakeLock.acquire();

        NetTool netTool = new NetTool(MainActivity.this);
        api = netTool.network_seg;
        init_val();
//        init_main_ui();
        init_free_ui();
        ip_dialog();
//        init_sockt();

        // from hockeyApp check for updates
        checkForUpdates();

    }

    public void init_main_ui() {
        // Set the first view "main"
        setContentView(R.layout.activity_main);
        current_view = main_v;
        init_ui();
    }

    public void init_val() {
        // getting all the default values from the values>array folder
        machine_list = getResources().getStringArray(R.array.machines);
        //TODO ? what is this doing
        sharedPreferences = getSharedPreferences(Config.SHARED_MACHINE_NAME, Context.MODE_PRIVATE);
        // getString gets string with a machine name else gets Unknown
        machineName = sharedPreferences.getString(Config.SHARED_MACHINE_NAME, Config.TAG_UNKNOWN);
        if (machineName.equals(Config.TAG_UNKNOWN)) {
            // machine Name was not found
            machine_dialog();
        }
    }

    public void init_ui() {
        // Gets all the attributes from the views
        ref_pb = (ProgressBar) findViewById(R.id.ref_pb);
        mem_pb = (ProgressBar) findViewById(R.id.mem_pb);
        time_tv = (TextView) findViewById(R.id.time_tv);
        machinename_tv = (TextView) findViewById(R.id.machine_name);
        memname_tv = (TextView) findViewById(R.id.mem_name);
        cd_tv = (TextView) findViewById(R.id.count_down);
        max_pb = (ProgressBar) findViewById(R.id.cal_max_pb);
        min_pb = (ProgressBar) findViewById(R.id.cal_min_pb);
        cd_station = (myTextView)findViewById(R.id.cd_station);
//        cd_im = (ImageView) findViewById(R.id.cd_im);

        // These are for drawing the bars TODO maybe can look into improving it
        pb_normal = getResources().getDrawable(R.drawable.pb_normal);
        pb_slow = getResources().getDrawable(R.drawable.pb_slow);
        pb_slower = getResources().getDrawable(R.drawable.pb_slower);
        pb_fast = getResources().getDrawable(R.drawable.pb_fast);
        pb_faster = getResources().getDrawable(R.drawable.pb_faster);
        pb_min_normal = getResources().getDrawable(R.drawable.pb_min_normal);
        pb_min_slow = getResources().getDrawable(R.drawable.pb_min_slow);
        pb_min_slower = getResources().getDrawable(R.drawable.pb_min_slower);
        pb_min_fast = getResources().getDrawable(R.drawable.pb_min_fast);
        pb_min_faster = getResources().getDrawable(R.drawable.pb_min_faster);
        pb_max_normal = getResources().getDrawable(R.drawable.pb_max_normal);
        pb_max_slow = getResources().getDrawable(R.drawable.pb_max_slow);
        pb_max_slower = getResources().getDrawable(R.drawable.pb_max_slower);
        pb_max_fast = getResources().getDrawable(R.drawable.pb_max_fast);
        pb_max_faster = getResources().getDrawable(R.drawable.pb_max_faster);

        // setting machineName on view
        machinename_tv.setText(machineName);
        // Listens for a click to change it ? TODO: not this, or not here maybe in the intial page.
        machinename_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                password_dialog();
            }
        });

        cd_tv.setVisibility(View.INVISIBLE);
        cd_station.setVisibility(View.INVISIBLE);
//        cd_im.setVisibility(View.INVISIBLE);
    }

    private void init_qs_ui() {
        setContentView(R.layout.activity_qscore);
        current_view = qs_v;
        memname_qs_tv = (TextView) findViewById(R.id.mem_name);
        machinename_qs_tv = (TextView) findViewById(R.id.machine_name);
        qs_tv = (TextView) findViewById(R.id.qs_total);
        metronome_tv = (TextView) findViewById(R.id.metronome);
        steady_tv = (TextView) findViewById(R.id.steady);
        range_tv = (TextView) findViewById(R.id.range);
        // TODO change here for qs studio and machines
        getQualityScore(1,1);

    }


    public void init_free_ui() {
        setContentView(R.layout.activity_free);
        current_view = free_v;
//        free_im = (ImageView) findViewById(R.id.free_im);
//        free_im.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setContentView(R.layout.activity_main);
//                init_main_ui();
//                init_sockt();
//            }
//        });
    }

    // websocket options
    IO.Options createOptions() {
        IO.Options opts = new IO.Options();
        opts.forceNew = true;
        return opts;
    }

    private void init_sockt() {
        // Initialize sockets
        try {
            // set options for the socket to ensure websocket
            IO.Options opts = createOptions();
            opts.transports = new String[] {WebSocket.NAME};

//          mSocket = IO.socket("http://192.168.0.127:8001/livedata");
            System.out.println("final api " + api);
            String url = "http://" + api + ":8001/livedata";
            // start socket at the url and with options
            mSocket = IO.socket(url, opts);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        mSocket.connect();
        mSocket.on("connect", onConnectMsg);
        mSocket.on(Socket.EVENT_CONNECT, onConnect);// 连接成功
        mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);// 断开连接
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);// 连接异常
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectTimeoutError);// 连接超时
        mSocket.on("live_data", onEventRecieved);
    }


    public void machine_dialog() {
        // shows a dialog for machine name setting , installation procedure
        final Dialog dialog = new Dialog(MainActivity.this);
        // set the content to a dialog view
        dialog.setContentView(R.layout.dialog);
        dialog.setTitle(Config.MACHINE_NAME);
        // bind the ok and cancel button
        Button ok_bt = (Button) dialog.findViewById(R.id.ok);
        Button cancel_bt = (Button) dialog.findViewById(R.id.cancel);
        // Content.. this is machine name..
        final AutoCompleteTextView machine_et = (AutoCompleteTextView) dialog.findViewById(R.id.input_et);
        // get the array of choices, here machine_list is the list of machines and display it
        // simple_list_item_1 is the layout view element
        ArrayAdapter machine_adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, machine_list);

        machine_et.setAdapter(machine_adapter);
        machine_et.setThreshold(0); //input 0 character to display hint

        // add on click listener to set and close dialog
        ok_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // set the Machine name
                machineName = machine_et.getText().toString();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(Config.SHARED_MACHINE_NAME, machineName);
                editor.apply();
                editor.commit();
                dialog.dismiss();
            }
        });
        cancel_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    // Shows a dialog for inputing your ip address
    public void ip_dialog() {
        // custom dialog set for IP address handling
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog);
        dialog.setTitle("IP Address");

        // content , bind the button
        Button ok_bt = (Button) dialog.findViewById(R.id.ok);
        Button cancel_bt = (Button) dialog.findViewById(R.id.cancel);

        // TODO ? autocomplete a ip ?
        final AutoCompleteTextView ip_et = (AutoCompleteTextView) dialog.findViewById(R.id.input_et);
        ip_et.setInputType(InputType.TYPE_CLASS_NUMBER);
        ok_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO ? error handling what if ip address is wrong ?
                api += ip_et.getText().toString();
                dialog.dismiss();
                init_sockt();
            }
        });
        cancel_bt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void password_dialog() {
        // TODO: A secret password dialog for changing machine ? is needed?
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog);
        dialog.setTitle(Config.PASSWORD);
        Button ok_bt = (Button) dialog.findViewById(R.id.ok);
        Button cancel_bt = (Button) dialog.findViewById(R.id.cancel);
        final AutoCompleteTextView pw_et = (AutoCompleteTextView) dialog.findViewById(R.id.input_et);
        pw_et.setTransformationMethod(PasswordTransformationMethod.getInstance());
        ok_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pw_et.getText().toString().equals(Config.PW)) {
                    dialog.dismiss();
                    machine_dialog();
                } else {
                    dialog.dismiss();
                    Toast.makeText(MainActivity.this, "Password incorrect. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        cancel_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    @Override
    protected void onResume() {
        super.onResume();
        // hockeymanager for app logs
        checkForCrashes();

    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterManagers();
    }

    @Override
    public void onDestroy() {
        // executed in the end of the view/app
        System.out.println("Closing Application");
        this.mWakeLock.release();
        disconnect_socket();
        super.onDestroy();
        // run hockeymanager
        unregisterManagers();

    }

    private Emitter.Listener onConnectMsg = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            //TODO Emmitter ? Sockets.
            System.out.println("Emitter invoked");
            System.out.println("onConnect msg : " + args);
        }
    };

    // This is the websocket listener for live data
    private Emitter.Listener onEventRecieved = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            // TODO? debug? prints all arguments
            for (int i = 0; i < args.length; i++) {
                System.out.println("received msg : " + i + " " + args[i]);
            }
            try {
                // capture json object
                jObject = (JSONObject) args[0];
                // just captures data array from the json list
                data = jObject.getJSONObject(Config.TAG_DATA);
                // checks for state in the data
                state = data.getString(Config.TAG_STATE);
                // print states {IDLE, START, STATIONARY, STOP}
                System.out.println("state " + state);
                // if stopped and view is the idle view
                if (state.equals(Config.TAG_IDLE) && current_view.equals(main_v)) {
                    Message msg = new Message();
                    msg.what = STOP;
                    handler.sendMessage(msg);
                }
                // if exercise is not started yet but has been created or has been started
                else if (state.equals(Config.TAG_INIT) || state.equals(Config.TAG_START) || state.equals(Config.TAG_RUNNING)) {
                    // get countdown
                    count_down = data.getInt(Config.TAG_CD);
                    // check if main view and go to start
                    if (!current_view.equals(main_v)) {
                        Message msg = new Message();
                        msg.what = START;
                        handler.sendMessage(msg);
                    }

                    if (count_down > 0) {
                        Message msg = new Message();
                        msg.what = COUNT_DOWN_START;
                        handler.sendMessage(msg);
                    } else {
                        Message msg = new Message();
                        msg.what = UPDATE;
                        msg.obj = data;
                        handler.sendMessage(msg);
                    }
                } else if (state.equals(Config.TAG_STATION)) {
                    count_down = data.getInt(Config.TAG_CD);
                    Message msg = new Message();
                    msg.what = COUNT_DOWN_STATION;
                    msg.obj = data;
                    handler.sendMessage(msg);
                } else if (state.equals(Config.TAG_STOP)){
                    Message msg = new Message();
                    msg.what = STOP;
                    handler.sendMessage(msg);
                }

                // see if exercise is complete
                else if(state.equals(Config.TAG_COMPLETE)) {
                    Message msg = new Message();
                    msg.what = IDLE;
                    handler.sendMessage(msg);
                    System.out.print("Exercise Complete");
                }

            } catch (JSONException e) {
                e.printStackTrace();
                if (!current_view.equals(free_v)) {
                    Message msg = new Message();
                    msg.what = STOP;
                    handler.sendMessage(msg);
                }
            }
        }

    };


    // constant denoting the states of the machine
    private static final int COUNT_DOWN_START = 0;
    private static final int COUNT_DOWN_STATION = 1;
    private static final int START = 2;
    private static final int UPDATE = 3;
    private static final int STOP = 4;
    private static final int ERROR = 5;
    private static final int IDLE = 6;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // check for Message containing states defined above
            if (msg.what == COUNT_DOWN_START) {
                time_tv.setVisibility(View.INVISIBLE);
                cd_tv.setVisibility(View.VISIBLE);
                cd_tv.setText(String.valueOf(count_down));
            } else if (msg.what == COUNT_DOWN_STATION) {
                cd_station.setVisibility(View.VISIBLE);
//                cd_im.setVisibility(View.VISIBLE);
                data = (JSONObject) msg.obj;
                cd_station.setText(String.valueOf(count_down));
                cd_station.invalidate();
//                cd_im.setImageResource(pic[count_down-1]);
                time_tv.setText(Config.CD);
                time_tv.setTextColor(getResources().getColor(R.color.fitgrey));
                time_tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 80);
                handle_data(data, COUNT_DOWN_STATION);
            } else if (msg.what == START) {
                init_main_ui();
            } else if (msg.what == UPDATE) {
                time_tv.setVisibility(View.VISIBLE);
                cd_tv.setVisibility(View.INVISIBLE);
                cd_station.setVisibility(View.INVISIBLE);
//                cd_im.setVisibility(View.INVISIBLE);

                data = (JSONObject) msg.obj;
                handle_data(data , UPDATE);

            } else if (msg.what == IDLE) {
                init_free_ui();
            }else if (msg.what == STOP) {
                init_qs_ui();
                // TODO check for real machine id and studio CHANGE THIS ASAP
                getQualityScore(1,1);
            } else if (msg.what == ERROR) {
                String toast_str = (String) msg.obj;
                Toast.makeText(MainActivity.this, toast_str, Toast.LENGTH_LONG).show();
                init_free_ui();
            }
        }
    };


    public void handle_data(JSONObject data, int state) {
        // Progress bar handler
        try {
            refVal = data.getDouble(Config.TAG_REF_VAL) * Config.BASE_VAL;
            timeVal = data.getDouble(Config.TAG_REAL_TIME);
            memVal = data.getDouble(Config.TAG_MEM_VAL) * Config.BASE_VAL;
            System.out.println("mem " + data.getDouble(Config.TAG_MEM_VAL));
            System.out.println("realVal " + data.getDouble(Config.TAG_REAL_VAL));
            System.out.println("refVal " + data.getDouble(Config.TAG_REF_VAL));
            System.out.println("handler data  " + state);
            if (state == UPDATE){
                time_tv.setText(String.valueOf((int) (timeVal)));  //update 1, stationary 0
            }

            ref_pb.setProgress((int) refVal);

            if (memVal > Config.MAX) {
                mem_pb.setProgress(1000);
                max_pb.setProgress((int) (memVal - Config.MAX));
                min_pb.setProgress(0);
            } else if (memVal < Config.MIN) {
                mem_pb.setProgress(0);
                max_pb.setProgress(0);
                min_pb.setProgress((int) Math.abs(memVal));
            } else {
                mem_pb.setProgress((int) memVal);
                max_pb.setProgress(0);
                min_pb.setProgress(0);
            }

            if (memVal > Config.MIN && memVal < Config.MAX) {
                if (memVal - refVal <= Config.TAG_TOO_SLOW) {
                    //too slow < -300
                    mem_pb.setProgressDrawable(pb_slower);
                    max_pb.setProgressDrawable(pb_max_slower);
                    min_pb.setProgressDrawable(pb_min_slower);
                } else if (memVal - refVal < Config.TAG_SLOW && memVal - refVal >= Config.TAG_TOO_SLOW) {
                    //slow -100 ~ -300
                    mem_pb.setProgressDrawable(pb_slow);
                    max_pb.setProgressDrawable(pb_max_slow);
                    min_pb.setProgressDrawable(pb_min_slow);
                } else if (memVal - refVal > Config.TAG_FAST && memVal - refVal <= Config.TAG_TOO_FAST) {
                    //fast 100 ~ 300
                    mem_pb.setProgressDrawable(pb_fast);
                    max_pb.setProgressDrawable(pb_max_fast);
                    min_pb.setProgressDrawable(pb_min_fast);
                } else if (memVal - refVal >= Config.TAG_TOO_FAST) {
                    //too fast >300
                    mem_pb.setProgressDrawable(pb_faster);
                    max_pb.setProgressDrawable(pb_max_faster);
                    min_pb.setProgressDrawable(pb_min_faster);
                } else {
                    //normal
                    mem_pb.setProgressDrawable(pb_normal);
                    max_pb.setProgressDrawable(pb_max_normal);
                    min_pb.setProgressDrawable(pb_min_normal);
                }
            }else{
                mem_pb.setProgressDrawable(pb_faster);
                max_pb.setProgressDrawable(pb_max_faster);
                min_pb.setProgressDrawable(pb_min_faster);
            }

        } catch (JSONException e) {
            e.printStackTrace();

        }
    }

    public void disconnect_socket() {
        // disconnect socket connection
        mSocket.disconnect();
        mSocket.off(Socket.EVENT_CONNECT, onConnect);
        mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectTimeoutError);
        mSocket.off("live_data", onEventRecieved);
    }

 // Emmitters are event handlers for socket specific events
    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            System.out.println("disconnected ");
//            isConnected = false;
        }
    };

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

//            if (!isConnected) {
//                try {
//                    JSONObject jsonObject = new JSONObject();
//                    jsonObject.put( "userName", "wang" );
            System.out.println("connect Successful ");
//                } catch ( JSONException e ) {
//                    e.printStackTrace();
//                }
//                isConnected = true;
//            }
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            System.out.println("Connection failed" + args[0]);
            Message msg = new Message();
            msg.what = ERROR;
            msg.obj = "Can not Connect to gateway";
            handler.sendMessage(msg);
        }
    };

    private Emitter.Listener onConnectTimeoutError = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            System.out.println("connection timeout" + args[0]);

        }
    };

    // method to get quality score data

    public void setQualityScore(JSONArray exercise_data){
        try {
            // extract the data out
            String exerciseID = (exercise_data.getJSONObject(1)).getString("exercise");
            String qualityScore = (exercise_data.getJSONObject(2)).getString("qs");
            String metronomeVal = (exercise_data.getJSONObject(3)).getString("metronome");
            String steadyVal = (exercise_data.getJSONObject(4)).getString("steady");
            String rangeVal = (exercise_data.getJSONObject(5)).getString("range");
            metronome_tv.setText(metronomeVal);
            qs_tv.setText(qualityScore);
            steady_tv.setText(steadyVal);
            range_tv.setText(rangeVal);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getQualityScore(int studio,int machine){
        // TODO where to get studio and machine from
        String gatewayURL = "http://"+api+":8001/fit20/v1.0/studios/"+studio+"/machines/"+machine+"/members/1/userdata";
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(gatewayURL, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                setQualityScore(response);
//                Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();
//                System.out.print(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(getApplicationContext(), "Error showing quality score", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // hockey app methods for distributions

    private void checkForCrashes() {
        CrashManager.register(this);
    }

    private void checkForUpdates() {
        // Remove this for store builds!
        UpdateManager.register(this);
    }

    private void unregisterManagers() {
        UpdateManager.unregister();
    }


}



