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
import com.capstone_project.medispot.CommonFiles.PillReminder;
import com.capstone_project.medispot.R;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class PillReminderAdapter extends RecyclerView.Adapter <PillReminderAdapter.MyReminderViewHolder> {

    Context context;
    ArrayList<PillReminder> arrayList;
    private onClickListener delete;

    public PillReminderAdapter(Context context, ArrayList<PillReminder> arrayList, onClickListener remove) {
        this.context = context;
        this.arrayList = arrayList;
        this.delete = remove;
    }

    @NonNull
    @Override
    public MyReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.reminder, parent,false);
        return new MyReminderViewHolder(v,delete);
    }

    @Override
    public void onBindViewHolder(@NonNull MyReminderViewHolder holder, int position) {

        PillReminder drug = arrayList.get(position);
        holder.drugName.setText(drug.getDrugName());
        holder.quantity.setText(drug.getDrugQuantity());
        holder.type.setText(drug.getDrugType());
        holder.schedule.setText(drug.getDrugSchedule());
        holder.time.setText(drug.getTime());
        holder.date.setText(drug.getDate());
        holder.instruction.setText(drug.getDrugInstructions());
        holder.DrugImgURL.setImageURI(Uri.parse(drug.getDrugImgURL()));
        Picasso.get().load(drug.getDrugImgURL()).into(holder.DrugImgURL);

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class MyReminderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView drugName, quantity, type, schedule, instruction, time,date;
        ImageView DrugImgURL;
        ImageView delete;


        public MyReminderViewHolder(@NonNull View itemView, onClickListener savedListener) {
            super(itemView);
            drugName = itemView.findViewById(R.id.drugName);
            quantity = itemView.findViewById(R.id.quantity);
            type = itemView.findViewById(R.id.type);
            schedule = itemView.findViewById(R.id.schedule);
            instruction = itemView.findViewById(R.id.instruction);
            time = itemView.findViewById(R.id.time);
            date = itemView.findViewById(R.id.date);
            DrugImgURL = itemView.findViewById(R.id.image);
            delete = itemView.findViewById(R.id.save);

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

        }
    }

    public interface onClickListener{
        void onDelete(int position);
    }
}
