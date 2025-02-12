package com.capstone_project.medispot.HelperClasses.SearchOptionsAdapter;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

public class searchPageHelper {
    String text, description;
    ColorDrawable color;
    int lottieAnimationView;

    public searchPageHelper(int lottieAnimationView, String text, String description, ColorDrawable color) {
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
