package com.teamgogoal.model;

import com.teamgogoal.dto.ParticipantDto;
import com.teamgogoal.service.ParticipantApiService;
import com.teamgogoal.utils.TggRetrofitUtils;

import rx.Observable;

public class ParticipantModel {

    private ParticipantApiService participantApiService;

    public ParticipantModel() {
        participantApiService = TggRetrofitUtils.getTggService(ParticipantApiService.class);
    }

    public Observable<ParticipantDto> getParticipant(int targetId) {
        return participantApiService.getParticipant(targetId);
    }

    public Observable<Void> inviteParticipant(ParticipantDto.Participant participant) {
        return participantApiService.inviteParticipant(participant);
    }

    public Observable<Void> deleteParticipant(ParticipantDto.Participant participant) {
        return participantApiService.deleteParticipant(participant);
    }

    public Observable<Void> acceptInvite() {
        return null;
    }
}
