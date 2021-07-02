package com.teamgogoal.view.interfaces;

import java.util.Map;

public interface TargetEditView extends BaseView {

    void readOneTargetComplete(Map<String, Object> data);

    void updateTargetComplete();

    void showMessage(String message);
}
