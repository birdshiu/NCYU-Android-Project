package com.example.whoscall;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class SearchPhoneFragment extends Fragment {

    public SearchPhoneFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_search_phone, container, false);

        Button btn=v.findViewById(R.id.searchPhoneButton);
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //test
                MySQLiteHelper mMySQLite=new MySQLiteHelper(SearchPhoneFragment.this.getContext().getApplicationContext(), getString(R.string.sqlite_database), null, 1);
                SQLiteDatabase sqlite=mMySQLite.getWritableDatabase();

                Cursor cursor=sqlite.rawQuery("select * from "+getString(R.string.user_advice)+" order by date(JoinDate) desc", null);
                Log.d("message", "下面是 advice");
                if(cursor != null){
                    if(cursor.getCount() != 0){
                        cursor.moveToFirst();
                        Log.d("message", cursor.getString(0)+cursor.getString(1)+cursor.getString(2));

                        while(cursor.moveToNext()){
                            Log.d("message", cursor.getString(0)+cursor.getString(1)+cursor.getString(2));
                        }
                    }
                }

                Log.d("message", "下面是 information");
                cursor=sqlite.rawQuery("select * from "+getString(R.string.phone_information), null);
                if(cursor != null){
                    if(cursor.getCount() != 0){
                        cursor.moveToFirst();
                        Log.d("message", cursor.getString(0)+cursor.getString(1)+cursor.getString(2));

                        while(cursor.moveToNext()){
                            Log.d("message", cursor.getString(0)+cursor.getString(1)+cursor.getString(2));
                        }
                    }
                }
            }
        });


        return v;
    }
}