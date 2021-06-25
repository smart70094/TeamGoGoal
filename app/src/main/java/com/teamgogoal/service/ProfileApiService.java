package com.teamgogoal.service;

import com.google.gson.JsonObject;
import com.teamgogoal.dto.ProfileDto;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import rx.Observable;

public interface ProfileApiService {

    @GET("app/auth/member")
    Observable<ProfileDto> get();

    @GET("app/auth/validate/password/{account}/{password}")
    Observable<JsonObject> getValidatePassword(@Path("account") String account, @Path("password") String password);

    @PUT("app/auth/member")
    Observable<Void> update(@Body ProfileDto profileDto);

}
