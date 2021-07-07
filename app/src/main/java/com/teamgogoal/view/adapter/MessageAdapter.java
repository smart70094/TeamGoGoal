package com.teamgogoal.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.teamgogoal.teamgogoal.TargetEventActivity;
import com.teamgogoal.dto.MessageDto;
import com.teamgogoal.dto.ParticipantDto;
import com.teamgogoal.model.MessageModel;
import com.teamgogoal.model.ParticipantModel;
import com.teamgogoal.presenter.FilePresenter;
import com.teamgogoal.utils.TggRetrofitUtils;
import com.teamgogoal.view.activity.R;

import java.util.List;
import java.util.Objects;

import rx.Observable;
import rx.schedulers.Schedulers;

public class MessageAdapter extends BaseAdapter {

    private Context context;

    private LayoutInflater layoutInflater;

    private List<MessageDto> datas;

    private FilePresenter filePresenter;

    private ParticipantModel participantModel;

    private MessageModel messageModel;

    private ViewHolder holder;

    private AlertDialog cheerAlertDialog;

    public MessageAdapter(Context context, List<MessageDto> datas) {
        this.filePresenter = new FilePresenter();
        this.participantModel = new ParticipantModel();
        this.messageModel = new MessageModel();

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.datas = datas;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public MessageDto getItem(int i) {
        return datas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    private static class ViewHolder {
        ImageView headImage;
        TextView content;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(Objects.isNull(view)) {
            view = layoutInflater.inflate(R.layout.request_list, null);
            holder = new ViewHolder();

            holder.headImage = view.findViewById(R.id.personal_photo);
            holder.content = view.findViewById(R.id.request_context);
            view.setTag(holder);
        } else
            holder = (ViewHolder) view.getTag();

        MessageDto item = datas.get(i);

        switch (item.getType()) {
            case "1": //邀請加入目標訊息
                invite(view, holder, item);
                break;
            case "2": //鼓勵訊息
                cheer(view, holder, item);
                break;
            case "3": //目標更新訊息
                update(view, holder, item);
                break;
            case "4": //目標刪除訊息
                delete(view, holder, item);
                break;
            case "5": //要求鼓勵訊息
                askCheer(view, holder, item);
            default:
                break;
        }
        return view;
    }

    private void invite(View view, ViewHolder holder, MessageDto messageDto) {
        holder.content.setText(messageDto.getContent());
        MessageAdapter self = this;

        view.setOnClickListener((view1) -> {
                new AlertDialog.Builder(context)
                        .setTitle("TeamGoGoal")
                        .setIcon(R.mipmap.ic_launcher)
                        .setMessage(messageDto.getContent())
                        .setPositiveButton("取消", (dialog, which) -> {
//                            刪除參與人關聯表資料
                            ParticipantDto.Participant participant = getParticipant(messageDto.getTargetId());
                            Observable<Void> deleteParticipantObservable = participantModel.deleteParticipant(participant);
                            deleteParticipantObservable.subscribeOn(Schedulers.io()).subscribe();

//                            訊息更新為已讀
                            Observable<Void> updateReadedMessageObservable = messageModel.updateReadedMessage();
                            updateReadedMessageObservable.subscribeOn(Schedulers.io()).subscribe();

//                             更新畫面資料
                            datas.remove(messageDto);
                            self.notifyDataSetChanged();
                        })
                        .setNegativeButton("加入", (dialog, which) -> {
//                             接受目標邀請
                            Observable<Void> acceptInviteObservable = participantModel.acceptInvite();
                            acceptInviteObservable.subscribeOn(Schedulers.io()).subscribe();

//                             訊息更新為已讀
                            Observable<Void> updateReadedMessageObservable = messageModel.updateReadedMessage();
                            updateReadedMessageObservable.subscribeOn(Schedulers.io()).subscribe();

//                             更新畫面資料
                            datas.remove(messageDto);
                            self.notifyDataSetChanged();
                        })
                        .show();
        });
    }

    private void cheer(View view, ViewHolder holder, MessageDto messageDto) {
        holder.content.setText(messageDto.getFromName() + "寄給你一則訊息");
        MessageAdapter self = this;

        view.setOnClickListener((v)  -> {
                new AlertDialog.Builder(context)
                        .setTitle("TeamGoGoal")
                        .setIcon(R.mipmap.ic_launcher)
                        .setMessage("來自" + messageDto.getFromName() + "的訊息\n" + messageDto.getContent())
                        .setPositiveButton("確定", (dialog, which) -> {
//                             訊息更新為已讀
                            Observable<Void> updateReadedMessageObservable = messageModel.updateReadedMessage();
                            updateReadedMessageObservable.subscribeOn(Schedulers.io()).subscribe();

//                             更新畫面資料
                            datas.remove(messageDto);
                            self.notifyDataSetChanged();
                        })
                        .show();
        });
    }

    private void update(View view, ViewHolder holder, MessageDto messageDto) {
        holder.content.setText(messageDto.getFromName() + "已更新「" + messageDto.getContent() + "」目標的資訊");
        view.setOnClickListener((v) -> {
                new AlertDialog.Builder(context)
                        .setTitle("TeamGoGoal")
                        .setIcon(R.mipmap.ic_launcher)
                        .setMessage(messageDto.getFromId() + "已更新「" + messageDto.getContent() + "」目標的資訊")
                        .setPositiveButton("確定", (dialog, which) -> {
//                             訊息更新為已讀
                            Observable<Void> updateReadedMessageObservable = messageModel.updateReadedMessage();
                            updateReadedMessageObservable.subscribeOn(Schedulers.io()).subscribe();

//                            跳轉至目標編輯頁
                            Intent i = new Intent(context, TargetEventActivity.class);
                            i.putExtra("id", messageDto.getTargetId());
                            i.putExtra("model", "read");
                            context.startActivity(i);
                        })
                        .show();
        });
    }

    private void delete(View view, ViewHolder holder, MessageDto messageDto) {
        holder.content.setText(messageDto.getContent());
        MessageAdapter self = this;

        view.setOnClickListener((v) -> {
                //產生視窗物件
                new AlertDialog.Builder(context)
                        .setTitle("TeamGoGoal")//設定視窗標題
                        .setIcon(R.mipmap.ic_launcher)//設定對話視窗圖示
                        .setMessage(messageDto.getContent())//設定顯示的文字
                        .setPositiveButton("確定", (dialog, which) -> {
//                             訊息更新為已讀
                            Observable<Void> updateReadedMessageObservable = messageModel.updateReadedMessage();
                            updateReadedMessageObservable.subscribeOn(Schedulers.io()).subscribe();

//                             更新畫面資料
                            datas.remove(messageDto);
                            self.notifyDataSetChanged();
                        })
                        .show();
        });
    }

    private void askCheer(View view, ViewHolder holder, MessageDto messageDto) {
        holder.content.setText(messageDto.getContent());
        MessageAdapter self = this;

        view.setOnClickListener((v) -> {

                View dialogView = LayoutInflater.from(context).inflate(R.layout.input_message, null);
                Button submitCheer = dialogView.findViewById(R.id.submitCheer);

                submitCheer.setOnClickListener((v1) -> {
//                    傳送鼓勵訊息
                    Observable<Void> sendCheerMessageObservable = messageModel.sendCheerMessage();
                    sendCheerMessageObservable.subscribeOn(Schedulers.io()).subscribe();

//                     息更新為已讀
                    Observable<Void> updateReadedMessageObservable = messageModel.updateReadedMessage();
                    updateReadedMessageObservable.subscribeOn(Schedulers.io()).subscribe();

//                    新畫面資料
                    datas.remove(messageDto);
                    self.notifyDataSetChanged();
                });

                cheerAlertDialog = new AlertDialog.Builder(context, R.style.hitStyle).setView(dialogView).create();
                cheerAlertDialog.show();
        });
    }

    private ParticipantDto.Participant getParticipant(int targetId) {
                return ParticipantDto.Participant.builder()
                            .account(TggRetrofitUtils.getAccount())
                            .targetId(targetId)
                            .build();
    }
}
