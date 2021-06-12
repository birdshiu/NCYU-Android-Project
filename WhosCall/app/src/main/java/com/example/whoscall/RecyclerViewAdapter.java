package com.example.whoscall;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{
    private List<String> mNumberString;
    List<String> mDateString;
    List<String> mStateString;
    private LinearLayout preShowedLinearLayout=null;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView callNumber, callResult, callState, callDate;

        public ViewHolder(View itemView){
            super(itemView);

            callDate=(TextView)itemView.findViewById(R.id.callDate);
            callState=(TextView)itemView.findViewById(R.id.callState);
            callNumber=(TextView) itemView.findViewById(R.id.callNumber);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view){
            Log.d("message", callNumber.getText().toString());
        }
    }

    public RecyclerViewAdapter(List<String> numberString, List<String> dateString,List<String> stateString){
        mNumberString=numberString;
        mDateString=dateString;
        mStateString=stateString;
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
        holder.callNumber.setText(mNumberString.get(position));
        holder.callDate.setText(mDateString.get(position));
        holder.callState.setText(mStateString.get(position));
    }

    @Override
    public int getItemCount(){
        return mNumberString.size();
    }
}
