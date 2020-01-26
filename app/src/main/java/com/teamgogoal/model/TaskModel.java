package com.teamgogoal.model;

import com.google.android.gms.tasks.Tasks;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.teamgogoal.dto.TaskDto;
import com.teamgogoal.dto.TaskListDto;
import com.teamgogoal.presenter.TaskPresenter;
import com.teamgogoal.service.TaskApiService;
import com.teamgogoal.service.action.HttpExceptionAction;
import com.teamgogoal.utils.TggRetrofitUtils;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class TaskModel {

    private TaskApiService taskApiService;

    public TaskModel() {
        this.taskApiService = TggRetrofitUtils.getTggService(TaskApiService.class);
    }

    public void getTaskWithTargetId(TaskPresenter taskPresenter, int targetid) {
        Observable<JsonObject> observable = taskApiService.getTaskWithTargetId(targetid);

        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response->{
                    JsonElement tasksJsonArray = response.get("tasks");
                    Type listType = new TypeToken<List<TaskDto>>() {}.getType();
                    List<TaskDto> tasks = new Gson().fromJson(tasksJsonArray.toString(), listType);

                    float completeProgressRate = response.get("completeprogressrate").getAsFloat();

                    TaskListDto taskListDto =
                            TaskListDto.builder()
                                    .completeProgressRate(completeProgressRate)
                                    .tasks(tasks)
                                    .build();
                    taskPresenter.getTaskWithTargetIdComplete(taskListDto);
                }, new HttpExceptionAction(taskPresenter));
    }

    public Observable<TaskDto> getOneTask(int id) {
        return taskApiService.getOneTask(id);
    }

    public Observable<JsonObject> createTask(TaskDto taskDto) {
        return taskApiService.createTask(taskDto);
    }

    public Observable<JsonObject> updateTask(int id, TaskDto taskDto) {
        return taskApiService.updateTask(id, taskDto);
    }

    public Observable<JsonObject> updateIsComplete(int id, String isComplete) {
        Map<String, Object> params = new HashMap<>();
        params.put("iscomplete", isComplete);

        return taskApiService.updateComplete(id, params);
    }
}
