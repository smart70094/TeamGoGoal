package com.teamgogoal.view.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.Tasks;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.teamgogoal.dto.TaskDto;
import com.teamgogoal.dto.TaskListDto;
import com.teamgogoal.model.TaskModel;
import com.teamgogoal.presenter.TaskPresenter;
import com.teamgogoal.utils.AlertDialogUtils;
import com.teamgogoal.utils.ToastUtils;
import com.teamgogoal.view.adapter.TargetAdapter;
import com.teamgogoal.view.adapter.TaskAdapter;
import com.teamgogoal.view.interfaces.TaskView;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TaskActivity extends AppCompatActivity implements TaskView {

    @BindView(R.id.listview_task)
    ListView taskListView;

    @BindView(R.id.dreamContext)
    TextView dreamContextTextView;

    @BindView(R.id.yourCircularProgressbar)
    CircularProgressBar circularProgressBar;

    @BindView(R.id.TargetTitle)
    TextView targetTitleTextView;

    @BindView(R.id.showAddTaskBtn)
    Button addTaskButton;

    private TaskPresenter taskPresenter;

    private TaskAdapter taskAdapter;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.taskPresenter = new TaskPresenter(this, new TaskModel());
        taskPresenter.onCreate();
        ButterKnife.bind(this);

        initialBundleProcessing();
        onClickTaskList();
        onLongClickTaskList();
    }

    private void initialBundleProcessing() {
        initialTaskList();
        initialBlueprint();
        initialTitle();
    }

    private void initialTaskList() {
        int id = getIntent().getExtras().getInt("id");
        taskPresenter.getTaskWithTargetId(id);
    }

    private void initialBlueprint() {
        String blueprint = getIntent().getExtras().getString("blueprint");
        dreamContextTextView.setText(blueprint);
    }

    private void initialTitle() {
        String title = getIntent().getExtras().getString("title");
        targetTitleTextView.setText(title);
    }

    private void onLongClickTaskList() {
        taskListView.setOnItemLongClickListener((adapterView, view, i, l) -> {
            TaskDto taskDto = (TaskDto) adapterView.getItemAtPosition(i);
            moveToTaskEditActivity(taskDto.getId());
            return true;
        });
    }


    private void onClickTaskList() {
        taskListView.setOnItemClickListener((adapterView, view, i, l) -> {
            TaskDto taskDto = (TaskDto) adapterView.getItemAtPosition(i);

             AlertDialogUtils.buildComfirmAlertDialog(
                    this, "TeamGoGoal", "是否完成任務？",
            (dialogInterface, j)->{
                taskPresenter.updateIsComplete(taskDto.getId(), "Y");
                taskDto.setIscomplete("Y");
                taskAdapter.notifyDataSetChanged();
            }, (dialogInterface, j)->{
                 taskPresenter.updateIsComplete(taskDto.getId(), "N");
             });
        });
    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_task);
    }

    @Override
    public void readOneTaskComplete(Map<String, Object> data) {
        System.out.println(data);
    }

    @Override
    public void getTaskWithTargetIdComplete(TaskListDto taskListDto) {
        circularProgressBar.setColor(ContextCompat.getColor(TaskActivity.this, R.color.processbar));
        circularProgressBar.setBackgroundColor(ContextCompat.getColor(TaskActivity.this, R.color.processbar_bg));
        circularProgressBar.setProgressBarWidth(18);
        circularProgressBar.setBackgroundProgressBarWidth(circularProgressBar.getProgressBarWidth());
        int animationDuration = 2500;
        circularProgressBar.setProgressWithAnimation(taskListDto.getCompleteProgressRate(), animationDuration); // Default duration = 1500ms

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        taskAdapter = new TaskAdapter(layoutInflater, taskListDto);
        taskListView.setAdapter(taskAdapter);
    }

    private void moveToTaskEditActivity() {
        Intent intent = new Intent(this, TaskEditActivity.class);
        intent.putExtra("targetid", getIntent().getExtras().getInt("id"));
        intent.putExtra("title", getIntent().getExtras().getString("title"));
        startActivity(intent);
    }

    private void moveToTaskEditActivity(int id) {
        Intent intent = new Intent(this, TaskEditActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("targetid", getIntent().getExtras().getInt("id"));
        startActivity(intent);
    }

    @OnClick(R.id.showAddTaskBtn)
    public void onViewClicked(View view) {
        switch(view.getId()) {
            case R.id.showAddTaskBtn:
                moveToTaskEditActivity();
                break;
            default:
                break;
        }
    }

    @Override
    public void showMessage(String message) {
        ToastUtils.showShortMessage(this, message);
    }

    @Override
    public void dismissProgressDialog() {
        progressDialog.dismiss();
    }
}
