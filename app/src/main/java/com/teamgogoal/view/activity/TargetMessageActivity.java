package com.teamgogoal.view.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.teamgogoal.presenter.TargetMessagePresenter;
import com.teamgogoal.utils.ProgressDialogUtils;
import com.teamgogoal.utils.ToastUtils;
import com.teamgogoal.view.interfaces.BaseView;
import com.teamgogoal.view.interfaces.TargetMessageView;

public class TargetMessageActivity extends AppCompatActivity implements TargetMessageView {

    private TargetMessagePresenter targetMessagePresenter;

    private ProgressDialog progressDialog;

    private int targetId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        progressDialog = ProgressDialogUtils.create(this);

        targetMessagePresenter = new TargetMessagePresenter(this);

        initTargetId();

        loadingMessage();
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
        targetId = bundle.getInt("TargetId");
    }

    private void loadingMessage() {

    }
}
