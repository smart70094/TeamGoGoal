package com.teamgogoal.service;

import com.teamgogoal.dto.ParticipantDto;

import java.util.List;

import rx.Observable;

public interface ParticipantApiService {

    Observable<List<ParticipantDto>> getParticipant();

}
