package com.example.teamgogoal.teamgogoal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.teamgogoal.view.activity.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Target_ListAdapter extends BaseAdapter {
    private LayoutInflater myInflater;
    List<HashMap<String, String>> list = new ArrayList<>();

    public Target_ListAdapter(Context context) {
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

            convertView = myInflater.inflate(R.layout.target_list, null);
            holder = new ViewHolder();
            holder.targetName = (TextView) convertView.findViewById(R.id.targetName);
            holder.targetDate = (TextView) convertView.findViewById(R.id.targetDate);
            holder.completeBar = (ProgressBar) convertView.findViewById(R.id.completeBar);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.targetName.setText(list.get(position).get("targetName"));
        holder.targetDate.setText(list.get(position).get("targetDate"));

        double percent = Double.valueOf(list.get(position).get("completemission")) / Double.valueOf(list.get(position).get("allmission"));
        holder.completeBar.setProgress((int) (percent*100 + 0.5));

        //圖片
        //holder.dateAndTime.setText(record_list.get(position).get("date"));


        return convertView;
    }

    static class ViewHolder {
        TextView targetName;
        TextView targetDate;
        ProgressBar completeBar;
    }

}