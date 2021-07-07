package com.teamgogoal.view.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import com.teamgogoal.dto.ParticipantDto;
import com.teamgogoal.presenter.ParticipantPresenter;
import com.teamgogoal.utils.DialogUtils;
import com.teamgogoal.utils.EditTextUtils;
import com.teamgogoal.utils.ProgressDialogUtils;
import com.teamgogoal.utils.TggRetrofitUtils;
import com.teamgogoal.utils.ToastUtils;
import com.teamgogoal.view.adapter.ParticipantAdapter;
import com.teamgogoal.view.interfaces.ParticipantView;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import butterknife.OnClick;

public class ParticipantActivity extends AppCompatActivity implements ParticipantView {

    private ParticipantPresenter participantPresenter;

    private ProgressDialog progressDialog;

    private int targetId;

    private Dialog inviteParticipantDialog;

    private Dialog deleteParticipantDialog;

//    群組會員
    private ParticipantAdapter participantAdapter;
    private GridView participantGridView;
    private List<ParticipantDto.Participant> participantList;

//    邀請中會員
    private ParticipantAdapter inviteParticipantAdapter;
    private GridView inviteParticipantGridView;
    private List<ParticipantDto.Participant> inviteParticipantList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);

        participantGridView = findViewById(R.id.attendMember);
        inviteParticipantGridView = findViewById(R.id.inviteMember);

        participantPresenter = new ParticipantPresenter(this);

        progressDialog = ProgressDialogUtils.create(this);

        initTargetId();

        loadingParticipant();
    }

    private void deleteParticipant(int position, Boolean isInviteParticipant) {
        ParticipantDto.Participant participant;
        BaseAdapter baseAdapter;

        if(isInviteParticipant) {
            participant = inviteParticipantList.remove(position);
            baseAdapter = inviteParticipantAdapter;
        } else {
            participant = participantList.remove(position);
            baseAdapter = participantAdapter;
        }

        participant.setTargetId(targetId);

        Boolean isSelf = TggRetrofitUtils.getAccount().equals(participant.getAccount());


        View dialog_view = LayoutInflater.from(this).inflate(R.layout.member_selector, null);
        Button button_delete_member = dialog_view.findViewById(R.id.button_delete_member);
        if(isSelf)
            button_delete_member.setText("退出目標");

        button_delete_member.setOnClickListener((view) -> {
                deleteParticipantDialog.dismiss();
                participantPresenter.deleteParticipant(participant);

                baseAdapter.notifyDataSetChanged();
                if(isSelf){
                    setResult(RESULT_OK);
                    switchView(TargetActivity.class);
                }
        });

        deleteParticipantDialog = DialogUtils.createDialog(this, dialog_view, R.style.hitStyle);
        deleteParticipantDialog.show();
    }

    @Override
    public <T> void switchView(Class<T> activityClass) {
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
    }

    @Override
    public void showMessage(String message) {
        ToastUtils.showShortMessage(this, message);
    }

    @Override
    public void dismissProgressDialog() {
        progressDialog.dismiss();
    }

    private void initTargetId() {
        Bundle bundle = getIntent().getExtras();
        if(bundle != null)
            targetId = bundle.getInt("TargetId");
    }

    private void loadingParticipant() {
        participantPresenter.getParticipant(targetId);
    }

    @Override
    public void initialParticipantAdapter(ParticipantDto datas) {
        List<ParticipantDto.Participant> participantList = datas.getParticipants();

        Map<Boolean, List<ParticipantDto.Participant>> groupDatas =
                participantList.stream()
                        .collect(Collectors.partitioningBy((participant)-> participant.getIsJoin().equalsIgnoreCase("Y")));

        this.participantList = groupDatas.get(Boolean.TRUE);
        inviteParticipantList = groupDatas.get(Boolean.FALSE);

        participantAdapter = new ParticipantAdapter(this, this.participantList);
        participantGridView.setNumColumns(5);
        participantGridView.setAdapter(participantAdapter);
        participantGridView.setOnItemClickListener((adapterView, view, i, l) -> deleteParticipant(i, false));

        inviteParticipantAdapter = new ParticipantAdapter(this, inviteParticipantList);
        inviteParticipantGridView.setNumColumns(5);
        inviteParticipantGridView.setAdapter(inviteParticipantAdapter);
        inviteParticipantGridView.setOnItemClickListener((adapterView, view, i, l)->deleteParticipant(i, true));

    }

    @OnClick(R.id.button7)
    public void inviteParticipant(View view) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.add_member_dialog, null);
        EditText memberID = dialogView.findViewById(R.id.memberID);
        Button addMemberBtn = dialogView.findViewById(R.id.addMemberBtn);

        addMemberBtn.setOnClickListener((v)-> {
                ParticipantDto.Participant participant =
                        ParticipantDto.Participant.builder()
                                .account(EditTextUtils.getText(memberID))
                                .targetId(targetId)
                                .build();

                participantPresenter.inviteParticipant(participant);
        });

        inviteParticipantDialog = DialogUtils.createDialog(this, dialogView, R.style.hitStyle);
        inviteParticipantDialog.show();
    }

    @OnClick(R.id.imageButton18)
    public void moveTarget(View view) {
        switchView(TargetActivity.class);
    }

    @Override
    public void inviteParticipantSuccess() {
        loadingParticipant();
        inviteParticipantDialog.dismiss();
        DialogUtils.showHit(this, "TeamGoGoal", "已送出邀請，等待對方回應");
    }
}
