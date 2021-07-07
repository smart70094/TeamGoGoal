package com.teamgogoal.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageDto {

    private int messageId;

    private String fromId;

    private String toId;

    private int targetId;

    private String type;

    private String content;

    private String isReaded;

    private int fromHeadImageId;

    private int fromName;
}
