package com.capstone_project.medispot.HelperClasses.HomePageAdapter;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.capstone_project.medispot.R;
import java.util.ArrayList;

public class featureAdapter extends RecyclerView.Adapter<featureAdapter.featuresHolders>{
    ArrayList<featureHelper> features;

    public featureAdapter(ArrayList<featureHelper> features) {
        this.features = features;
    }

    @NonNull
    @Override
    public featuresHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.features_cards,parent,false);
        featuresHolders featuresHolders = new featuresHolders(view);
        return featuresHolders;
    }

    @Override
    public void onBindViewHolder(@NonNull featuresHolders holder, int position) {
        featureHelper featureHelper = features.get(position);


        holder.lottieAnimationView.setAnimation(featureHelper.getLottieAnimationView());
        holder.text.setText(featureHelper.getText());
        holder.description.setText(featureHelper.getDescription());
        holder.layout.setBackground(featureHelper.getColor());

    }

    @Override
    public int getItemCount() {
        return features.size();
    }


    public static class featuresHolders extends RecyclerView.ViewHolder{
        LottieAnimationView lottieAnimationView;
        TextView text, description;
        LinearLayout layout;


        public featuresHolders(@NonNull View itemView) {
            super(itemView);

            lottieAnimationView = itemView.findViewById(R.id.lottie);
            text = itemView.findViewById(R.id.Feature_text);
            description = itemView.findViewById(R.id.Feature_desc);
            layout = itemView.findViewById(R.id.background);

        }
    }
}
