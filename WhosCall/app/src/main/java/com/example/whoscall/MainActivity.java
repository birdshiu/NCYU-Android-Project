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
import android.provider.CallLog;
import android.util.Log;
import android.widget.TextView;

import java.net.HttpURLConnection;

public class MainActivity extends AppCompatActivity {
    private final int REQUEST_CODE_READ_CALL_LOG=1;
    private final int REQUEST_CODE_INTERNET=2;
    private TextView txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        askForAllPermission();

        Intent tmpIntent=new Intent(MainActivity.this, LoginActivity.class);
        startActivity(tmpIntent);
    }

    private void askForAllPermission(){
        //程式請求所有權限，使用者都同意後才能繼續使用本程式
        if(ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_CALL_LOG}, REQUEST_CODE_READ_CALL_LOG);
        }
    }

    @Override
    protected void onResume(){
        //test
        super.onResume();

        //SharedPreferences shareP=getSharedPreferences("account_result", MODE_PRIVATE);
        //Log.d("message", shareP.getString("account", "nope"));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        Log.d("message", String.valueOf(grantResults[0]));
    }
}