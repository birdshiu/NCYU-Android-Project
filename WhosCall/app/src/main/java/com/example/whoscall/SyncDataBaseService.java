package com.example.whoscall;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class SyncDataBaseService extends Service {
    private MySQLiteHelper mMySQLite;

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
        mMySQLite=new MySQLiteHelper(getApplicationContext(), getString(R.string.sqlite_database), null, 1);
        SQLiteDatabase sqlite=mMySQLite.getWritableDatabase();

        //如何知道 sqlite 某個table是否存在:https://stackoverflow.com/questions/1601151/how-do-i-check-in-sqlite-whether-a-table-exists
        Cursor cursor=sqlite.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name='"+getString(R.string.user_advice)+"'", null);
        if(cursor != null){
            if(cursor.getCount() == 0){
                sqlite.execSQL("CREATE TABLE "+getString(R.string.user_advice)+" ("+
                        "Number TEXT PRIMARY KEY,"+
                        "Description TEXT,"+
                        "JoinDate DATETIME,"+
                        "SyncWithCloud INTEGER);");

                Log.d("message", "sql create ok");
            }
            cursor.close();
        }

        cursor=sqlite.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name='"+getString(R.string.phone_information)+"'", null);
        if(cursor != null){
            if(cursor.getCount() == 0){
                sqlite.execSQL("CREATE TABLE "+getString(R.string.phone_information)+" ("+
                        "Number TEXT PRIMARY KEY,"+
                        "Result TEXT,"+
                        "UpdateDate DATETIME);");

                Log.d("message", "phone information create ok");
            }
            cursor.close();
        }

        sqlite.close();
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

    public void createDataBase(){

    }
}