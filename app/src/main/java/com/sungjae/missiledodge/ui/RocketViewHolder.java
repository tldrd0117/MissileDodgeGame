package com.sungjae.missiledodge.ui;

import android.graphics.Rect;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by iseongjae on 2017. 8. 13..
 */

public class RocketViewHolder {
    View view;
    int width;

    public RocketViewHolder(View view, int width) {
        this.view = view;
        this.width = width;
    }

    public int getYPosition(){
        return (int) (((FrameLayout.LayoutParams) view.getLayoutParams()).topMargin + view.getTranslationY());
    }

    public int getXPosition(){
        return (int) (((FrameLayout.LayoutParams) view.getLayoutParams()).leftMargin + view.getTranslationX());
    }

    public Rect getRect(){
        Rect rect = new Rect();
        rect.top = getYPosition();
        rect.left = getXPosition() + width/3;
        rect.bottom = getYPosition() + width;
        rect.right = getXPosition() + width*2/3;
        return rect;
    }

}
