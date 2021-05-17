package com.example.whoscall;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private final String LOGIN_RESULT_OK="0";

    private EditText loginEdtAccount, loginEdtPassword;
    private Button loginBtnLogin, loginBtnRegister;

    private Handler loginHandler;
    private String resultCode;
    private String userAccount, userPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginEdtAccount=findViewById(R.id.loginEdtAccount);
        loginEdtPassword=findViewById(R.id.loginEdtPassword);
        loginBtnLogin=findViewById(R.id.loginBtnLogin);
        loginBtnRegister=findViewById(R.id.loginBtnRegister);

        loginBtnLogin.setEnabled(false);
        loginBtnLogin.setOnClickListener(loginBtnLoginOnClickListener);
        loginBtnRegister.setOnClickListener(loginBtnRegisterOnClickListener);
        loginEdtAccount.addTextChangedListener(loginAccountTextChangeListener);
        loginEdtPassword.addTextChangedListener(loginPasswordTextChangeListener);

        loginHandler=new Handler();
    }

    private View.OnClickListener loginBtnLoginOnClickListener=new View.OnClickListener(){
        @Override
        public void onClick(View view){
            loginBtnLogin.setEnabled(false); //防止使用者一點再點
            userAccount=loginEdtAccount.getText().toString();
            userPassword=loginEdtPassword.getText().toString();

            Log.d("message", userAccount);
            Log.d("message", userPassword);
            //點擊後就弄個 Thread 來跑網路的傳輸工作
            /**
             * HttpUrlConnection的範例:
             * https://stackoverflow.com/questions/4205980/java-sending-http-parameters-via-post-method-easily
             * https://blog.csdn.net/liufunan/article/details/51246732
             **/
            new Thread(new Runnable(){
                @Override
                public void run(){
                    URL url;
                    HttpURLConnection connection;
                    DataOutputStream dataOStream;

                    try{
                        url=new URL("http://"+getString(R.string.server_ip)+"/test.php"); //請求的目標
                        connection=(HttpURLConnection)url.openConnection();

                        String parameters="user="+userAccount+"&password="+userPassword; //post的值，用&做連接
                        byte[] postData=parameters.getBytes(Charset.forName("UTF-8")); //轉成 byte 序列，utf-8 編碼
                        int postDataLength=postData.length; //post需要知道資料的長度

                        //下面都是一些設定
                        connection=(HttpURLConnection)url.openConnection();
                        connection.setRequestMethod("POST");
                        connection.setInstanceFollowRedirects(false); //不確定是啥，好像可有可無
                        connection.setDoOutput(true); //使用 URL 連結做輸出
                        connection.setDoInput(true); //使用 URL 連結做輸入
                        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); //Post 的資料格式
                        connection.setRequestProperty("charset", "UTF-8");
                        connection.setRequestProperty("Content-Length", Integer.toString(postDataLength));
                        connection.setUseCaches(false);

                        //寫 Post 資料到連結
                        dataOStream=new DataOutputStream(connection.getOutputStream());
                        dataOStream.write(postData);
                        dataOStream.flush();

                        /**
                         * 如果最後沒有呼叫到 connection.getInputStream()，Server那邊好像不會接收到資料。(不知為啥)
                         * 之後如果要讀Server端送過來的資料，可用以下方法
                         *  參考資料:https://stackoverflow.com/questions/50506450/how-to-read-whole-data-from-datainputstream-by-a-loop
                         */
                        DataInputStream dataIStream=new DataInputStream(connection.getInputStream());
                        StringBuffer inputLine=new StringBuffer();
                        String tmpString;
                        while((tmpString=dataIStream.readLine()) != null){
                            inputLine.append(tmpString);
                            Log.d("message", inputLine.toString());
                         }


                    }catch(Exception e){
                        Log.d("message", e.toString());
                    }
                }
            }).start();
        }
    };

    private View.OnClickListener loginBtnRegisterOnClickListener=new View.OnClickListener(){
        public void onClick(View view){
            Intent tmpIntent=new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(tmpIntent);
        }
    };

    private TextWatcher loginAccountTextChangeListener=new TextWatcher(){

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            /**
             * 只要 EditText 的內容有變，這個方法就會觸發
             * 參考資料:https://stackoverflow.com/questions/11309710/how-to-apply-the-textchange-event-on-edittext
             **/
            String sValue=s.toString();//那個 sValue 是 EditText 的內容
            if(!sValue.matches("[a-zA-Z0-9]+")){ //輸入限定只能是 A~Z 或 a~z 跟 0~9
                if(sValue.length() != 0){ //要先確定是否有字串
                    loginEdtAccount.getText().delete(s.length()-1, s.length());//去除最後一個字(就是使用者輸入的那個不合法字)
                }
            }
            checkLoginBtnLoginEnable();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private TextWatcher loginPasswordTextChangeListener=new TextWatcher(){

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            /**
             * inputType=password 的 editText 不能直接從那個 CharSequence 得值，而且也不能用 delete 來更動
             * 內容，所以這邊的做法跟 Account 的部份不同。
             */
            String sValue=loginEdtPassword.getText().toString();
            if(!sValue.matches("[a-zA-Z0-9]+")){ //輸入限定只能是 A~Z 或 a~z 跟 0~9
                if(sValue.length() != 0){ //要先確定是否有字串
                    //loginEdtPassword.getText().delete(s.length()-1, s.length()); 這個無法作用
                    loginEdtPassword.setText(sValue.substring(0, sValue.length()-1));
                    loginEdtPassword.setSelection(sValue.length()-1); //把游標放到最後:https://stackoverflow.com/questions/8035107/how-to-set-cursor-position-in-edittext
                }
            }
            checkLoginBtnLoginEnable();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    public void onBackPressed() {
        /**
         * 沒幹什麼，只是讓使用者無法按back鍵回到前頁
         *參考的資料
         * https://stackoverflow.com/questions/4779954/disable-back-button-in-android
         */
    }

    private void checkLoginBtnLoginEnable(){ //查看兩個輸入框是否有東西
        int editPasswordLength=loginEdtPassword.getText().toString().length();
        int editAccountLength=loginEdtAccount.getText().toString().length();

        if(editAccountLength != 0 && editPasswordLength != 0){ //兩個都有的話，就能按下登入鈕
            loginBtnLogin.setEnabled(true);
        }else{
            loginBtnLogin.setEnabled(false);
        }
    }
}