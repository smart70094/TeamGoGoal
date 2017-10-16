package com.example.teamgogoal.teamgogoal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class task_listadapter extends BaseAdapter {
    private LayoutInflater myInflater;
    List<HashMap<String, String>> list = new ArrayList<>();

    public task_listadapter(Context context) {
        myInflater = LayoutInflater.from(context);

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

            convertView = myInflater.inflate(R.layout.task_list, null);
            holder = new ViewHolder();
            holder.missionName = (TextView) convertView.findViewById(R.id.missionName);

            //圖片區
            //holder.dateAndTime = (TextView) convertView.findViewById(R.id.dataAndTime);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.missionName.setText(list.get(position).get("missionName"));

        //圖片
        //holder.dateAndTime.setText(list.get(position).get("date"));


        return convertView;
    }

    static class ViewHolder {
        ImageView personal_photo;
        TextView missionName;
    }

}