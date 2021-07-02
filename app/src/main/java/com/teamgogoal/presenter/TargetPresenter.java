package com.teamgogoal.presenter;

import com.teamgogoal.model.TargetModel;
import com.teamgogoal.view.interfaces.TargetView;

import java.util.List;
import java.util.Map;

public class TargetPresenter extends BasePresenter {

    private TargetView targetView;
    private TargetModel targetModel;

    public TargetPresenter(TargetView targetView, TargetModel targetModel) {
        this.targetView = targetView;
        this.targetModel = targetModel;
    }

    public void onCreate() {
        targetModel.readTarget(this);
    }

    public void readData(List<Map<String, Object>> datas) {
        targetView.readData(datas);
    }

    public void deleteData(int id) {
        targetModel.deleteTarget(this, id);
    }

    public void deleteComplete() {
        showMessage("刪除目標成功!");
        targetModel.readTarget(this);
    }

    @Override
    public void showMessage(String message) {
        targetView.showMessage(message);
    }

    @Override
    public void dismissProgressDialog() {
        targetView.dismissProgressDialog();
    }
}
