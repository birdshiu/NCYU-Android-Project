package com.example.whoscall;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private TextView txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent tmpIntent=new Intent(MainActivity.this, LoginActivity.class);
        startActivity(tmpIntent);

        Uri allCalls = Uri.parse("content://call_log/calls");
        //Cursor c = managedQuery(CallLog.Calls.CONTENT_URI, null, null, null, null);

        /*String result="";

        String num= c.getString(c.getColumnIndex(CallLog.Calls.NUMBER));// for  number
        String name= c.getString(c.getColumnIndex(CallLog.Calls.CACHED_NAME));// for name
        String duration = c.getString(c.getColumnIndex(CallLog.Calls.DURATION));// for duration
        int type = Integer.parseInt(c.getString(c.getColumnIndex(CallLog.Calls.TYPE)));// for call type, Incoming or out going.

        result+=num+", "+name+", "+duration+", "+type;
        txt.setText(result);*/
    }
}