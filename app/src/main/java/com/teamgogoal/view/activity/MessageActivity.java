package com.teamgogoal.view.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.GridView;
import android.widget.ListView;

import com.teamgogoal.dto.MessageListDto;
import com.teamgogoal.presenter.MessagePresenter;
import com.teamgogoal.utils.ProgressDialogUtils;
import com.teamgogoal.utils.ToastUtils;
import com.teamgogoal.view.adapter.MessageAdapter;
import com.teamgogoal.view.interfaces.MessageView;

public class MessageActivity extends AppCompatActivity implements MessageView {

    private MessagePresenter messagePresenter;

    private ProgressDialog progressDialog;

    private ListView messageListView;
    private MessageAdapter messageAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        progressDialog = ProgressDialogUtils.create(this);
        messageListView = findViewById(R.id.request_list);

        messagePresenter = new MessagePresenter(this);
        messagePresenter.loadingMessage();
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

    @Override
    public void loadingMessageSuccess(MessageListDto messageListDto) {
        messageAdapter = new MessageAdapter(this, messageListDto.getMessages());
        messageListView.setAdapter(messageAdapter);
        messageAdapter.notifyDataSetChanged();
    }
}
