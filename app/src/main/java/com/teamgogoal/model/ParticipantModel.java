package com.teamgogoal.model;

import com.teamgogoal.dto.ParticipantDto;
import com.teamgogoal.service.ParticipantApiService;
import com.teamgogoal.utils.TggRetrofitUtils;

import java.util.List;

import rx.Observable;

public class ParticipantModel {

    private ParticipantApiService participantApiService;

    public ParticipantModel() {
        participantApiService = TggRetrofitUtils.getTggService(ParticipantApiService.class);
    }

    public Observable<List<ParticipantDto>> getParticipant() {
        return participantApiService.getParticipant();
    }
}
