package com.teamgogoal.service;

import com.google.gson.JsonObject;
import com.teamgogoal.dto.ForgetPasswordDto;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import rx.Observable;

public interface ForgetPasswordApiService {

    @PUT("app/forget/password")
    Observable<JsonObject> forgetPassword(@Body ForgetPasswordDto forgetPasswordDto);
}
