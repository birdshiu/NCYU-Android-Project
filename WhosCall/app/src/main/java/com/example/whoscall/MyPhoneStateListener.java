package com.example.whoscall;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.lang.ref.WeakReference;

public class MyPhoneStateListener extends PhoneStateListener {
    private final WeakReference<MenuActivity> mActivity;

    public MyPhoneStateListener(MenuActivity activity){
        mActivity=new WeakReference<MenuActivity>(activity);
    }
    @Override
    public void onCallStateChanged(int state, String phoneNumber) {
            //電話狀態是響起的
        if(state == TelephonyManager.CALL_STATE_RINGING){
            MenuActivity activity=mActivity.get();
            if(activity != null){
                activity.checkOnGoingNumber(phoneNumber);
            }
        }
    }
}