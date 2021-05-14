package com.example.whoscall;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
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
    private EditText loginEdtAccount, loginEdtPassword;
    private Button loginBtnLogin;
    private Thread tmpT;

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

                        String parameters="user=pony&password=1234"; //post的值，用&做連接
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
                         * 之後如困要讀Server端送過來的資料，可用以下方法
                         *  參考資料:https://stackoverflow.com/questions/50506450/how-to-read-whole-data-from-datainputstream-by-a-loop
                         */
                        //DataInputStream dataIStream=new DataInputStream(connection.getInputStream());
                        //StringBuffer inputLine=new StringBuffer();
                        //String tmp;
                        //while((tmp=dataIStream.readLine()) != null){
                        //    inputLine.append(tmp);
                        //    Log.d("message", inputLine.toString());
                         //}


                    }catch(Exception e){
                        Log.d("message", e.toString());
                    }
                }
            }).start();
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
}