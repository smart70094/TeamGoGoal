package com.teamgogoal.presenter;

import com.google.gson.JsonObject;
import com.teamgogoal.dto.TaskListDto;
import com.teamgogoal.model.TargetModel;
import com.teamgogoal.model.TaskModel;
import com.teamgogoal.service.action.HttpExceptionAction;
import com.teamgogoal.view.interfaces.TaskView;

import java.util.Map;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class TaskPresenter extends BasePresenter {

    private TaskView taskView;
    private TaskModel taskModel;

    public TaskPresenter(TaskView taskView, TaskModel taskModel) {
        this.taskView = taskView;
        this.taskModel = taskModel;
    }

    public void onCreate() {
        taskView.setContentView();
    }


    public void getTaskWithTargetId(int targetid) {
        taskModel.getTaskWithTargetId(this, targetid);
    }

    public void getTaskWithTargetIdComplete(TaskListDto taskListDto) {
        taskView.getTaskWithTargetIdComplete(taskListDto);
    }

    public void updateIsComplete(int id, String isComplete) {
        Observable<JsonObject> observable = taskModel.updateIsComplete(id, isComplete);

        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resposne->{

                }, new HttpExceptionAction(this));
    }

    public void showMessage(String message) {
        taskView.showMessage(message);
    }
}
