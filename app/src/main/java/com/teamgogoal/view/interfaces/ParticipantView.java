package com.teamgogoal.view.interfaces;

import com.teamgogoal.dto.ParticipantDto;

public interface ParticipantView extends BaseView {

    void initialParticipantAdapter(ParticipantDto datas);

    void inviteParticipantSuccess();
}
