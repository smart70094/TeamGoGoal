package com.teamgogoal.presenter;

import com.teamgogoal.dto.MessageListDto;
import com.teamgogoal.model.MessageModel;
import com.teamgogoal.view.interfaces.MessageView;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MessagePresenter extends BasePresenter {

    private MessageView messageView;

    private MessageModel messageModel;

    public MessagePresenter(MessageView messageView) {
        this.messageView = messageView;

        this.messageModel = new MessageModel();
    }

    @Override
    public void showMessage(String message) {
        messageView.showMessage(message);
    }

    @Override
    public void dismissProgressDialog() {
        messageView.dismissProgressDialog();
    }

    public void loadingMessage() {
        Observable<MessageListDto> apiObservable = messageModel.getMessage();

        apiObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(messageListDto -> messageView.loadingMessageSuccess(messageListDto));
    }
}
