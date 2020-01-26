package com.teamgogoal.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaskListDto {
    private float completeProgressRate;
    private List<TaskDto> tasks;
}
