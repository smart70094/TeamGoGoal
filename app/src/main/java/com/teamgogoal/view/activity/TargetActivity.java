package com.teamgogoal.view.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;

import android.widget.ListAdapter;
import android.widget.ListView;

import com.example.teamgogoal.teamgogoal.Message;
import com.teamgogoal.model.TargetModel;
import com.teamgogoal.presenter.TargetPresenter;
import com.teamgogoal.utils.ToastUtils;
import com.teamgogoal.view.adapter.TargetAdapter;
import com.teamgogoal.view.interfaces.TargetView;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TargetActivity extends AppCompatActivity implements TargetView {

    @BindView(R.id.Profile)
    ImageButton profileButton;
    @BindView(R.id.Review)
    ImageButton reviewButton;
    @BindView(R.id.Notice)
    ImageButton noticeButton;
    @BindView(R.id.Target)
    ImageButton targetButton;
    @BindView(R.id.Quest)
    ImageButton questButton;
    @BindView(R.id.listview_target)
    ListView targetListView;
    @BindView(R.id.showAddTargetBtn)
    Button addTargetButton;
    private TargetPresenter targetPresenter;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_target);

        targetPresenter = new TargetPresenter(this, new TargetModel());
        targetPresenter.onCreate();
        ButterKnife.bind(this);

        registerTargetClickEvent();
        registerTargetLongClickEvent();

    }

    @Override
    public void showMessage(String message) {
        ToastUtils.showShortMessage(this, message);
    }

    @Override
    public <T> void switchView(Class<T> activityClass) {
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
    }

    @Override
    public void dismissProgressDialog() {
        progressDialog.dismiss();
    }

    // 跳到會員編輯頁
    public void moveProfilleActivity(View view) {
        startActivity(new Intent(this, ProfileActivity.class));
    }

    // 註冊目標點擊事件
    private void registerTargetClickEvent() {
        targetListView.setOnItemClickListener((adapterView, view, i, l) -> {
            Map<String, Object> data = (Map<String, Object>) adapterView.getItemAtPosition(i);
            int id = (int) data.get("id");
            String blueprint = (String) data.get("blueprint");
            String title = (String) data.get("title");

            Intent intent = new Intent();
            intent.putExtra("id", id);
            intent.putExtra("blueprint", blueprint);
            intent.putExtra("title", title);
            intent.setClass(TargetActivity.this, TaskActivity.class);
            startActivity(intent);
        });
    }

    // 註冊任務點擊事件
    private void registerTargetLongClickEvent() {
        targetListView.setOnItemLongClickListener((adapterView, view, i, l) -> {

            View targetOperationDialogView = LayoutInflater.from(this).inflate(R.layout.target_selector, null);
            Button readTarget = (Button) targetOperationDialogView.findViewById(R.id.readTarget);
            Button modifyTarget = (Button) targetOperationDialogView.findViewById(R.id.modifyTarget);
            Button deleteTarget = (Button) targetOperationDialogView.findViewById(R.id.deleteTarget);
            AlertDialog targetOperationDialog = new AlertDialog.Builder(this, R.style.hitStyle).setView(targetOperationDialogView).create();

            Map<String, Object> data = (Map<String, Object>) adapterView.getItemAtPosition(i);
            int id = (int) data.get("id");
            Intent intent = new Intent();
            intent.putExtra("id", id);
            intent.setClass(TargetActivity.this, TargetEditActivity.class);

            readTarget.setOnClickListener((readTargetView)->{
                intent.putExtra("model", "read");
                startActivity(intent);
                targetOperationDialog.dismiss();
            });

            modifyTarget.setOnClickListener((modifyTargetView)-> {
                intent.putExtra("model", "update");
                startActivity(intent);
                targetOperationDialog.dismiss();
            });

            deleteTarget.setOnClickListener((deleteTargetView)-> {
                targetPresenter.deleteData(id);
                targetOperationDialog.dismiss();
            });

            targetOperationDialog.show();
            return true;
        });
    }

    @Override
    public void showTargetEdit(View view) {
        Intent intent = new Intent(this, TargetEditActivity.class);
        startActivity(intent);
    }

    @Override
    public void readData(List<Map<String, Object>> datas) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        ListAdapter targetAdapter = new TargetAdapter(layoutInflater, datas);
        targetListView.setAdapter(targetAdapter);
    }

    @Override
    public void deleteTargetComplete() {
        ToastUtils.showShortMessage(this, "刪除目標成功!");
    }

    @OnClick(R.id.Target)
    public void moveMessageActivity(View view) {
        switchView(MessageActivity.class);
    }

}
