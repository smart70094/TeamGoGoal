package com.teamgogoal.service;

import com.teamgogoal.dto.MessageListDto;

import retrofit2.http.GET;
import rx.Observable;

public interface MessageApiService {

    @GET("app/auth/message")
    Observable<MessageListDto> getMessage();

}
