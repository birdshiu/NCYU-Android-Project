package com.example.whoscall;

import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.provider.CallLog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ListCallLogFragment extends Fragment {
    private RecyclerView listCallLogRecyclerView;
    private Handler listCallHandler;

    public ListCallLogFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_list_call_log, container, false);

        listCallLogRecyclerView=v.findViewById(R.id.listCallLogRecyclerView);
        listCallHandler=new Handler();


        return v;
    }

    @Override
    public void onResume(){
        listCallHandler.post(listCallOnResumeRunnable);
        super.onResume();
    }

    private Runnable listCallOnResumeRunnable=new Runnable() {
        @Override
        public void run() {
            List<String> listString=new ArrayList<>();
            //讀取 call log:https://stackoverflow.com/questions/6786666/how-do-i-access-call-log-for-android
            Cursor managedCursor=ListCallLogFragment.this.getContext().getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, CallLog.Calls.DATE + " DESC");
            int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);

            while(managedCursor.moveToNext()){
                listString.add(managedCursor.getString(number));
            }

            managedCursor.close();

            listCallLogRecyclerView.setLayoutManager(new LinearLayoutManager(ListCallLogFragment.this.getContext()));

            RecyclerViewAdapter adapter=new RecyclerViewAdapter(listString);
            listCallLogRecyclerView.setAdapter(adapter);
        }
    };

    private String getCallDetails() {
        StringBuffer sb = new StringBuffer();
        Cursor managedCursor = this.getContext().getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, CallLog.Calls.DATE + " DESC");
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        sb.append("Call Details :");
        while (managedCursor.moveToNext()) {
            String phNumber = managedCursor.getString(number);
            String callType = managedCursor.getString(type);
            String callDate = managedCursor.getString(date);
            Date callDayTime = new Date(Long.valueOf(callDate));
            String callDuration = managedCursor.getString(duration);
            String dir = null;

            Log.d("message", phNumber);
            int dircode = Integer.parseInt(callType);
            switch (dircode) {
                case CallLog.Calls.OUTGOING_TYPE:
                    dir = "OUTGOING";
                    break;

                case CallLog.Calls.INCOMING_TYPE:
                    dir = "INCOMING";
                    break;

                case CallLog.Calls.MISSED_TYPE:
                    dir = "MISSED";
                    break;
            }
            sb.append("\nPhone Number:--- " + phNumber + " \nCall Type:--- "
                    + dir + " \nCall Date:--- " + callDayTime
                    + " \nCall duration in sec :--- " + callDuration);
            sb.append("\n----------------------------------");
        }
        managedCursor.close();
        return sb.toString();

    }
}