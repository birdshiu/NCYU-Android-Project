package com.example.whoscall;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * 這個程式的格式跟 LoginActivity.java 差不多，有80%都是從那邊搬過來
 * 只是他做的是註冊帳號。
 */
public class RegisterActivity extends AppCompatActivity {
    private final String REGISTER_RESULT_OK="0";
    private final String REGISTER_RESULT_DUPLICATED="1";
    private final String REGISTER_RESULT_SERVER_ERROR="2";
    private final String REGISTER_RESULT_NO_INTERNET="3";

    private Handler registerHandler;
    private Button registerBtnRegister;
    private EditText registerEdtAccount, registerEdtPassword;
    private ProgressDialog registerProgressDialog;

    private String userAccount, userPassword;
    private String resultCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerHandler=new Handler();
        registerBtnRegister=findViewById(R.id.registerBtnRegister);
        registerEdtAccount=findViewById(R.id.registerEdtAccount);
        registerEdtPassword=findViewById(R.id.registerEdtPassword);
        /**
         * ProgressDialog 參考資料:https://a7069810.pixnet.net/blog/post/400094257-%5Bandroid%5D-progressdialog
         */
        registerProgressDialog=new ProgressDialog(RegisterActivity.this);
        registerProgressDialog.setMessage("處理中...");
        registerProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        registerProgressDialog.setCancelable(false); //不能手動停止

        registerBtnRegister.setEnabled(false);
        registerBtnRegister.setOnClickListener(registerBtnRegisterOnClickListener);

        registerEdtAccount.addTextChangedListener(registerAccountTextChangeListener);
        registerEdtPassword.addTextChangedListener(registerPasswordTextChangeListener);

    }

    private View.OnClickListener registerBtnRegisterOnClickListener=new View.OnClickListener(){
        @Override
        public void onClick(View view){
            userAccount=registerEdtAccount.getText().toString();
            userPassword=registerEdtPassword.getText().toString();
            registerProgressDialog.show(); //顥示那個轉轉轉的
            /**
             * 點擊後就弄個 Thread 來跑網路的傳輸工作
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

                    /**
                     * 可以用下面被註解的程式碼來得知網路狀態
                     * 參考資料:https://www.tutlane.com/tutorial/android/android-internet-connection-status-with-examples
                     */

                    /*ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo nInfo = cm.getActiveNetworkInfo();
                    boolean connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();*/

                    try{
                        url=new URL("http://"+getString(R.string.server_ip)+"/Register.php"); //請求的目標
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

                        BufferedReader bf=new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        DataInputStream dataIStream=new DataInputStream(connection.getInputStream());
                        resultCode=bf.readLine(); //讀取結果
                        Log.d("message", resultCode);

                        registerHandler.post(afterRegisterBtnClick);

                        /*StringBuffer inputLine=new StringBuffer();
                        String tmpString;
                        while((tmpString=dataIStream.readLine()) != null){
                            inputLine.append(tmpString);
                            Log.d("message", inputLine.toString());
                        }*/


                    }catch(Exception e){
                        Log.d("message", e.toString());
                    }
                }
            }).start();
        }
    };

    private TextWatcher registerAccountTextChangeListener=new TextWatcher(){

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            /**
             * 跟loginActivity差不多
             */
            String sValue=s.toString();//那個 sValue 是 EditText 的內容
            if(!sValue.matches("[a-zA-Z1-9]+")){ //輸入限定只能是 A~Z 或 a~z 跟 1~9
                if(sValue.length() != 0){ //要先確定是否有字串
                    registerEdtAccount.getText().delete(s.length()-1, s.length());//去除最後一個字(就是使用者輸入的那個不合法字)
                }
            }
            checkRegisterBtnRegisterEnable();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private TextWatcher registerPasswordTextChangeListener=new TextWatcher(){

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String sValue=s.toString();//那個 sValue 是 EditText 的內容
            if(!sValue.matches("[a-zA-Z1-9]+")){ //輸入限定只能是 A~Z 或 a~z 跟 1~9
                if(sValue.length() != 0){ //要先確定是否有字串
                    registerEdtPassword.getText().delete(s.length()-1, s.length());//去除最後一個字(就是使用者輸入的那個不合法字)
                }
            }
            checkRegisterBtnRegisterEnable();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void checkRegisterBtnRegisterEnable(){ //查看兩個輸入框是否有東西
        int editPasswordLength=registerEdtPassword.getText().toString().length();
        int editAccountLength=registerEdtAccount.getText().toString().length();

        if(editAccountLength != 0 && editPasswordLength != 0){ //兩個都有的話，就能按下註冊鈕
            registerBtnRegister.setEnabled(true);
        }else{
            registerBtnRegister.setEnabled(false);
        }
    }

    private Runnable afterRegisterBtnClick=new Runnable(){
        //有接收到 php 端回應後要做的事
        public void run(){
            registerProgressDialog.dismiss(); //停止 progressdialog
        }
    };
}