package com.teamgogoal.model;

import com.teamgogoal.dto.MessageListDto;
import com.teamgogoal.service.MessageApiService;
import com.teamgogoal.utils.TggRetrofitUtils;

import rx.Observable;

public class MessageModel {

    private MessageApiService messageApiService;

    public MessageModel() {
        this.messageApiService = TggRetrofitUtils.getTggService(MessageApiService.class);
    }

    public Observable<MessageListDto> getMessage() {
        return messageApiService.getMessage();
    }

    public Observable<Void> updateReadedMessage() {
        return null;
    }

    public Observable<Void> sendCheerMessage() {
        return null;
    }

}
