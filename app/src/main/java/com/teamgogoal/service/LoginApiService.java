package com.teamgogoal.service;

import com.google.gson.JsonObject;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface LoginApiService {

    @GET("app/login")
    Observable<JsonObject> login(@Query("account") String account, @Query("password") String password);
}
