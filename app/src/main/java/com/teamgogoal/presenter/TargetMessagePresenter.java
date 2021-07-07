package com.teamgogoal.presenter;

import com.teamgogoal.model.MessageModel;
import com.teamgogoal.view.interfaces.TargetMessageView;

public class TargetMessagePresenter extends BasePresenter {

    private TargetMessageView targetMessageView;

    private MessageModel messageModel;

    public TargetMessagePresenter(TargetMessageView targetMessageView) {
        this.targetMessageView = targetMessageView;
    }

    @Override
    public void showMessage(String message) {
        targetMessageView.showMessage(message);
    }

    @Override
    public void dismissProgressDialog() {
        targetMessageView.dismissProgressDialog();
    }
}
