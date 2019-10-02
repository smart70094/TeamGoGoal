package com.teamgogoal.presenter;

import com.teamgogoal.model.TargetModel;
import com.teamgogoal.view.interfaces.TargetEditView;

public class TargetEditPresenter extends BasePresenter {

    private TargetEditView targetEditView;
    private TargetModel targetModel;

    public TargetEditPresenter(TargetEditView targetEditView, TargetModel targetModel) {
        this.targetEditView = targetEditView;
        this.targetModel = targetModel;
    }

    public void onCreate() { targetEditView.setContentView();}
}
