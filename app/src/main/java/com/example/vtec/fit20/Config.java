package com.example.vtec.fit20;

/**
 * Created by VTEC on 12/9/2016.
 */
public class Config {
    //TODO many are not static do we need it?
    public static final String url_apiHost = "/api/fit20/1.0/";
    public static final String url_get_data = url_apiHost + "sessionmanager/Lat%20Pull%20Down";
    public static final String url_get_qscore = url_apiHost + "qualityscore/Lat%20Pull%20Down";
    public static final String TAG_STATUS = "status";
    public static final String TAG_DATA = "data";
    public static final String TAG_SUCCESS = "success";
    public static final String TAG_ERROR = "error";
    public static final String TAG_MEM_VAL = "memberValue";
    public static final String TAG_REF_VAL = "referenceValue";
//    public static final String TAG_CUR_REP = "CurrentRep";
//    public static final String TAG_TOTAL_REP = "TotalReps";
    public static final String TAG_REAL_VAL = "realValue";
    public static final String TAG_REAL_TIME = "timeElapsed";
    public static final String TAG_CD = "countdown";
    public static final String TAG_CAL_MAX = "calibration_max";
    public static final String TAG_CAL_MIN = "calibration_min";
    public static final String TAG_STATE = "state";
    public static final String TAG_IDLE = "IDLE";
    public static final String TAG_INIT = "INIT";
    public static final String TAG_START = "START";
    public static final String TAG_RUNNING = "RUNNING";
    public static final String TAG_STOP = "STOP";
    public static final String TAG_COMPLETE = "COMPLETE";
    public static final String TAG_PAUSE = "PAUSE";
    public static final String TAG_STATION = "STATIONARY";
    public static final String TAG_QS = "qscore";
    public static final String TAG_MET = "metronome";
    public static final String TAG_RANGE = "range";
    public static final String TAG_STEADY = "steady";
    public static final String SHARED_MACHINE_NAME = "machine_name";
    public static final String TAG_UNKNOWN = "Unknown";
    public static final String MACHINE_NAME = "Machine Name";
    public static final String PASSWORD = "Password";
    public static final String PW = "Fit 20";
    public static final String CD = "Countdown";
    public static final int BASE_VAL = 10;
    public static final int TAG_TOO_SLOW = -150;
    public static final int TAG_SLOW = -40;
    public static final int TAG_FAST = 40;
    public static final int TAG_TOO_FAST = 150;
    public static final int MAX = 1000;
    public static final int MIN = 0;
}
