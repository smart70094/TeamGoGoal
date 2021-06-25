package com.teamgogoal.presenter;

import com.google.gson.JsonObject;
import com.teamgogoal.dto.TaskDto;
import com.teamgogoal.model.TaskModel;
import com.teamgogoal.service.action.HttpExceptionAction;
import com.teamgogoal.view.interfaces.TaskEditView;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class TaskEditPresenter extends BasePresenter{

    private TaskEditView taskEditView;
    private TaskModel taskModel;

    public TaskEditPresenter(TaskEditView taskEditView) {
        this.taskEditView = taskEditView;
        this.taskModel = new TaskModel();
    }

    public void getOneTask(int id) {
        Observable<TaskDto> getOneTaskObservable = taskModel.getOneTask(id);

        getOneTaskObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resposne->{
                    taskEditView.getOneTaskComplete(resposne);
                }, new HttpExceptionAction(this));
    }

    public void createTask(TaskDto taskDto) {
        Observable<JsonObject> createTaskObservable = taskModel.createTask(taskDto);
        createTaskObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resposne->{
                    showMessage("新增任務成功！");
                }, new HttpExceptionAction(this));
    }

    public void updateTask(int id, TaskDto taskDto) {
        Observable<JsonObject> updateTaskObservable = taskModel.updateTask(id, taskDto);
        updateTaskObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resposne->{
                    showMessage("更新任務成功！！");
                }, new HttpExceptionAction(this));
    }

    public void showMessage(String message) {
        this.taskEditView.showMessage(message);
    }

    @Override
    public void dismissProgressDialog() {
        taskEditView.dismissProgressDialog();
    }
}
