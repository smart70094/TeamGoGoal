package com.teamgogoal.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.teamgogoal.dto.ParticipantDto;
import com.teamgogoal.view.activity.R;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ParticipantAdapter extends BaseAdapter {

    private Context context;

    private LayoutInflater layoutInflater;

    private List<ParticipantDto> datas;

    public ParticipantAdapter(Context context, List<ParticipantDto> data) {
        this.context = context;
        this.datas = data;
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

    private static class ViewHolder {
        ImageView personPhoto;
        TextView name;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;

        if(Objects.isNull(view)) {
            view = layoutInflater.inflate(R.layout.member_item, null);
            holder = new ViewHolder();
            holder.personPhoto = view.findViewById(R.id.personal_photo);
            holder.name = view.findViewById(R.id.member_name);
            view.setTag(holder);
        } else
            holder = (ViewHolder) view.getTag();

        Map<String, Object> item = (Map) getItem(i);
        holder.name.setText((String) item.get("name"));

        return view;
    }
}
