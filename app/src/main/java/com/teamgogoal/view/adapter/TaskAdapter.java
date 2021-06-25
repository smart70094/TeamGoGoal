package com.teamgogoal.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.teamgogoal.dto.TaskDto;
import com.teamgogoal.dto.TaskListDto;
import com.teamgogoal.view.activity.R;

import java.util.List;
import java.util.Map;

public class TaskAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private TaskListDto taskListDto;

    static class ViewHolder {
        ImageView personal_photo;
        TextView missionName;
        ImageButton complete_btn;
    }

    public TaskAdapter(LayoutInflater layoutInflater, TaskListDto taskListDto) {
        this.layoutInflater = layoutInflater;
        this.taskListDto = taskListDto;
    }

    @Override
    public int getCount() {
        return taskListDto.getTasks().size();
    }

    @Override
    public Object getItem(int i) {
        return  taskListDto.getTasks().get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if(view == null) {
            view = layoutInflater.inflate(R.layout.task_list, null);
            viewHolder = new ViewHolder();
            viewHolder.personal_photo = view.findViewById(R.id.personal_photo);
            viewHolder.missionName = view.findViewById(R.id.missionName);
            viewHolder.complete_btn = view.findViewById(R.id.complete_btn);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        TaskDto taskDto = taskListDto.getTasks().get(i);
        String title = taskDto.getTitle();
        String iscomplete = taskDto.getIscomplete();

        viewHolder.missionName.setText(title);
        if ("Y".equalsIgnoreCase(iscomplete))
            viewHolder.complete_btn.setVisibility(View.VISIBLE);
        else
            viewHolder.complete_btn.setVisibility(View.INVISIBLE);

        return view;
    }

}
