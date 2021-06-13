package com.example.whoscall;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import java.util.Calendar;
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
            List<String> numberString=new ArrayList<>();
            List<String> resultString=new ArrayList<>();
            List<String> dateString=new ArrayList<>();
            List<String> stateString=new ArrayList<>();

            //讀取 call log:https://stackoverflow.com/questions/6786666/how-do-i-access-call-log-for-android
            Cursor managedCursor=ListCallLogFragment.this.getContext().getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, CallLog.Calls.DATE + " DESC");
            int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
            int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
            int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);

            while(managedCursor.moveToNext()){
                MySQLiteHelper mMySQLite=new MySQLiteHelper(ListCallLogFragment.this.getContext().getApplicationContext(), getString(R.string.sqlite_database), null, 1);
                SQLiteDatabase sqlite=mMySQLite.getWritableDatabase();

                if(managedCursor.getString(number).equals("")){
                    numberString.add("不明的號碼");
                    resultString.add("");
                }else{
                    numberString.add(managedCursor.getString(number));
                    Cursor cursor=sqlite.rawQuery("select Description from "+getString(R.string.user_advice)+" where Number='"+managedCursor.getString(number)+"'", null);

                    if(cursor.getCount() != 0){
                        cursor.moveToFirst();
                        resultString.add(cursor.getString(0));
                    }else{
                        cursor=sqlite.rawQuery("select Result from "+getString(R.string.phone_information)+" where Number='"+managedCursor.getString(number)+"'", null);

                        if(cursor.getCount() != 0){
                            cursor.moveToFirst();
                            resultString.add(cursor.getString(0));
                        }
                        else resultString.add("");
                    }
                    cursor.close();
                }
                sqlite.close();

                Date dateTime=new Date(Long.parseLong(managedCursor.getString(date))); //轉成 date 格式
                //https://stackoverflow.com/questions/7182996/java-get-month-integer-from-date
                Calendar cal=Calendar.getInstance();
                cal.setTime(dateTime);
                int month=cal.get(Calendar.MONTH);
                int day=cal.get(Calendar.DAY_OF_MONTH);
                dateString.add(String.valueOf(month+1)+"月"+String.valueOf(day)+"日");

                switch(Integer.parseInt(managedCursor.getString(type))){
                    case CallLog.Calls.OUTGOING_TYPE:
                        stateString.add("去電");
                        break;
                    case CallLog.Calls.INCOMING_TYPE:
                        stateString.add("來電");
                        break;
                    case CallLog.Calls.MISSED_TYPE:
                        stateString.add("未接");
                        break;
                    case CallLog.Calls.REJECTED_TYPE:
                        stateString.add("拒接");
                        break;
                    default:
                        stateString.add("???");
                        break;
                }
            }
            managedCursor.close();

            listCallLogRecyclerView.setLayoutManager(new LinearLayoutManager(ListCallLogFragment.this.getContext()));
            RecyclerViewAdapter adapter=new RecyclerViewAdapter(numberString, dateString, stateString, resultString);
            listCallLogRecyclerView.setAdapter(adapter);
        }
    };
}