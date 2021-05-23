package com.example.whoscall;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class ListCallLogFragment extends Fragment {
    private Button btn;

    public ListCallLogFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_list_call_log, container, false);

        btn=v.findViewById(R.id.listCallFragmentBtn);
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                MenuActivity menuActivity=(MenuActivity)getActivity();
                if(menuActivity != null){
                    menuActivity.mSyncDataBaseService.showUserData();
                }
            }
        });

        return v;
    }
}