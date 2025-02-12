package com.capstone_project.medispot.HelperClasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone_project.medispot.CommonFiles.drugs;
import com.capstone_project.medispot.R;

import java.util.ArrayList;

public class drugListAdapter extends RecyclerView.Adapter <drugListAdapter.MyViewHolder> {

    Context context;
    ArrayList<drugs> arrayList;

    private onClickListener listener;

    public drugListAdapter(Context context, ArrayList<drugs> arrayList, onClickListener onClickListener) {
        this.context = context;
        this.arrayList = arrayList;
        this.listener = onClickListener;
    }

    @NonNull
    @Override
    public drugListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.drugs, parent,false);
        return new MyViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull drugListAdapter.MyViewHolder holder, int position) {

        drugs drug = arrayList.get(position);
        holder.drugName.setText(drug.getDrugName());
        holder.Document_ReferenceID.setText(drug.getDocument_ReferenceID());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView drugName, Document_ReferenceID;
        onClickListener onClickListener;

        public MyViewHolder(@NonNull View itemView, onClickListener onClickListener) {
            super(itemView);
            drugName = itemView.findViewById(R.id.drugName);
            Document_ReferenceID = itemView.findViewById(R.id.reference);

            this.onClickListener = onClickListener;
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            onClickListener.onClick(getAdapterPosition());
        }
    }

    public interface onClickListener{
        void onClick(int position);
    }
}
