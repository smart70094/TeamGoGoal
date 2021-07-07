package com.teamgogoal.view.interfaces;

import com.teamgogoal.dto.MessageListDto;

public interface MessageView extends BaseView {

    void loadingMessageSuccess(MessageListDto messageListDto);
}
