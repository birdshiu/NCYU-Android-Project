package com.example.whoscall;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class SyncDataBaseService extends Service {

    public class LocalBinder extends Binder{
        SyncDataBaseService getService(){
            return SyncDataBaseService.this;
        }
    }

    private LocalBinder mLocalBinder=new LocalBinder();

    public SyncDataBaseService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mLocalBinder;
    }

    @Override
    public void onCreate(){
        Log.d("message", "onCreate()");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent){
        return super.onUnbind(intent);
    }

    public void showUserData(){ //test
        SharedPreferences sharedP=getSharedPreferences(getString(R.string.whos_calls_shared_preference), MODE_PRIVATE);
    }
}