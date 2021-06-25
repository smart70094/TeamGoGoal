package com.teamgogoal.view.interfaces;

import android.view.View;

public interface LoginView {

    void setContentView();

    void login(View view);

    <T> void switchView(Class<T> activityClass);

    void showMessage(String message);

    void dismissProgressDialog();
}
