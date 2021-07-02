package com.teamgogoal.presenter;

import com.teamgogoal.model.TargetModel;
import com.teamgogoal.utils.ToastUtils;
import com.teamgogoal.view.interfaces.TargetEditView;

import java.util.Map;

public class TargetEditPresenter extends BasePresenter {

    private TargetEditView targetEditView;
    private TargetModel targetModel;

    public TargetEditPresenter(TargetEditView targetEditView, TargetModel targetModel) {
        this.targetEditView = targetEditView;
        this.targetModel = targetModel;
    }

    public void addTarget(Map<String, Object> params) {
        targetModel.addTarget(this, params);
    }

    public void updateTarget(Map<String, Object> params) { targetModel.updateTarget(this, params);}

    public void updateTargetComplete() {
        targetEditView.updateTargetComplete();
    }

    public void readOneTarget(int id) {
        targetModel.readOneTarget(this, id);
    }

    public void readOneTargetComplete(Map<String, Object> data) {
        targetEditView.readOneTargetComplete(data);
    }

    @Override
    public void dismissProgressDialog() {
        targetEditView.dismissProgressDialog();
    }

    public void showMessage(String message) {
        targetEditView.showMessage(message);
    }
}
