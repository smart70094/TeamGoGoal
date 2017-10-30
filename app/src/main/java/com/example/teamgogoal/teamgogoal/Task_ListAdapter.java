package com.example.teamgogoal.teamgogoal;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Task_ListAdapter extends BaseAdapter {
    Context context;
    private LayoutInflater myInflater;
    List<HashMap<String, String>> list = new ArrayList<>();

    public Task_ListAdapter(Context context) {
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
        ViewHolder holder;

        if (convertView == null) {

            convertView = myInflater.inflate(R.layout.task_list, null);
            holder = new ViewHolder();
            holder.missionName = (TextView) convertView.findViewById(R.id.missionName);
            holder.linearlayout_bg = (LinearLayout) convertView.findViewById(R.id.LinearLayout_bg);
            holder.complete_btn = (ImageButton) convertView.findViewById(R.id.complete_btn);

            //圖片區
            //holder.dateAndTime = (TextView) convertView.findViewById(R.id.dataAndTime);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String auth = list.get(position).get("auth").trim();
        String state = list.get(position).get("state").trim();

        holder.missionName.setText(list.get(position).get("missionName"));
        if(auth.equals(LoginActivity.user.account)){
            holder.linearlayout_bg.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_record_msg));

            if(state.equals("Y")){
                holder.complete_btn.setVisibility(View.INVISIBLE);
            }else {
                holder.complete_btn.setVisibility(View.VISIBLE);
            }
        }else{
            holder.linearlayout_bg.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_task_other_complete));
            holder.complete_btn.setVisibility(View.INVISIBLE);
        }



        holder.missionName.setText(list.get(position).get("missionName"));

        //圖片
        //holder.dateAndTime.setText(list.get(position).get("date"));


        return convertView;
    }

    static class ViewHolder {
        ImageView personal_photo;
        TextView missionName;
        LinearLayout linearlayout_bg;
        ImageButton complete_btn;
    }

}