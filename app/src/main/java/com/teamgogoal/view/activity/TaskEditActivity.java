package com.teamgogoal.view.activity;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.teamgogoal.dto.TaskDto;
import com.teamgogoal.presenter.TaskEditPresenter;
import com.teamgogoal.utils.EditTextUtils;
import com.teamgogoal.utils.StringUtils;
import com.teamgogoal.utils.TimeUtils;
import com.teamgogoal.utils.ToastUtils;
import com.teamgogoal.validate.TggValidator;
import com.teamgogoal.view.interfaces.TaskEditView;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TaskEditActivity extends AppCompatActivity implements TaskEditView {

    @BindView(R.id.TargetTitle)
    TextView TargetTitleTextView;
    @BindView(R.id.taskNameEt)
    EditText taskNameEditText;
    @BindView(R.id.taskContentEt)
    EditText taskContentEditText;
    @BindView(R.id.remindTimeEt)
    EditText remindTimeEditText;
    @BindView(R.id.imageButton8)
    ImageButton selectTimeImageButton;

    private TaskEditPresenter taskEditPresenter;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_event);
        ButterKnife.bind(this);

        taskEditPresenter = new TaskEditPresenter(this);

        initialBundleProcessing();
    }

    private void initialBundleProcessing() {
        initialTargetTitle();
        initialEditTaskData();
    }

    private void initialEditTaskData() {
        int id = getIntent().getExtras().getInt("id");
        if(id != 0) {
            taskEditPresenter.getOneTask(id);
        }
    }

    private void initialTargetTitle() {
        Bundle bundle = getIntent().getExtras();
        TargetTitleTextView.setText(bundle.getString("title"));
    }


    @Override
    public void showMessage(String message) {
        ToastUtils.showShortMessage(this, message);
    }

    @OnClick({R.id.TaskEventBtn, R.id.imageButton8})
    public void onViewClicked(View view) {
        switch(view.getId()) {
            case R.id.TaskEventBtn:
                upsertTask();
                break;
            case R.id.imageButton8:
                selectTime();
                break;
            default:
                break;
        }
    }

    @Override
    public void getOneTaskComplete(TaskDto taskDto) {
        taskNameEditText.setText(taskDto.getTitle());
        taskContentEditText.setText(taskDto.getContent());
        remindTimeEditText.setText(TimeUtils.buildTimeFormat(taskDto.getTime()));
    }

    private void upsertTask() {
        Bundle bundle = getIntent().getExtras();

        int targetid = bundle.getInt("targetid");
        int id = bundle.getInt("id");
        String time = EditTextUtils.getText(remindTimeEditText);
        time = time.replace(":", "");

        TaskDto taskDto = TaskDto.builder()
                                    .title(EditTextUtils.getText(taskNameEditText))
                                    .content(EditTextUtils.getText(taskContentEditText))
                                    .time(time)
                                    .iscomplete("N")
                                    .targetid(targetid)
                                    .build();

        TggValidator.execute(this, taskDto);

        if(id == 0)
            taskEditPresenter.createTask(taskDto);
        else
            taskEditPresenter.updateTask(id, taskDto);

        moveToTargetActivity();
    }

    private void moveToTargetActivity() {
        Intent intent = new Intent(this, TargetActivity.class);
        startActivity(intent);
    }

    private void selectTime() {
        TimeUtils.showTimePickerDialog(this, remindTimeEditText);
    }

    @Override
    public void dismissProgressDialog() {
        progressDialog.dismiss();
    }
}
