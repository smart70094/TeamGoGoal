package com.teamgogoal.presenter;

import com.teamgogoal.model.TargetModel;
import com.teamgogoal.view.interfaces.TargetView;

public class TargetPresenter extends BasePresenter {

    private TargetView targetView;
    private TargetModel targetModel;

    public TargetPresenter(TargetView targetView, TargetModel targetModel) {
        this.targetView = targetView;
        this.targetModel = targetModel;
    }

    public void onCreate() {
        targetView.setContentView();
    }

    @Override
    public void showMessage(String message) {

    }
}
