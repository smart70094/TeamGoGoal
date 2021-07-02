package com.teamgogoal.view.interfaces;

import com.teamgogoal.dto.ParticipantDto;

import java.util.List;
import java.util.Map;

public interface ParticipantView extends BaseView {

    void initialParticipantAdapter(List<ParticipantDto> datas);

}
