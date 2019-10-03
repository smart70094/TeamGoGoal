package com.teamgogoal.service;

import com.google.gson.JsonObject;

import java.util.Map;

import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import rx.Observable;

public interface TargetApiService {

    @POST("app/auth/target")
    Observable<JsonObject> addTarget(@Body Map<String, Object> body);
}
