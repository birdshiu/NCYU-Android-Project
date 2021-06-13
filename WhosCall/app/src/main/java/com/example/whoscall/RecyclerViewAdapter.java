package com.example.whoscall;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{
    private List<String> mNumberString;
    private List<String> mDateString;
    private List<String> mStateString;
    private List<String> mResultString;
    //private Dialog reportDialog;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView callNumber, callResult, callState, callDate;
        private ProgressDialog progressDialog;
        private Dialog reportDialog;
        private AlertDialog.Builder altDlgBuilder;
        private Handler handler;
        private boolean isReportError;

        public ViewHolder(View itemView){
            super(itemView);

            altDlgBuilder=new AlertDialog.Builder(itemView.getContext());
            handler=new Handler();
            callDate=(TextView)itemView.findViewById(R.id.callDate);
            callState=(TextView)itemView.findViewById(R.id.callState);
            callNumber=(TextView) itemView.findViewById(R.id.callNumber);
            callResult=(TextView)itemView.findViewById(R.id.callResult);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view){
            if(!callNumber.getText().toString().equals("不明的號碼")){
                reportDialog=new Dialog(view.getContext());
                reportDialog.setContentView(R.layout.report_dlg);
                TextView reportDlgText=reportDialog.findViewById(R.id.reportDlgText);
                EditText reportDlgEditText=reportDialog.findViewById(R.id.reportDlgEditText);
                Button reportDlgButton=reportDialog.findViewById(R.id.reportDlgButton);
                String originReportString;

                reportDlgText.setText("號碼:\n"+callNumber.getText());

                MySQLiteHelper mMySQLite=new MySQLiteHelper(view.getContext(), view.getContext().getString(R.string.sqlite_database), null, 1);
                SQLiteDatabase sqlite=mMySQLite.getWritableDatabase();
                Cursor cursor=sqlite.rawQuery("select Description from "+view.getContext().getString(R.string.user_advice)+" where Number='"+callNumber.getText().toString()+"'", null);
                if(cursor.getCount() != 0){
                    cursor.moveToFirst();
                    reportDlgEditText.setText(cursor.getString(0));
                }
                sqlite.close();
                cursor.close();

                originReportString=reportDlgEditText.getText().toString();//先記一下原本 EditText 的內容，之後有變動的話才需上傳雲端

                reportDlgButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        String reportString=reportDlgEditText.getText().toString();
                        if(!reportString.equals(originReportString)){
                            progressDialog=new ProgressDialog(view.getContext());
                            progressDialog.setMessage("處理中...");
                            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            progressDialog.setCancelable(false); //不能手動停止
                            progressDialog.show();
                            startReport(callNumber.getText().toString(), reportDlgEditText.getText().toString());
                        }else{
                            reportDialog.dismiss();
                        }
                    }
                });
                reportDialog.show();
            }
        }

        private void startReport(String number, String description){
            isReportError=false;
            new Thread(){
                @Override
                public void run(){
                    URL url;
                    HttpURLConnection connection;
                    DataOutputStream dataOStream;
                    SharedPreferences sharedP=callNumber.getContext().getSharedPreferences(callNumber.getContext().getString(R.string.whos_calls_shared_preference), Context.MODE_PRIVATE);
                    String user=sharedP.getString(callNumber.getContext().getString(R.string.user_account), "");
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date();
                    String joinDate=formatter.format(date);

                    try{
                        url=new URL("http://"+callNumber.getContext().getString(R.string.server_ip)+"/Report.php"); //請求的目標
                        connection=(HttpURLConnection)url.openConnection();

                        String parameters="user="+user+"&number="+number+"&description="+description+"&joinDate="+joinDate; //post的值，用&做連接
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

                        BufferedReader bf=new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        if(bf.readLine() != null){
                            isReportError=true;
                        }
                    }catch(Exception e){
                        isReportError=true;
                    }

                    if(!isReportError){//回報到 server 端成功的話，就可以把資料也存到本地端
                        MySQLiteHelper mMySQLite=new MySQLiteHelper(callNumber.getContext(), callNumber.getContext().getString(R.string.sqlite_database), null, 1);
                        SQLiteDatabase sqlite=mMySQLite.getWritableDatabase();

                        if(description.equals("")){
                            //表示這筆資料是要被刪的:https://jcgogo.pixnet.net/blog/post/21641005-%5Bandroid-sqlite%5D-sqlite-%E5%88%AA%E9%99%A4%E5%92%8C%E4%BF%AE%E6%94%B9%E8%B3%87%E6%96%99
                            sqlite.delete(callNumber.getContext().getString(R.string.user_advice), "Number='"+number+"'",null);
                        }else{
                            //判斷是改資料還是新增資料
                            Cursor cursor=sqlite.rawQuery("select * from "+callNumber.getContext().getString(R.string.user_advice)+" where Number='"+number+"'", null);
                            ContentValues values = new ContentValues();
                            values.put("Number", number);
                            values.put("Description", description);
                            values.put("JoinDate", joinDate);
                            if(cursor.getCount() != 0){//表示要做的是改資料
                                sqlite.update(callNumber.getContext().getString(R.string.user_advice), values, "Number='"+number+"'", null);
                            }else{
                                sqlite.insert(callNumber.getContext().getString(R.string.user_advice), null ,values);
                            }
                        }

                        sqlite.close();
                    }

                    handler.post(afterReported);
                }
            }.start();
        }

        private Runnable afterReported=new Runnable(){
            public void run(){
                progressDialog.dismiss();
                reportDialog.dismiss();
                if(isReportError){
                    altDlgBuilder.setTitle("錯誤 !!");
                    altDlgBuilder.setMessage("更新失敗，請確認網路是否有連上");
                    altDlgBuilder.setIcon(android.R.drawable.ic_dialog_alert);
                }else{
                    altDlgBuilder.setTitle("成功 !!");
                    altDlgBuilder.setMessage("更新成功 !!");
                    altDlgBuilder.setIcon(android.R.drawable.ic_dialog_info);
                }

                altDlgBuilder.show();
            }
        };
    }

    public RecyclerViewAdapter(List<String> numberString, List<String> dateString,List<String> stateString, List<String> resultString){
        mNumberString=numberString;
        mDateString=dateString;
        mStateString=stateString;
        mResultString=resultString;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i){
        View v= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_view_item, viewGroup, false);
        ViewHolder viewHolder=new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {
        //改 textView 字體的顏色:https://stackoverflow.com/questions/4602902/how-to-set-the-text-color-of-textview-in-code
        holder.callNumber.setText(mNumberString.get(position));
        holder.callDate.setText(mDateString.get(position));
        holder.callState.setText(mStateString.get(position));
        holder.callResult.setText(mResultString.get(position));


        String callStateString=holder.callState.getText().toString();
        if(callStateString.equals("去電")){
            holder.callState.setTextColor(Color.parseColor("#00BB00"));
        }else if(callStateString.equals("來電")){
            holder.callState.setTextColor(Color.parseColor("#00BB00"));
        }else if(callStateString.equals("未接")){
            holder.callState.setTextColor(Color.parseColor("#FF8000"));
        }else if(callStateString.equals("拒接")){
            holder.callState.setTextColor(Color.parseColor("#EA0000"));
        }
    }

    @Override
    public int getItemCount(){
        return mNumberString.size();
    }
}
