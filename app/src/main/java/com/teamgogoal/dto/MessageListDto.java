package com.teamgogoal.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageListDto {

    private List<MessageDto> messages;

}
