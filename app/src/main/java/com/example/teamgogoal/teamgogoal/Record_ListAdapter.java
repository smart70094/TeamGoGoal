package com.example.teamgogoal.teamgogoal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.teamgogoal.view.activity.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Record_ListAdapter extends BaseAdapter {
    private LayoutInflater myInflater;
    List<HashMap<String, String>> list = new ArrayList<>();

    public Record_ListAdapter(Context context) {
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
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {

            convertView = myInflater.inflate(R.layout.record_list, null);
            holder = new ViewHolder();
            holder.message = (TextView) convertView.findViewById(R.id.message);
            holder.dateAndTime = (TextView) convertView.findViewById(R.id.dataAndTime);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.message.setText(list.get(position).get("recordText"));
        holder.dateAndTime.setText(list.get(position).get("date"));


        return convertView;
    }

    static class ViewHolder {
        TextView message;
        TextView dateAndTime;
    }

}