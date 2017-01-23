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
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

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

    }

    public void init_main_ui() {
        setContentView(R.layout.activity_main);
        current_view = main_v;
        init_ui();
    }

    public void init_val() {
        machine_list = getResources().getStringArray(R.array.machines);
        sharedPreferences = getSharedPreferences(Config.SHARED_MACHINE_NAME, Context.MODE_PRIVATE);
        machineName = sharedPreferences.getString(Config.SHARED_MACHINE_NAME, Config.TAG_UNKNOWN);
        if (machineName.equals(Config.TAG_UNKNOWN)) {
            machine_dialog();
        }
    }

    public void init_ui() {
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

        machinename_tv.setText(machineName);

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


    private void init_sockt() {
        try {
//            mSocket = IO.socket("http://192.168.0.127:8001/livedata");
            System.out.println("fianl api " + api);
            String url = "http://" + api + ":8001/livedata";
            mSocket = IO.socket(url);

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
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog);
        dialog.setTitle(Config.MACHINE_NAME);
        Button ok_bt = (Button) dialog.findViewById(R.id.ok);
        Button cancel_bt = (Button) dialog.findViewById(R.id.cancel);
        final AutoCompleteTextView machine_et = (AutoCompleteTextView) dialog.findViewById(R.id.input_et);
        ArrayAdapter machine_adapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, machine_list);
        machine_et.setAdapter(machine_adapter);
        machine_et.setThreshold(0); //input 0 character to display hint
        ok_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                machineName = machine_et.getText().toString();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(Config.SHARED_MACHINE_NAME, machineName);
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

    public void ip_dialog() {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog);
        dialog.setTitle("IP Address");
        Button ok_bt = (Button) dialog.findViewById(R.id.ok);
        Button cancel_bt = (Button) dialog.findViewById(R.id.cancel);
        final AutoCompleteTextView ip_et = (AutoCompleteTextView) dialog.findViewById(R.id.input_et);
        ip_et.setInputType(InputType.TYPE_CLASS_NUMBER);
        ok_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    Toast.makeText(MainActivity.this, "Password incorrect. Please try again!!!", Toast.LENGTH_SHORT).show();
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
    }

    @Override
    public void onDestroy() {
        System.out.println("destory");
        this.mWakeLock.release();
        disconnect_socket();
        super.onDestroy();
    }

    private Emitter.Listener onConnectMsg = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            System.out.println("Emitter invoked");

            System.out.println("onConnect msg : " + args);
        }
    };

    private Emitter.Listener onEventRecieved = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

//
            for (int i = 0; i < args.length; i++) {
                System.out.println("received masg : " + i + " " + args[i]);
            }
            try {
                jObject = (JSONObject) args[0];
                data = jObject.getJSONObject(Config.TAG_DATA);
                state = data.getString(Config.TAG_STATE);
                System.out.println("state " + state);
                if (state.equals(Config.TAG_STOP) && current_view.equals(main_v)) {
                    Message msg = new Message();
                    msg.what = STOP;
                    handler.sendMessage(msg);

                } else if (state.equals(Config.TAG_START)) {
                    count_down = data.getInt(Config.TAG_CD);

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


    private static final int COUNT_DOWN_START = 0;
    private static final int COUNT_DOWN_STATION = 1;
    private static final int START = 2;
    private static final int UPDATE = 3;
    private static final int STOP = 4;
    private static final int ERROR = 5;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
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


            } else if (msg.what == STOP) {
                init_free_ui();
            } else if (msg.what == ERROR) {
                String toast_str = (String) msg.obj;
                Toast.makeText(MainActivity.this, toast_str, Toast.LENGTH_LONG);
                init_free_ui();
            }
        }
    };

    public void handle_data(JSONObject data, int state) {
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
        mSocket.disconnect();
        mSocket.off(Socket.EVENT_CONNECT, onConnect);
        mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectTimeoutError);
        mSocket.off("live_data", onEventRecieved);
    }


    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            System.out.println("disconnect ");
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

}



