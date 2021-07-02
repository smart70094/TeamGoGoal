package com.teamgogoal.view.interfaces;

public interface BaseView {
    <T> void switchView(Class<T> activityClass);

    void showMessage(String message);

    void dismissProgressDialog();
}
