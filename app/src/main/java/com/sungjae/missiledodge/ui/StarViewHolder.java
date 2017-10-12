package com.sungjae.missiledodge.ui;

import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by iseongjae on 2017. 8. 13..
 */

public class StarViewHolder {
    View view;
    int speed;


    public StarViewHolder(View view, int speed) {
        this.view = view;
        this.speed = speed;
    }

    public int getYPosition(){
        return (int) (((FrameLayout.LayoutParams) view.getLayoutParams()).topMargin + view.getTranslationY());
    }

    public int getXPosition(){
        return (int) (((FrameLayout.LayoutParams) view.getLayoutParams()).leftMargin + view.getTranslationX());
    }
}
