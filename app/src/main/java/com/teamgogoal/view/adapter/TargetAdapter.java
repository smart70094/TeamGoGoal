package com.teamgogoal.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.teamgogoal.teamgogoal.Target_ListAdapter;
import com.teamgogoal.utils.DateUtils;
import com.teamgogoal.view.activity.R;

import java.util.List;
import java.util.Map;

public class TargetAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private List<Map<String, Object>> datas;

    static class ViewHolder {
        TextView titleTextView;
        TextView durationDateTextView;
        ProgressBar completeBar;
    }

    public TargetAdapter(LayoutInflater layoutInflater, List<Map<String, Object>> datas) {
        this.layoutInflater = layoutInflater;
        this.datas = datas;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int i) {
        return datas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if(view == null) {
            view = layoutInflater.inflate(R.layout.target_list, null);
            viewHolder = new ViewHolder();
            viewHolder.titleTextView = view.findViewById(R.id.targetName);
            viewHolder.durationDateTextView = view.findViewById(R.id.targetDate);
            viewHolder.completeBar = view.findViewById(R.id.completeBar);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        Map<String, Object> data = datas.get(i);
        String title = (String) data.get("title");
        String startdate = (String) data.get("startdate");
        String enddate = (String) data.get("enddate");
        String durationDate = DateUtils.addDotDate(startdate) + "-" + DateUtils.addDotDate(enddate);

        viewHolder.titleTextView.setText(title);
        viewHolder.durationDateTextView.setText(durationDate);
        viewHolder.completeBar.setProgress(50);

        return view;
    }
}
