package com.teamgogoal.service;

import com.teamgogoal.dto.ParticipantDto;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

public interface ParticipantApiService {

    @GET("app/auth/participant/{targetId}")
    Observable<ParticipantDto> getParticipant(@Path("targetId") int targetId);

    @POST("app/auth/participant")
    Observable<Void> inviteParticipant(@Body ParticipantDto.Participant participant);

    @HTTP(method = "DELETE", path = "app/auth/participant", hasBody = true)
    Observable<Void> deleteParticipant(@Body ParticipantDto.Participant participant);
}
