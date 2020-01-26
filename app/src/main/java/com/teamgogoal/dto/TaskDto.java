package com.teamgogoal.dto;

import com.teamgogoal.validate.annotation.Length;
import com.teamgogoal.validate.annotation.NotBlank;
import com.teamgogoal.validate.annotation.NotNull;
import com.teamgogoal.validate.annotation.Number;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaskDto {

    private int id;

    @NotBlank(message = "任務名稱不可為空！")
    @Length(max = 100, message = "任務名稱長度不可超過100字！")
    private String title;

    @NotBlank(message = "任務內容不可為空！")
    @Length(max = 100, message = "任務內容長度不可超過100字！")
    private String content;

    @NotBlank(message = "提醒時間不可為空！")
    @Length(min = 4, max = 4, message = "提醒時間長度需為4")
    private String time;

    private int targetid;
    private String iscomplete;
}
