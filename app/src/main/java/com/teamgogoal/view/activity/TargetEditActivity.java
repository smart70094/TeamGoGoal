package com.teamgogoal.view.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.teamgogoal.model.TargetModel;
import com.teamgogoal.presenter.TargetEditPresenter;
import com.teamgogoal.utils.EditTextUtls;
import com.teamgogoal.utils.ToastUtils;
import com.teamgogoal.view.interfaces.TargetEditView;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TargetEditActivity extends AppCompatActivity implements TargetEditView {
    @BindView(R.id.targetNameTxt)
    EditText targetNameEditText;
    @BindView(R.id.targetContent)
    EditText targetContentEditText;
    @BindView(R.id.startTimeTxt)
    EditText startTimeEditText;
    @BindView(R.id.selectStartTimeBtn)
    ImageButton selectStartTimeButton;
    @BindView(R.id.selectEndTimeBtn)
    ImageButton selectEndTimeButton;
    @BindView(R.id.submitTargetBtn)
    Button submitTargetButton;
    @BindView(R.id.clearMessageBtn)
    Button clearMessageButton;
    @BindView(R.id.cannelBtn)
    Button cannelButton;
    @BindView(R.id.EndTimeTxt)
    EditText endTimeTxt;

    private TargetEditPresenter targetEditPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        targetEditPresenter = new TargetEditPresenter(this, new TargetModel());
        targetEditPresenter.onCreate();
        ButterKnife.bind(this);
    }

    @Override
    public void showShortMessage(String message) {
        ToastUtils.showShortMessage(this, message);
    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_target_edit);
    }

    private void addTarget() {
        submitTargetButton.setEnabled(false);
        Map<String, Object> params = new HashMap<>();
        params.put("title", EditTextUtls.getText(targetNameEditText));
        params.put("content", EditTextUtls.getText(targetContentEditText));
        params.put("startdate", EditTextUtls.getText(startTimeEditText));
        params.put("enddate", EditTextUtls.getText(endTimeTxt));

        targetEditPresenter.addTarget(params);
    }

    @Override
    public void addTargetComplete() {
        showShortMessage("新增目標成功!");
        submitTargetButton.setEnabled(true);
    }

    @OnClick({R.id.selectStartTimeBtn, R.id.selectEndTimeBtn, R.id.submitTargetBtn, R.id.clearMessageBtn, R.id.cannelBtn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.selectStartTimeBtn:
                ToastUtils.showShortMessage(this, "2");
                break;
            case R.id.selectEndTimeBtn:
                ToastUtils.showShortMessage(this, "3");
                break;
            case R.id.submitTargetBtn:
                addTarget();
                break;
            case R.id.clearMessageBtn:
                ToastUtils.showShortMessage(this, "5");
                break;
            case R.id.cannelBtn:
                ToastUtils.showShortMessage(this, "6");
                break;
        }
    }
}
