package com.teamgogoal.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ParticipantDto {
    private String name;
    private int headImageId;
    private byte[] image;
}
