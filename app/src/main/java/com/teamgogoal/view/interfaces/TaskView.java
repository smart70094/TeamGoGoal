package com.teamgogoal.view.interfaces;

import com.teamgogoal.dto.TaskListDto;

import java.util.Map;

public interface TaskView extends BaseView {

    void readOneTaskComplete(Map<String, Object> data);

    void getTaskWithTargetIdComplete(TaskListDto taskListDto);

}
