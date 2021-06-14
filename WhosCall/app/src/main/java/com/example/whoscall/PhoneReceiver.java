package com.example.whoscall;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Menu;

import java.lang.ref.WeakReference;

public class PhoneReceiver extends BroadcastReceiver {
    private TelephonyManager telephony;
    private final WeakReference<MenuActivity> mActivity;

    public PhoneReceiver(MenuActivity activity){
        mActivity=new WeakReference<MenuActivity>(activity);
    }

    public void onReceive(Context context, Intent intent) {
        //接收 phone calling:https://stackoverflow.com/questions/13395633/add-phonestatelistener
        MenuActivity activity=mActivity.get();
        if(activity != null){
            MyPhoneStateListener phoneListener = new MyPhoneStateListener(activity);
            telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            telephony.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }

    public void onDestroy() {
        telephony.listen(null, PhoneStateListener.LISTEN_NONE);
    }

}
