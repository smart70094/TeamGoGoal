package com.example.teamgogoal.teamgogoal;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;

import com.lukedeighton.wheelview.adapter.WheelArrayAdapter;

import java.util.List;

public class PictureAdapter2 extends WheelArrayAdapter {

    private Context context;
    private int selectItem;
    private List<GalleryModel> list;

    public PictureAdapter2(List<GalleryModel> list, Context context) {
        super(list);
    }


    @Override
    public Drawable getDrawable(int position) {
        GalleryModel gm = (GalleryModel) getItem(position);

        Drawable b = gm.getDrawable();
        Drawable[] drawable = new Drawable[]{
                b,
                new TextDrawable(gm.getText())
        };
        return new LayerDrawable(drawable);
    }
}
