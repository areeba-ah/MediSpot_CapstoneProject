package com.capstone_project.medispot.HelperClasses.SearchOptionsAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.capstone_project.medispot.R;

import java.util.ArrayList;

public class searchAdapter extends RecyclerView.Adapter<searchAdapter.searchHolders>{
    ArrayList<searchPageHelper> search_page;

    public searchAdapter(ArrayList<searchPageHelper> features) {
        this.search_page = features;
    }

    @NonNull
    @Override
    public searchHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_page_cards,parent,false);
        searchHolders searchHolders = new searchHolders(view);
        return searchHolders;
    }

    @Override
    public void onBindViewHolder(@NonNull searchHolders holder, int position) {
        searchPageHelper searchPageHelper = search_page.get(position);

        holder.lottieAnimationView.setAnimation(searchPageHelper.getLottieAnimationView());
        holder.text.setText(searchPageHelper.getText());
        holder.description.setText(searchPageHelper.getDescription());
        holder.layout.setBackground(searchPageHelper.getColor());

    }

    @Override
    public int getItemCount() {
        return search_page.size();
    }


    public static class searchHolders extends RecyclerView.ViewHolder{
        LottieAnimationView lottieAnimationView;
        TextView text, description;
        LinearLayout layout;


        public searchHolders(@NonNull View itemView) {
            super(itemView);

            lottieAnimationView = itemView.findViewById(R.id.lottie);
            text = itemView.findViewById(R.id.text);
            description = itemView.findViewById(R.id.desc);
            layout = itemView.findViewById(R.id.background);

        }
    }
}
