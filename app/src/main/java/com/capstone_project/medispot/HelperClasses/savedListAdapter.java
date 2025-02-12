package com.capstone_project.medispot.HelperClasses;


import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.capstone_project.medispot.CommonFiles.savedDrugs;
import com.capstone_project.medispot.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class savedListAdapter extends RecyclerView.Adapter <savedListAdapter.MySavedViewHolder> {

    Context context;
    ArrayList<savedDrugs> arrayList;

    private onClickListener listener;
    private onClickListener saved;

    public savedListAdapter(Context context, ArrayList<savedDrugs> arrayList, onClickListener onClickListener, onClickListener remove) {
        this.context = context;
        this.arrayList = arrayList;
        this.listener = onClickListener;
        this.saved = remove;
    }

    @NonNull
    @Override
    public MySavedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.saved_drugs, parent,false);
        return new MySavedViewHolder(v, listener,saved);
    }

    @Override
    public void onBindViewHolder(@NonNull MySavedViewHolder holder, int position) {

        savedDrugs drug = arrayList.get(position);
        holder.drugName.setText(drug.getDrugName());
        holder.date.setText(drug.getDate());
        holder.DrugImgURL.setImageURI(Uri.parse(drug.getDrugImgURL()));
        Picasso.get().load(drug.getDrugImgURL()).into(holder.DrugImgURL);

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class MySavedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView drugName, date;
        ImageView DrugImgURL;
        onClickListener onClickListener;
        onClickListener savedListener;
        ImageView delete;


        public MySavedViewHolder(@NonNull View itemView, onClickListener onClickListener, onClickListener savedListener) {
            super(itemView);
            drugName = itemView.findViewById(R.id.drugName);
            date = itemView.findViewById(R.id.date);
            DrugImgURL = itemView.findViewById(R.id.image);
            delete = itemView.findViewById(R.id.save);


            this.onClickListener = onClickListener;
            itemView.setOnClickListener(this);

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(savedListener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            savedListener.onDelete(position);
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
