package com.teamgogoal.service;

import com.google.gson.JsonObject;
import com.teamgogoal.dto.TaskDto;

import java.util.Map;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import rx.Observable;

public interface TaskApiService {

    @GET("app/auth/task/all/{targetid}")
    Observable<JsonObject> getTaskWithTargetId(@Path("targetid") int targetid);

    @GET("app/auth/task/{id}")
    Observable<TaskDto> getOneTask(@Path("id") int id);

    @POST("app/auth/task")
    Observable<JsonObject>  createTask(@Body TaskDto taskDto);

    @PUT("app/auth/task/{id}")
    Observable<JsonObject> updateTask(@Path("id") int id, @Body TaskDto taskDto);

    @PUT("app/auth/task/complete/{id}")
    Observable<JsonObject> updateComplete(@Path("id") int id, @Body Map<String, Object> params);

}
