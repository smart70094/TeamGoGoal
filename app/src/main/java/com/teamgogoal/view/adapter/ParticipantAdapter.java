package com.teamgogoal.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.teamgogoal.dto.ParticipantDto;
import com.teamgogoal.jpa.entity.File;
import com.teamgogoal.model.FileModel;
import com.teamgogoal.presenter.FilePresenter;
import com.teamgogoal.utils.ToastUtils;
import com.teamgogoal.view.activity.R;
import com.teamgogoal.view.interfaces.FileView;

import java.util.List;
import java.util.Objects;

public class ParticipantAdapter extends BaseAdapter {

    private Context context;

    private LayoutInflater layoutInflater;

    private List<ParticipantDto.Participant> datas;

    private FilePresenter filePresenter;

    public ParticipantAdapter(Context context, List<ParticipantDto.Participant> data) {
        this.filePresenter = new FilePresenter();

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.datas = data;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public ParticipantDto.Participant getItem(int i) {
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

        ParticipantDto.Participant item = getItem(i);
        holder.name.setText(item.getName());

        filePresenter.initHeadImage(context, item.getHeadImageId(), holder.personPhoto);

        return view;
    }
}
