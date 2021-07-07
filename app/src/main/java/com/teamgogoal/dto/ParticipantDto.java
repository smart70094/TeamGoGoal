package com.teamgogoal.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ParticipantDto {

    @Data
    @Builder
    public static class Participant {
        private String account;
        private int targetId;
        private String name;
        private int headImageId;
        private String isJoin;
        private byte[] image;
    }

    private List<Participant> participants;
}
