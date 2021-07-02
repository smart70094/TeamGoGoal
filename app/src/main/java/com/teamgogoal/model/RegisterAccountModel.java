package com.teamgogoal.model;

import com.teamgogoal.dto.ProfileDto;
import com.teamgogoal.service.FileApiService;
import com.teamgogoal.service.RegisterAccountApiService;
import com.teamgogoal.utils.TggRetrofitUtils;

import rx.Observable;

public class RegisterAccountModel {

    private RegisterAccountApiService registerAccountApiService;

    public RegisterAccountModel() {
        this.registerAccountApiService = TggRetrofitUtils.getTggService(RegisterAccountApiService.class);
    }

    public Observable<Void> registerAccount(ProfileDto profileDto) {
        return registerAccountApiService.registterAccount(profileDto);
    }
}
