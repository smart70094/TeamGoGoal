package com.teamgogoal.view.interfaces;

import android.view.View;

import java.util.List;
import java.util.Map;

public interface TargetView extends BaseView{

    void showTargetEdit(View view);

    void readData(List<Map<String, Object>> datas);

    void deleteTargetComplete();

}
