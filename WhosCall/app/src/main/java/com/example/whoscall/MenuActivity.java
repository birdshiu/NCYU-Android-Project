package com.example.whoscall;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import android.os.Bundle;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        //隱藏 Action Bar:https://stackoverflow.com/questions/8456835/how-to-disable-action-bar-permanently
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
    }

    public class InnerPagerAdapter extends FragmentPagerAdapter{

        public InnerPagerAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int position){
            Fragment fragment=null;

            switch(position){

            }
        }
    }
}