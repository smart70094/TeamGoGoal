package com.teamgogoal.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ForgetPasswordDto {

    private String account;
}
