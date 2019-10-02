package com.teamgogoal.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.teamgogoal.model.TargetModel;
import com.teamgogoal.presenter.TargetPresenter;
import com.teamgogoal.utils.ToastUtils;
import com.teamgogoal.view.interfaces.TargetView;

public class TargetActivity extends AppCompatActivity implements TargetView {

    private TargetPresenter targetPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        targetPresenter = new TargetPresenter(this , new TargetModel());
        targetPresenter.onCreate();
    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_target);
    }

    @Override
    public void showTargetEdit(View view) {
        Intent intent = new Intent(this, TargetEditActivity.class);
        startActivity(intent);
    }

}
