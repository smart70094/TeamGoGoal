package com.teamgogoal.view.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.teamgogoal.model.TargetModel;
import com.teamgogoal.presenter.TargetEditPresenter;
import com.teamgogoal.utils.DateUtils;
import com.teamgogoal.utils.EditTextUtils;
import com.teamgogoal.utils.StringUtils;
import com.teamgogoal.utils.ToastUtils;
import com.teamgogoal.view.interfaces.TargetEditView;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TargetEditActivity extends AppCompatActivity implements TargetEditView {
    @BindView(R.id.targetNameEt)
    EditText targetNameEditText;
    @BindView(R.id.targeContentEt)
    EditText targetContentEditText;
    @BindView(R.id.startTimeEt)
    EditText startTimeEditText;
    @BindView(R.id.selectStartTimeBtn)
    ImageButton selectStartTimeButton;
    @BindView(R.id.endTimeEt)
    EditText endTimeEditText;
    @BindView(R.id.selectEndTimeBtn)
    ImageButton selectEndTimeButton;
    @BindView(R.id.TargetEventBtn)
    Button submitTargetButton;


    private TargetEditPresenter targetEditPresenter;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        targetEditPresenter = new TargetEditPresenter(this, new TargetModel());
        targetEditPresenter.onCreate();
        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            int id = bundle.getInt("id");
            targetEditPresenter.readOneTarget(id);

            String model = bundle.getString("model");
            if(StringUtils.hasAssignment(model) && model.equalsIgnoreCase("read")) {
                enableUpdateComponent(false);
            } else {
                enableUpdateComponent(true);
            }
        }
    }

    @Override
    public void showShortMessage(String message) {
        ToastUtils.showShortMessage(this, message);
    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_target_event_acivity);
    }

    private Map<String, Object> getTargetDataMap() {
        Map<String, Object> params = new HashMap<>();
        params.put("title", EditTextUtils.getText(targetNameEditText));
        params.put("content", EditTextUtils.getText(targetContentEditText));
        params.put("startdate", EditTextUtils.getText(startTimeEditText));
        params.put("enddate", EditTextUtils.getText(endTimeEditText));
        return params;
    }

    private void createAndUpdateTarget() {
        submitTargetButton.setEnabled(false);
        Bundle bundle = getIntent().getExtras();

        Map<String, Object> params = getTargetDataMap();
        if(bundle != null) {
            int id = bundle.getInt("id");
            params.put("id", id);
            targetEditPresenter.updateTarget(params);
        } else {
            targetEditPresenter.addTarget(params);
        }
    }

    @Override
    public void readOneTargetComplete(Map<String, Object> data) {
        targetNameEditText.setText((String) data.get("title"));
        targetContentEditText.setText((String) data.get("content"));
        startTimeEditText.setText((String) data.get("startdate"));
        endTimeEditText.setText((String) data.get("enddate"));
    }

    @Override
    public void updateTargetComplete() {
        showShortMessage("目標儲存成功!");
        submitTargetButton.setEnabled(true);
        moveToTarget();
    }

    private void clearTarget() {
        targetNameEditText.setText("");
        targetContentEditText.setText("");
        startTimeEditText.setText("");
        endTimeEditText.setText("");
    }

    private void enableUpdateComponent(boolean flag) {
        targetNameEditText.setEnabled(flag);
        targetContentEditText.setEnabled(flag);
        startTimeEditText.setEnabled(flag);
        endTimeEditText.setEnabled(flag);
        submitTargetButton.setEnabled(flag);
//        clearMessageButton.setEnabled(flag);
        selectStartTimeButton.setEnabled(flag);
        selectEndTimeButton.setEnabled(flag);
    }

    private void moveToTarget() {
        Intent intent = new Intent();
        intent.setClass(this, TargetActivity.class);
        startActivity(intent);
    }

    @OnClick({R.id.selectStartTimeBtn, R.id.selectEndTimeBtn, R.id.TargetEventBtn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.selectStartTimeBtn:
                DateUtils.showDatePickerDialog(this, startTimeEditText);
                break;
            case R.id.selectEndTimeBtn:
                DateUtils.showDatePickerDialog(this, endTimeEditText);
                break;
            case R.id.TargetEventBtn:
                createAndUpdateTarget();
                break;
            default:
                break;
        }
    }

    @Override
    public void dismissProgressDialog() {
        progressDialog.dismiss();
    }

    @Override
    public void showMessage(String message) {
        ToastUtils.showShortMessage(this, message);
    }
}
