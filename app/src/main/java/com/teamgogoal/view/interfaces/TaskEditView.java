package com.teamgogoal.view.interfaces;

import com.teamgogoal.dto.TaskDto;

public interface TaskEditView {

    void showMessage(String message);

    void getOneTaskComplete(TaskDto taskDto);
}
