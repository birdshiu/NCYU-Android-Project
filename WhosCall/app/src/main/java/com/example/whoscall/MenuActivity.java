package com.example.whoscall;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import com.google.android.material.tabs.TabLayout;

public class MenuActivity extends AppCompatActivity {
    public SyncDataBaseService mSyncDataBaseService=null;
    private ServiceConnection mServiceConn=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mSyncDataBaseService=((SyncDataBaseService.LocalBinder)service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        //隱藏 Action Bar:https://stackoverflow.com/questions/8456835/how-to-disable-action-bar-permanently
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        InnerPagerAdapter pagerAdapter=new InnerPagerAdapter(getSupportFragmentManager());

        ViewPager viewPager=findViewById((R.id.menuViewPager));
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout=findViewById(R.id.menuTabLayout);
        tabLayout.setupWithViewPager(viewPager);

        Intent it=new Intent(MenuActivity.this, SyncDataBaseService.class);
        bindService(it, mServiceConn, BIND_AUTO_CREATE);
    }

    public class InnerPagerAdapter extends FragmentPagerAdapter{

        public InnerPagerAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int position){
            Fragment fragment=null;

            switch(position){
                case 0:
                    fragment=new ListCallLogFragment();
                    break;
                case 1:
                    fragment=new SearchPhoneFragment();
                    break;
                case 2:
                    fragment=new ReportPhoneFragment();
                    break;
            }

            return fragment;
        }

        @Override
        public int getCount(){
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position){
            switch(position){
                case 0:
                    return "通話記錄";
                case 1:
                    return "詢問號碼";
                case 2:
                   return "回報號碼";
                default:
                    return null;
            }
        }
    }

    public void onBackPressed() {
        /**
         * 沒幹什麼，只是讓使用者無法按back鍵回到前頁
         *參考的資料
         * https://stackoverflow.com/questions/4779954/disable-back-button-in-android
         */
    }
}