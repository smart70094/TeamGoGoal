package com.teamgogoal.service;

import com.teamgogoal.dto.ProfileDto;

import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

public interface RegisterAccountApiService {

    @POST("app/member")
    public Observable<Void> registterAccount(@Body ProfileDto profileDto);

}
