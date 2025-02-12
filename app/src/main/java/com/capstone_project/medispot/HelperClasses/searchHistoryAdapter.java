package com.capstone_project.medispot.HelperClasses;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone_project.medispot.CommonFiles.savedDrugs;
import com.capstone_project.medispot.R;

import java.util.ArrayList;

public class searchHistoryAdapter extends RecyclerView.Adapter <searchHistoryAdapter.MyHistoryViewHolder> {

    Context context;
    ArrayList<savedDrugs> arrayList;

    private onClickListener listener;
    private onClickListener history;

    public searchHistoryAdapter(Context context, ArrayList<savedDrugs> arrayList, onClickListener onClickListener, onClickListener remove) {
        this.context = context;
        this.arrayList = arrayList;
        this.listener = onClickListener;
        this.history = remove;
    }

    @NonNull
    @Override
    public MyHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.search_history, parent,false);
        return new MyHistoryViewHolder(v, listener,history);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHistoryViewHolder holder, int position) {
        savedDrugs drug = arrayList.get(position);
        holder.drugName.setText(drug.getDrugName());
        holder.date.setText(drug.getTimestamp().toDate().toString());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class MyHistoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView drugName;
        TextView date;
        onClickListener onClickListener;
        onClickListener savedListener;
        ImageView delete;


        public MyHistoryViewHolder(@NonNull View itemView, onClickListener onClickListener, onClickListener historyListener) {
            super(itemView);
            drugName = itemView.findViewById(R.id.drugName);
            date = itemView.findViewById(R.id.timestamp);
            delete = itemView.findViewById(R.id.remove);

            this.onClickListener = onClickListener;
            itemView.setOnClickListener(this);

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(historyListener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            historyListener.onDelete(position);
                        }
                    }
                }
            });
        }

        @Override
        public void onClick(View view) {
            onClickListener.onClick(getAdapterPosition());
        }
    }

    public interface onClickListener{
        void onClick(int position);
        void onDelete(int position);
    }
}
