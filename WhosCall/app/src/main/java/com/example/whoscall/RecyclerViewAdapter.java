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

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{
    private List<String> mListString;
    private LinearLayout preShowedLinearLayout=null;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView mTxt;

        public ViewHolder(View itemView){
            super(itemView);
            mTxt=(TextView) itemView.findViewById(R.id.txt);
            mTxt.setOnClickListener(this);
        }

        @Override
        public void onClick(View view){
            Log.d("message", ((TextView)view).getText().toString());
        }
    }

    public RecyclerViewAdapter(List<String> listString){
        mListString=listString;
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
        holder.mTxt.setText(mListString.get(position));
    }

    @Override
    public int getItemCount(){
        return mListString.size();
    }
}
