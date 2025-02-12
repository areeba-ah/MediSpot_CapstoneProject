package com.capstone_project.medispot.HelperClasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.capstone_project.medispot.CommonFiles.drugs;
import com.capstone_project.medispot.R;

import java.util.ArrayList;

public class drugResultAdapter extends RecyclerView.Adapter <drugResultAdapter.MyViewHolder> {

    Context context;
    ArrayList<drugs> arrayList;

    private onClickListener listener;

    public drugResultAdapter(Context context, ArrayList<drugs> arrayList, onClickListener onClickListener) {
        this.context = context;
        this.arrayList = arrayList;
        this.listener = onClickListener;
    }

    @NonNull
    @Override
    public drugResultAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.drug_results, parent,false);
        return new MyViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull drugResultAdapter.MyViewHolder holder, int position) {

        drugs drug = arrayList.get(position);
        holder.drugName.setText(drug.getDrugName());
        holder.brandName.setText(drug.getBrandName());
        Glide.with(context).load(drug.getFrontImage()).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView drugName, brandName;
        ImageView image;
        onClickListener onClickListener;

        public MyViewHolder(@NonNull View itemView, onClickListener onClickListener) {
            super(itemView);
            drugName = itemView.findViewById(R.id.drugName);
            brandName = itemView.findViewById(R.id.name);
            image = itemView.findViewById(R.id.image);

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
