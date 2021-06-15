package com.example.whoscall;

import android.app.AlertDialog;
import android.app.Dialog;
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
import android.widget.ImageView;
import android.widget.TextView;

public class SearchPhoneFragment extends Fragment {
    private Button searchPhoneButton;
    private EditText searchPhoneEditText;
    private Dialog searchPhoneDialog;

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
                searchPhoneDialog=new Dialog(SearchPhoneFragment.this.getContext());
                searchPhoneDialog.setContentView(R.layout.search_phone_dlg);

                TextView searchPhoneDlgNumberText=searchPhoneDialog.findViewById(R.id.searchPhoneDlgNumberText);
                TextView searchPhoneDlgResultText=searchPhoneDialog.findViewById(R.id.searchPhoneDlgResultText);
                ImageView searchPhoneDlgImage=searchPhoneDialog.findViewById(R.id.searchPhoneDlgImage);

                if(phoneNumber.equals((""))){
                    searchPhoneDlgImage.setImageResource(R.drawable.search_phone_fragment_please_input);
                    searchPhoneDlgNumberText.setText("要輸入號碼 !!!");
                    searchPhoneDialog.show();
                    return;
                }

                MySQLiteHelper mMySQLite=new MySQLiteHelper(SearchPhoneFragment.this.getContext(), getString(R.string.sqlite_database), null, 1);
                SQLiteDatabase sqlite=mMySQLite.getWritableDatabase();
                Cursor cursor=sqlite.rawQuery("select Result from "+getString(R.string.phone_information)+" where Number='"+phoneNumber+"'", null);

                searchPhoneDlgNumberText.setText(phoneNumber);

                if(cursor.getCount() != 0){
                    cursor.moveToFirst();
                    searchPhoneDlgImage.setImageResource(R.drawable.search_phone_fragment_find_number);
                    searchPhoneDlgResultText.setText(cursor.getString(0));

                }else{
                    searchPhoneDlgImage.setImageResource(R.drawable.search_phone_fragment_not_find_number);
                    searchPhoneDlgResultText.setText("啊 ! 似乎找不到這個號碼的資訊");
                }

                searchPhoneDialog.show();
                sqlite.close();
                cursor.close();
            }
        });
        return v;
    }
}