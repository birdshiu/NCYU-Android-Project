package com.example.whoscall;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

public class MenuActivity extends AppCompatActivity {
    private PhoneReceiver phoneReceiver;
    private Handler menuHandler;
    private ProgressDialog menuProgressDialog;
    private boolean syncHasError;

    private ViewPager menuViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        menuHandler=new Handler();

        //隱藏 Action Bar:https://stackoverflow.com/questions/8456835/how-to-disable-action-bar-permanently
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        syncHasError=false;

        menuProgressDialog=new ProgressDialog(MenuActivity.this);
        menuProgressDialog.setMessage("同步資料中...");
        menuProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        menuProgressDialog.setCancelable(false); //不能手動停止
        menuProgressDialog.show();

        menuViewPager=findViewById((R.id.menuViewPager));
        //menuViewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout=findViewById(R.id.menuTabLayout);
        tabLayout.setupWithViewPager(menuViewPager);

        MySQLiteHelper mMySQLite=new MySQLiteHelper(getApplicationContext(), getString(R.string.sqlite_database), null, 1);
        SQLiteDatabase sqlite=mMySQLite.getWritableDatabase();

        //如何知道 sqlite 某個table是否存在:https://stackoverflow.com/questions/1601151/how-do-i-check-in-sqlite-whether-a-table-exists
        Cursor cursor=sqlite.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name='"+getString(R.string.user_advice)+"'", null);
        if(cursor != null){
            if(cursor.getCount() == 0){
                sqlite.execSQL("CREATE TABLE "+getString(R.string.user_advice)+" ("+
                        "Number VARCHAR(15) PRIMARY KEY,"+
                        "Description VARCHAR(15),"+
                        "JoinDate DATETIME);");
            }
            cursor.close();
        }

        cursor=sqlite.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name='"+getString(R.string.phone_information)+"'", null);
        if(cursor != null){
            if(cursor.getCount() == 0){
                sqlite.execSQL("CREATE TABLE "+getString(R.string.phone_information)+" ("+
                        "Number VARCHAR(15) PRIMARY KEY,"+
                        "Result VARCHAR(15),"+
                        "UpdateDate DATETIME);");
            }
            cursor.close();
        }
        sqlite.close();
        syncLocalData();

