package com.example.whoscall;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class MyPhoneStateListener extends PhoneStateListener {
    @Override
    public void onCallStateChanged(int state, String phoneNumber) {
        switch (state) {
            //電話狀態是閒置的
            case TelephonyManager.CALL_STATE_IDLE:
                Log.d("message", "idle");
                break;
            //電話狀態是接起的
            case TelephonyManager.CALL_STATE_OFFHOOK:
                Log.d("message", "offhook");
                break;
            //電話狀態是響起的
            case TelephonyManager.CALL_STATE_RINGING:
                Log.d("message", "ringing"+phoneNumber);
                break;
            default:
                break;
        }
    }
}