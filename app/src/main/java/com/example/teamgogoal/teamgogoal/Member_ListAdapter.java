package com.example.teamgogoal.teamgogoal;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.teamgogoal.view.activity.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Member_ListAdapter extends BaseAdapter {
    Context context;
    ViewHolder holder;
    private LayoutInflater myInflater;
    List<HashMap<String, String>> list = new ArrayList<>();

    public Member_ListAdapter(Context context) {
        myInflater = LayoutInflater.from(context);
        this.context = context;
    }

    public void setData(List<HashMap<String, String>> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {

            convertView = myInflater.inflate(R.layout.member_item, null);
            holder = new ViewHolder();
            holder.personal_photo = (ImageView) convertView.findViewById(R.id.personal_photo);
            holder.member_name = (TextView)convertView.findViewById(R.id.member_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //set Photo
        try {
            Drawable photo = TargetActivity.photo_handle.getPhotoByID(list.get(position).get("uid"));
            if (photo == null) {
                holder.personal_photo.setImageDrawable(context.getResources().getDrawable(R.drawable.item_person_photo,null));
            } else {
                holder.personal_photo.setImageDrawable(photo);
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        holder.member_name.setText(list.get(position).get("account").toString());

        return convertView;
    }

    static class ViewHolder {
        ImageView personal_photo;
        TextView member_name;
    }

}