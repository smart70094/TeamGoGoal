package com.example.teamgogoal.teamgogoal;

import android.graphics.drawable.Drawable;

public class GalleryModel {

    private int imageView;
    private String text;
    private Drawable drawable;

    public GalleryModel(int imageView, String text, Drawable drawable) {
        super();
        this.imageView = imageView;
        this.text = text;
        this.drawable = drawable;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getImageView() {
        return imageView;
    }

    public void setImageView(int imageView) {
        this.imageView = imageView;
    }

    public Drawable getDrawable(){return drawable;}

}
