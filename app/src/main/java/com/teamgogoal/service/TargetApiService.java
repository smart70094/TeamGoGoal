package com.teamgogoal.service;

import com.google.gson.JsonObject;

import java.util.Map;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import rx.Observable;

public interface TargetApiService {

    @POST("app/auth/target")
    Observable<JsonObject> addTarget(@Body Map<String, Object> body);

    @PUT("app/auth/target")
    Observable<JsonObject> updateTarget(@Body Map<String, Object> body);

    @GET("app/auth/target")
    Observable<JsonObject> readTarget();

    @GET("app/auth/target/{id}")
    Observable<JsonObject> readOneTarget(@Path("id") int id);

    @DELETE("app/auth/target/{id}")
    Observable<JsonObject> deleteTarget(@Path("id") int id);
}
