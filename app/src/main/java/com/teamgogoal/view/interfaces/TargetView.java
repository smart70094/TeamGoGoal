package com.teamgogoal.view.interfaces;

import android.view.View;

import java.util.List;
import java.util.Map;

public interface TargetView {

    void setContentView();

    void showTargetEdit(View view);

    void readData(List<Map<String, Object>> datas);

    void deleteTargetComplete();

    void showMessage(String message);

    void dismissProgressDialog();

    void moveProfile();
}
