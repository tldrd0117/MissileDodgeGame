package com.sungjae.missiledodge.ui;

import android.graphics.Rect;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by iseongjae on 2017. 8. 13..
 */

public class DiamondViewHolder {
    View view;
    int speed;
    int width;

    public DiamondViewHolder(View view, int speed,int width) {
        this.view = view;
        this.speed = speed;
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
        rect.left = getXPosition();
        rect.bottom = getYPosition() + width;
        rect.right = getXPosition() + width;
        return rect;
    }
}
