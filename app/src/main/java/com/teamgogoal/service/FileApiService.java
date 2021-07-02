package com.teamgogoal.service;

import com.teamgogoal.jpa.entity.File;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import rx.Observable;

public interface FileApiService {

    @GET("app/file/{id}")
    Observable<ResponseBody> get(@Path("id") int id);

    @Multipart
    @POST("app/file")
    Observable<File> create(@Part MultipartBody.Part file);

}
