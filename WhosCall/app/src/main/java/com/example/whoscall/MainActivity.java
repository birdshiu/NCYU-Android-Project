package com.example.whoscall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CallLog;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.net.HttpURLConnection;

public class MainActivity extends AppCompatActivity {
    private final String REQUEST_FOR_PERMISSION_MESSAGE="哦不!\n你似乎沒讓程式取得所有權限\n趕快點擊下方的按鈕\n到設定取得所有權限吧 !";
    private final String READY_START_MESSAGE="即將進入本程式\n請稍候...";

    private TextView mainTxtMessage;
    private Button mainBtnGivePermission;
    private ImageView mainImage;
    private Handler mainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainHandler=new Handler();

        mainBtnGivePermission=findViewById(R.id.mainBtnGivePermission);
        mainTxtMessage=findViewById(R.id.mainTxtMessage);
        mainImage=findViewById(R.id.mainImage);

        mainBtnGivePermission.setOnClickListener(mainBtnGivePermissionOnClickListener);
        
    }

    private void checkAllPermission(){
        boolean bReadCallLog=false;
        boolean bReadPhoneState=false;

        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED){
            bReadCallLog=true;
        }

        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED){
            bReadPhoneState=true;
        }

        if(bReadCallLog & bReadPhoneState){ //兩個權限都有得到的話
            mainBtnGivePermission.setVisibility(View.INVISIBLE);
            mainTxtMessage.setText(READY_START_MESSAGE);
            mainTxtMessage.setTextSize(32);
            mainImage.setImageResource(R.drawable.ready_start);
            mainHandler.postDelayed(enterLoginOrMenuActivityRunnable, 1500); //1.5秒後準備進入
        }else{
            /**
             * 把 View 給藏起來。
             * 參考資料:https://tomkuo139.blogspot.com/2016/03/android-view-visible-hide.html
             **/
            mainBtnGivePermission.setVisibility(View.VISIBLE);
            mainTxtMessage.setText(REQUEST_FOR_PERMISSION_MESSAGE);
            mainTxtMessage.setTextSize(20);
            mainImage.setImageResource(R.drawable.give_me_permission);
        }
    }

    private View.OnClickListener mainBtnGivePermissionOnClickListener=new View.OnClickListener(){
        public void onClick(View view){
            /**
             * 這裡是開啟這個程式的應用程式資訊，讓使用者手動去開啟權限。
             * 參考資料:https://stackoverflow.com/questions/17167442/how-to-launch-app-info-for-a-android-package
             **/
            Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            i.addCategory(Intent.CATEGORY_DEFAULT);
            i.setData(Uri.parse("package:" + getPackageName()));
            startActivity(i);
        }
    };

    private Runnable enterLoginOrMenuActivityRunnable=new Runnable(){
        public void run(){
            SharedPreferences sharedP=getSharedPreferences(getString(R.string.whos_calls_shared_preference), MODE_PRIVATE);
            Intent tmpIntent;

            if(sharedP.getString(getString(R.string.user_account), "nope").equals("nope")){ //假如拿不到 user_account 值
                tmpIntent=new Intent(MainActivity.this, LoginActivity.class); //進入登入頁面
            }else{
                tmpIntent=new Intent(MainActivity.this, MenuActivity.class); //進入選單頁面
            }
            startActivity(tmpIntent);
        }
    };

    @Override
    protected void onResume(){
        /**
         * 每次 resume 都檢查一次權限。
         */
        super.onResume();
        checkAllPermission();
    }

}