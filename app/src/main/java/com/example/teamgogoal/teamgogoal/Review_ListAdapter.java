package com.example.teamgogoal.teamgogoal;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lukedeighton.wheelview.adapter.WheelArrayAdapter;

import java.util.List;

public class Review_ListAdapter extends WheelArrayAdapter {

    ViewHolder holder;
    private LayoutInflater myInflater;
    private Context context;

    public Review_ListAdapter(List<GalleryModel> list, Context context) {
        super(list);
        this.context = context;
        myInflater = LayoutInflater.from(context);
    }


    @Override
    public Drawable getDrawable(int position) {
        View convertView = null;
        if (convertView == null) {

            convertView = myInflater.inflate(R.layout.item_review, null);
            holder = new ViewHolder();
            holder.planet = (ImageView) convertView.findViewById(R.id.planet);
            //holder.targetName = (TextView)convertView.findViewById(R.id.targetName);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        GalleryModel gm = (GalleryModel) getItem(position);
        //set Photo
        holder.planet.setImageDrawable((Drawable) gm.getDrawable());
        //holder.targetName.setText(gm.getText());

        convertView.setDrawingCacheEnabled(true);

        convertView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        convertView.layout(0, 0, convertView.getMeasuredWidth(), convertView.getMeasuredHeight());

        convertView.buildDrawingCache(true);
        Bitmap bitmap = Bitmap.createBitmap(convertView.getDrawingCache());

        convertView.setDrawingCacheEnabled(false);

        Drawable d = new BitmapDrawable(context.getResources(), bitmap);
        return d;
    }

    static class ViewHolder {
        ImageView planet;
        TextView targetName;
    }
}
