package com.dark.canves.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

public class Frame extends RelativeLayout {

    public static int WrapContent = LayoutParams.WRAP_CONTENT;


    public Frame(Context context) {
        super(context);
        Init();
    }

    public Frame(Context context, AttributeSet attrs) {
        super(context, attrs);
        Init();
    }

    public Frame(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Init();
    }

    public Frame(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        Init();
    }

    public void Init(){

    }

    public void setWidthHeight(int widthInDp, int heightInDp) {
        int widthInPixels = 0;
        int heightInPixels = 0;

        if (widthInDp == WrapContent){
            widthInPixels = WrapContent;
        }else {
            if (heightInDp == WrapContent){
                heightInPixels = WrapContent;
            }else {
                float density = getContext().getResources().getDisplayMetrics().density;
                   widthInPixels = (int) (widthInDp * density);
                   heightInPixels  = (int) (heightInDp * density);
            }
        }
        // Set the width and height
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(widthInPixels, heightInPixels);
        setLayoutParams(params);
    }


    public void setBackground(int strokeWidth, int strokeColor, int cornerRadius, int bgColor) {
        // Create a drawable for the background
        GradientDrawable backgroundDrawable = new GradientDrawable();

        // Set stroke
        backgroundDrawable.setStroke(strokeWidth, strokeColor);

        // Set corner radius
        backgroundDrawable.setCornerRadius(cornerRadius);

        // Set background color
        backgroundDrawable.setColor(bgColor);

        // Set the drawable as background for the view
        setBackground(backgroundDrawable);
    }

    // Method to set background with stroke, corner radius, and background image
    public void setBackground(int strokeWidth, int strokeColor, int cornerRadius, Bitmap background) {
        // Create a GradientDrawable for stroke and corner radius
        GradientDrawable strokeDrawable = new GradientDrawable();
        strokeDrawable.setCornerRadius(cornerRadius);
        strokeDrawable.setStroke(strokeWidth, strokeColor);

        // Create a BitmapDrawable for the background image
        BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), background);

        // Create a LayerDrawable and add stroke and background image
        LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{bitmapDrawable, strokeDrawable});

        // Set the LayerDrawable as background for the view
        setBackground(layerDrawable);
    }

}
