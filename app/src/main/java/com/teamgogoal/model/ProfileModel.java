package com.teamgogoal.model;

import com.google.gson.JsonObject;
import com.teamgogoal.dto.ProfileDto;
import com.teamgogoal.service.ProfileApiService;
import com.teamgogoal.utils.TggRetrofitUtils;

import org.json.JSONObject;

import rx.Observable;

public class ProfileModel {

    private ProfileApiService profileApiService;

    public ProfileModel() {
        this.profileApiService = TggRetrofitUtils.getTggService(ProfileApiService.class);
    }

    public Observable<ProfileDto> get() {
        return profileApiService.get();
    }

    public Observable<JsonObject> getValidatePassword(String account, String password) {
        return profileApiService.getValidatePassword(account, password);
    }

    public Observable<Void> update(ProfileDto profileDto) {
        return profileApiService.update(profileDto);
    }
}
