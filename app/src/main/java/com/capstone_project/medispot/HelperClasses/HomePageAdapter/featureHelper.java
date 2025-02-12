package com.capstone_project.medispot.HelperClasses.HomePageAdapter;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

public class featureHelper {
    String text, description;
    ColorDrawable color;
    int lottieAnimationView;

    public featureHelper(int lottieAnimationView, String text, String description, ColorDrawable color) {
        this.lottieAnimationView = lottieAnimationView;
        this.text = text;
        this.description = description;
        this.color = color;
    }

    public Drawable getColor()  {
        return color;
    }

    public int getLottieAnimationView() {
        return lottieAnimationView;
    }

    public String getText() {
        return text;
    }

    public String getDescription() {
        return description;
    }


}
