package com.example.whoscall;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
    private final int REGISTER_RESULT_OK=0;
    private final int REGISTER_RESULT_DUPLICATED=1;
    private final int REGISTER_RESULT_SERVER_ERROR=2;
    private final int REGISTER_RESULT_NO_INTERNET=3;

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
            registerBtnRegister.setEnabled(false); //防止使用者一按再按
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
                        connection.setConnectTimeout(3000); //逾時
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
                        /*StringBuffer inputLine=new StringBuffer();
                        String tmpString;
                        while((tmpString=dataIStream.readLine()) != null){
                            inputLine.append(tmpString);
                            Log.d("message", inputLine.toString());
                        }*/

                        BufferedReader bf=new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        DataInputStream dataIStream=new DataInputStream(connection.getInputStream());
                        resultCode=bf.readLine(); //讀取結果，Server只會傳一個字

                    }catch(Exception e){
                        //有抓到錯誤的話，可能就是 server 端出錯(或本地網路有問題)
                        resultCode=String.valueOf(REGISTER_RESULT_SERVER_ERROR);
                    }

                    registerHandler.post(afterRegisterBtnRegisterClick);
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
            if(!sValue.matches("[a-zA-Z0-9]+")){ //輸入限定只能是 A~Z 或 a~z 跟 1~9
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
            /**
             * inputType=password 的 editText 不能直接從那個 CharSequence 得值，而且也不能用 delete 來更動
             * 內容，所以這邊的做法跟 Account 的部份不同。
             */
            String sValue=registerEdtPassword.getText().toString();
            if(!sValue.matches("[a-zA-Z0-9]+")){ //輸入限定只能是 A~Z 或 a~z 跟 0~9
                if(sValue.length() != 0){ //要先確定是否有字串
                    //loginEdtPassword.getText().delete(s.length()-1, s.length()); 這個無法作用
                    registerEdtPassword.setText(sValue.substring(0, sValue.length()-1));
                    registerEdtPassword.setSelection(sValue.length()-1); //把游標放到最後:https://stackoverflow.com/questions/8035107/how-to-set-cursor-position-in-edittext
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

    private Runnable afterRegisterBtnRegisterClick=new Runnable(){
        //有接收到 php 端回應後要做的事
        public void run(){
            registerProgressDialog.dismiss(); //停止 progressdialog

            int iResultCode=Integer.parseInt(resultCode);

            switch(iResultCode){ //檢查狀態碼
                case REGISTER_RESULT_OK:
                    showRegisterOkAlertDialog();
                    break;
                case REGISTER_RESULT_DUPLICATED:
                    showDuplicatedAccountErrorAlertDialog();
                    break;
                case REGISTER_RESULT_SERVER_ERROR:
                    showServerErrorAlertDialog();
                    break;
            }
        }
    };

    private void showServerErrorAlertDialog(){
        AlertDialog.Builder altDlgBuilder=new AlertDialog.Builder(RegisterActivity.this);
        altDlgBuilder.setTitle("錯誤");
        altDlgBuilder.setMessage("Server 端出錯 !\n看要不要等等再試。");
        altDlgBuilder.setIcon(android.R.drawable.ic_dialog_alert);
        altDlgBuilder.setCancelable(false);

        AlertDialog dialog; //close dialog:https://stackoverflow.com/questions/4336470/how-do-i-close-an-android-alertdialog/13871146

        altDlgBuilder.setPositiveButton("好哦", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                registerBtnRegister.setEnabled(true); //可以按了
                dialog.dismiss();
            }
        });

        dialog=altDlgBuilder.create();
        dialog.show();
    }

    private void showRegisterOkAlertDialog(){
        AlertDialog.Builder altDlgBuilder=new AlertDialog.Builder(RegisterActivity.this);
        altDlgBuilder.setTitle("註冊成功");
        altDlgBuilder.setMessage("你的帳號成功註冊\n現在能回到登入頁面做登入。");
        altDlgBuilder.setIcon(android.R.drawable.ic_dialog_info);
        altDlgBuilder.setCancelable(false);

        AlertDialog dialog; //close dialog:https://stackoverflow.com/questions/4336470/how-do-i-close-an-android-alertdialog/13871146

        altDlgBuilder.setPositiveButton("讚哦", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                RegisterActivity.this.finish();
            }
        });

        dialog=altDlgBuilder.create();
        dialog.show();
    }

    private void showDuplicatedAccountErrorAlertDialog(){
        AlertDialog.Builder altDlgBuilder=new AlertDialog.Builder(RegisterActivity.this);
        altDlgBuilder.setTitle("帳號重複");
        altDlgBuilder.setMessage("這個帳號名稱有人使用了哦!\n看看要不要換個。");
        altDlgBuilder.setIcon(android.R.drawable.ic_dialog_alert);
        altDlgBuilder.setCancelable(false);

        AlertDialog dialog; //close dialog:https://stackoverflow.com/questions/4336470/how-do-i-close-an-android-alertdialog/13871146

        altDlgBuilder.setPositiveButton("好哦", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                registerBtnRegister.setEnabled(true); //可以按了
                dialog.dismiss();
            }
        });

        dialog=altDlgBuilder.create();
        dialog.show();
    }
}