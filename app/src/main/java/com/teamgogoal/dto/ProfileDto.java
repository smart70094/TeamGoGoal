package com.teamgogoal.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProfileDto {
    private String id;
    private String name;
    private String account;
    private String password;
    private String email;
    private Integer headImageId;
    private String headImage;
}
