package com.example.whoscall;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

public class MenuActivity extends AppCompatActivity {

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
}