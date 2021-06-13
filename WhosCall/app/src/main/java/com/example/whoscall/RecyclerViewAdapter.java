package com.example.whoscall;

import android.app.Dialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{
    private List<String> mNumberString;
    private List<String> mDateString;
    private List<String> mStateString;
    private List<String> mResultString;
    //private Dialog reportDialog;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView callNumber, callResult, callState, callDate;

        public ViewHolder(View itemView){
            super(itemView);

            callDate=(TextView)itemView.findViewById(R.id.callDate);
            callState=(TextView)itemView.findViewById(R.id.callState);
            callNumber=(TextView) itemView.findViewById(R.id.callNumber);
            callResult=(TextView)itemView.findViewById(R.id.callResult);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view){
            if(!callNumber.getText().toString().equals("不明的號碼")){
                Dialog reportDialog=new Dialog(view.getContext());
                reportDialog.setContentView(R.layout.report_dlg);
                TextView reportNumberDlgText=reportDialog.findViewById(R.id.reportDlgNumberText);
                reportNumberDlgText.setText("號碼:\n"+callNumber.getText());
                reportDialog.show();
            }
        }
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
