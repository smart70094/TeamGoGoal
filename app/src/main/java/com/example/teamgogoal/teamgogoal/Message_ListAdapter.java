package com.example.teamgogoal.teamgogoal;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Message_ListAdapter extends BaseAdapter {
    private LayoutInflater myInflater;
    List<HashMap<String, String>> list = new ArrayList<>();
    HashMap<String,Drawable> originator_photo;

    public Message_ListAdapter(Context context,HashMap<String,Drawable> originator_photo) {
        myInflater = LayoutInflater.from(context);
        this.originator_photo = originator_photo;
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
        ViewHolder holder;

        if (convertView == null) {

            convertView = myInflater.inflate(R.layout.message_list, null);
            holder = new ViewHolder();
            holder.msg_context = (TextView) convertView.findViewById(R.id.msg_context);
            holder.personal_photo = (ImageView) convertView.findViewById(R.id.personal_photo);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.msg_context.setText(list.get(position).get("context"));
        holder.personal_photo.setImageDrawable(originator_photo.get(list.get(position).get("originator")));
        return convertView;
    }

    static class ViewHolder {
        ImageView personal_photo;
        TextView msg_context;
    }

}