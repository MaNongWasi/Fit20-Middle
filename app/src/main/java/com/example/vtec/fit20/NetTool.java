package com.example.vtec.fit20;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by VTEC on 1/11/2017.
 */
public class NetTool {
    private Context context;
    public String local_IP, network_seg;
//    private int ip_index;
//    private volatile List<String> ip_list = new ArrayList<>();
//    private Runtime runtime = Runtime.getRuntime();
    private WifiManager mWifiManager;
    private WifiInfo mWifiInfo;
//    private List<ScanResult> mWifiList;
//    private List<WifiConfiguration> MwifiConfig;
//    WifiManager.WifiLock mWifiLock;
//    private String ping = "ping -c 1 -w 0.5 " ;//-c count -w response time
//    private int j;
//    private Runtime run = Runtime.getRuntime();
//    private Process proc = null;

    public NetTool(Context context){
        this.context = context;

        mWifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        mWifiInfo = mWifiManager.getConnectionInfo();

//        mWifiManager.startScan();
//        mWifiList = mWifiManager.getScanResults();

        //set local ip
        String ip = getLocalIP().toString();
        local_IP = ip.substring(1, ip.length());
        network_seg = this.local_IP.substring(0, this.local_IP.lastIndexOf(".")+1);
        System.out.println("local IP " + local_IP);
    }

    //get local IP
    public InetAddress getLocalIP(){
        int hostAddress = mWifiInfo.getIpAddress();
        byte[] addressBytes = {(byte)(0xff & hostAddress), (byte)(0xff & (hostAddress >> 8)),
                (byte)(0xff & (hostAddress >> 16)),
                (byte)(0xff & (hostAddress >> 24))};
        try{
            return InetAddress.getByAddress(addressBytes);
        }catch(UnknownHostException e){
            throw new AssertionError();
        }
    }

}
