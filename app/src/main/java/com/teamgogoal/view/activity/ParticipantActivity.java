package com.teamgogoal.view.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.GridView;

import com.teamgogoal.dto.ParticipantDto;
import com.teamgogoal.presenter.ParticipantPresenter;
import com.teamgogoal.utils.ProgressDialogUtils;
import com.teamgogoal.utils.ToastUtils;
import com.teamgogoal.view.adapter.ParticipantAdapter;
import com.teamgogoal.view.interfaces.ParticipantView;

import java.util.List;

public class ParticipantActivity extends AppCompatActivity implements ParticipantView {

    private ParticipantPresenter participantPresenter;

    private ProgressDialog progressDialog;

//    群組會員
    private ParticipantAdapter participantAdapter;
    private GridView participantGridView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);
        participantGridView = findViewById(R.id.attendMember);

        participantPresenter = new ParticipantPresenter(this);

        progressDialog = ProgressDialogUtils.create(this);

        loadingParticipant();
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

    private void loadingParticipant() {
        participantPresenter.getParticipant();
    }

    @Override
    public void initialParticipantAdapter(List<ParticipantDto> datas) {
        participantAdapter = new ParticipantAdapter(this, datas);

        participantGridView.setNumColumns(5);
        participantGridView.setAdapter(participantAdapter);
        participantGridView.setOnItemClickListener((adapterView, view, i, l)->ToastUtils.showShortMessage(this, "wait"));

    }
}
