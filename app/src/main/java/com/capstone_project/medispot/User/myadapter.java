package com.capstone_project.medispot.User;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone_project.medispot.CommonFiles.drugs;
import com.capstone_project.medispot.HelperClasses.drugListAdapter;
import com.capstone_project.medispot.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class myadapter extends FirestoreRecyclerAdapter<drugs,myadapter.myviewholder>
{

    private drugListAdapter.onClickListener listener;

    public myadapter(@NonNull FirestoreRecyclerOptions<drugs> options) {
        super(options);
    }

    @Override
    public void onBindViewHolder(@NonNull myviewholder holder, int position,  @NonNull drugs model) {

        holder.drugName.setText(model.getDrugName());
        holder.Document_ReferenceID.setText(model.getDocument_ReferenceID());
    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
       View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.drugs,parent,false);
       return new myviewholder(view);
    }


    public static class myviewholder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView drugName, Document_ReferenceID;
        drugListAdapter.onClickListener onClickListener;

        public myviewholder(@NonNull View itemView) {
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
}
