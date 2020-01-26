package com.teamgogoal.view.interfaces;

import java.util.Map;

public interface TargetEditView {

    void setContentView();

    void showShortMessage(String message);

    void readOneTargetComplete(Map<String, Object> data);

    void updateTargetComplete();
}
