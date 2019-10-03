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

    public void showShortMessage(String message) {
        targetEditView.showShortMessage(message);
    }

    public void addTarget(Map<String, Object> params) {
        targetModel.addTarget(this, params);
    }

    public void addTargetComplete() {
        showShortMessage("新增目標成功!");
    }


    public void onCreate() { targetEditView.setContentView();}
}
