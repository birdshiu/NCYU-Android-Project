package com.example.whoscall;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class SearchPhoneFragment extends Fragment {
    private Button searchPhoneButton;
    private EditText searchPhoneEditText;

    public SearchPhoneFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_search_phone, container, false);

        searchPhoneEditText=v.findViewById(R.id.searchPhoneEditText);
        searchPhoneButton=v.findViewById(R.id.searchPhoneButton);
        searchPhoneButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String phoneNumber=searchPhoneEditText.getText().toString();
                AlertDialog.Builder altDialog=new AlertDialog.Builder(SearchPhoneFragment.this.getContext());

                if(phoneNumber.equals((""))) return;

                MySQLiteHelper mMySQLite=new MySQLiteHelper(SearchPhoneFragment.this.getContext(), getString(R.string.sqlite_database), null, 1);
                SQLiteDatabase sqlite=mMySQLite.getWritableDatabase();

                Cursor cursor=sqlite.rawQuery("select Result from "+getString(R.string.phone_information)+" where Number='"+phoneNumber+"'", null);

                altDialog.setTitle(phoneNumber);

                if(cursor.getCount() != 0){
                    cursor.moveToFirst();
                    altDialog.setIcon(android.R.drawable.ic_dialog_info);
                    altDialog.setMessage(cursor.getString(0));

                }else{
                    altDialog.setIcon(android.R.drawable.ic_dialog_alert);
                    altDialog.setMessage("查無此電話的資訊");
                }

                altDialog.show();
                sqlite.close();
                cursor.close();
            }
        });


        return v;
    }
}