        IntentFilter intentFilter=new IntentFilter("android.intent.action.PHONE_STATE");
        phoneReceiver=new PhoneReceiver(MenuActivity.this);
        registerReceiver(phoneReceiver, intentFilter);
    }


    private void syncLocalData() {
        //先同步使用者建議的電話號碼，再同步電話號碼資訊
        new Thread(new Runnable(){
            @Override
            public void run(){
                MySQLiteHelper mMySQLite=new MySQLiteHelper(getApplicationContext(), getString(R.string.sqlite_database), null, 1);
                SQLiteDatabase sqlite=mMySQLite.getWritableDatabase();

                String JoinDate, UpdateDate; //這兩個會上傳給 server,用來確認最新一筆資料的時間，會送到 server 查詢

                Cursor cursor=sqlite.rawQuery("select * from "+getString(R.string.user_advice), null);

                if(cursor.getCount() == 0){ //本地還沒有資料
                    //datetime格式:https://docs.microsoft.com/zh-tw/sql/t-sql/functions/date-and-time-data-types-and-functions-transact-sql?view=sql-server-ver15
                    JoinDate="1980-01-01 00:00:00"; //反正就一個比較久之前的日期
                }else{
                    cursor=sqlite.rawQuery("select * from "+getString(R.string.user_advice)+" order by date(JoinDate) desc", null); //拿最新一筆資料的日期
                    cursor.moveToFirst();
                    JoinDate=cursor.getString(2);
                }

                cursor=sqlite.rawQuery("select * from "+getString(R.string.phone_information), null);
                if(cursor.getCount() == 0){ //本地還沒有資料
                    //datetime格式:https://docs.microsoft.com/zh-tw/sql/t-sql/functions/date-and-time-data-types-and-functions-transact-sql?view=sql-server-ver15
                    UpdateDate="1980-01-01 00:00:00";//反正就一個比較久之前的日期
                }else{
                    cursor=sqlite.rawQuery("select * from "+getString(R.string.phone_information)+" order by date(UpdateDate) desc", null); //拿最新一筆資料的日期
                    cursor.moveToFirst();
                    UpdateDate=cursor.getString(2);
                }

                URL url;
                HttpURLConnection connection;

                try{
                    SharedPreferences sharedP=getSharedPreferences(getString(R.string.whos_calls_shared_preference), MODE_PRIVATE);
                    String user=sharedP.getString(getString(R.string.user_account), "");
                    url=new URL("http://"+getString(R.string.server_ip)+"/SyncLocal.php"); //請求的目標
                    connection=(HttpURLConnection)url.openConnection();

                    String parameters="user="+user+"&joindate="+JoinDate+"&updatedate="+UpdateDate; //post的值，用&做連接
                    byte[] postData=parameters.getBytes(Charset.forName("UTF-8")); //轉成 byte 序列，utf-8 編碼
                    int postDataLength=postData.length; //post需要知道資料的長度

                    //下面都是一些設定
                    connection=(HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setInstanceFollowRedirects(false); //不確定是啥，好像可有可無
                    connection.setDoOutput(true); //使用 URL 連結做輸出
                    connection.setDoInput(true); //使用 URL 連結做輸入
                    connection.setConnectTimeout(3000); //逾時
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); //Post 的資料格式
                    connection.setRequestProperty("Content-Length", Integer.toString(postDataLength));
                    connection.setRequestProperty("charset", "UTF-8");
                    connection.setUseCaches(false);

                    DataOutputStream dataOStream=new DataOutputStream(connection.getOutputStream());
                    dataOStream.write(postData);
                    dataOStream.flush();

                    //然後把下載來的資料存到本地資料庫
                    BufferedReader bf=new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    int rowNumber;

                    rowNumber=Integer.parseInt(bf.readLine()); //先接個數字，這是 user_advice_phone_number 部分
                    for(int i=0;i<rowNumber;i++){
                        //傳過來的資料都是用 "/" 分開的:http://tw.gitbook.net/java/java_string_split.html
                        String[] strValues=bf.readLine().split("/");
                        //再來就用 sqlite 寫入資料:https://developer.android.com/training/data-storage/sqlite
                        ContentValues values = new ContentValues();
                        values.put("Number", strValues[0]);
                        values.put("Description", strValues[1]);
                        values.put("JoinDate", strValues[2]);

                        sqlite=mMySQLite.getWritableDatabase();
                        sqlite.insert(getString(R.string.user_advice), null, values);
                    }

                    rowNumber=Integer.parseInt(bf.readLine()); //再接一次數字
                    for(int i=0;i<rowNumber;i++) {
                        //跟上面差不多
                        String[] strValues = bf.readLine().split("/");
                        //再來就用 sqlite 寫入資料:https://developer.android.com/training/data-storage/sqlite
                        ContentValues values = new ContentValues();
                        values.put("Number", strValues[0]);
                        values.put("Result", strValues[1]);
                        values.put("UpdateDate", strValues[2]);

                        sqlite = mMySQLite.getWritableDatabase();
                        sqlite.insert(getString(R.string.phone_information), null, values);
                    }
                }catch(Exception e){
                    Log.d("message",e.toString());
                    //有抓到錯誤的話，可能就是 server 端出錯(或本地網路有問題)
                    syncHasError=true;
                }
                cursor.close();
                sqlite.close();
                menuHandler.post(afterSyncLocalData);
            }
        }).start();
    }

    public void checkOnGoingNumber(String phoneNumber){
        try{
            String result="";
            MySQLiteHelper mMySQLite=new MySQLiteHelper(getApplicationContext(), getString(R.string.sqlite_database), null, 1);
            SQLiteDatabase sqlite=mMySQLite.getWritableDatabase();

            Cursor cursor=sqlite.rawQuery("select Description from "+getString(R.string.user_advice)+" where Number='"+phoneNumber+"'", null);

            if(cursor.getCount() != 0){
                cursor.moveToFirst();
                result=cursor.getString(0);
            }else{
                cursor=sqlite.rawQuery("select Result from "+getString(R.string.phone_information)+" where Number='"+phoneNumber+"'", null);
                if(cursor.getCount() != 0){
                    cursor.moveToFirst();
                    result=cursor.getString(0);
                }
            }
            //android 的通知:https://foolcodefun.github.io/blog/android/2018/01/11/Android-Notification.html
            NotificationManager manager=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            Notification.Builder builder=new Notification.Builder(getApplicationContext());

            builder.setContentTitle("來電辨識");
            builder.setSmallIcon(R.drawable.thimking);

            if(result.equals("")){
                builder.setContentText("不明的電話號碼");
            }else{
                builder.setContentText(result);
            }
            NotificationChannel channel;
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                channel=new NotificationChannel("0", "who's call", NotificationManager.IMPORTANCE_HIGH);
                builder.setChannelId("0");
                manager.createNotificationChannel(channel);
            }else{
                builder.setDefaults(Notification.DEFAULT_ALL)
                        .setVisibility(Notification.VISIBILITY_PUBLIC);
            }
            manager.notify(0, builder.build());
        }catch(Exception e){
            Log.d("message", e.toString());
        }
    }

    private Runnable afterSyncLocalData=new Runnable(){
        @Override
        public void run(){
            if(syncHasError){
                Toast.makeText(MenuActivity.this, "資料同步失敗，請確認連線", Toast.LENGTH_LONG).show();
            }
            menuProgressDialog.dismiss();
            InnerPagerAdapter pagerAdapter=new InnerPagerAdapter(getSupportFragmentManager()); //這時再布置那個 fragment
            menuViewPager.setAdapter(pagerAdapter);
        }
    };

    public class InnerPagerAdapter extends FragmentPagerAdapter{

        public InnerPagerAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int position){
            Fragment fragment=null;

            switch(position){
                case 0:
                    fragment=new ListCallLogFragment();
                    break;
                case 1:
                    fragment=new SearchPhoneFragment();
                    break;
            }

            return fragment;
        }

        @Override
        public int getCount(){
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position){
            switch(position){
                case 0:
                    return "通話記錄";
                case 1:
                    return "詢問號碼";
                default:
                    return null;
            }
        }
    }

    public void showSearchPhoneFragment(){
        menuViewPager.setCurrentItem(2);
    }

    public void onBackPressed() {
        /**
         * 沒幹什麼，只是讓使用者無法按back鍵回到前頁
         *參考的資料
         * https://stackoverflow.com/questions/4779954/disable-back-button-in-android
         */
    }
}