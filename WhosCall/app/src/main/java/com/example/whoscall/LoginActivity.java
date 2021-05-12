package com.example.whoscall;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {
    private EditText loginEdtAccount, loginEdtPassword;
    private Button loginBtnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginEdtAccount=findViewById(R.id.loginEdtAccount);
        loginEdtPassword=findViewById(R.id.loginEdtPassword);
        loginBtnLogin=findViewById(R.id.loginBtnLogin);

        loginBtnLogin.setOnClickListener(loginBtnLoginOnClickListener);
    }

    private View.OnClickListener loginBtnLoginOnClickListener=new View.OnClickListener(){
        @Override
        public void onClick(View view){
            //測試sharedPreferences 用
            //SharedPreferences sharedP=getSharedPreferences("account_result", MODE_PRIVATE);
            //sharedP.edit().putString("account", loginEdtAccount.getText().toString()).commit();
            //finish();
        }
    };

    @Override
    public void onBackPressed() {
        //沒幹什麼，只是讓使用者無法按back鍵回到前頁
        //參考的資料
        //https://stackoverflow.com/questions/4779954/disable-back-button-in-android
    }
}