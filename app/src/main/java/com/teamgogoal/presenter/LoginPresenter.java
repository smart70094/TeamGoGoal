package com.teamgogoal.presenter;

import com.teamgogoal.model.LoginModel;
import com.teamgogoal.view.interfaces.LoginView;

import lombok.Getter;

public class LoginPresenter extends BasePresenter{
    @Getter
    private LoginView loginView;
    private LoginModel loginModel;


    public LoginPresenter(LoginView loginView, LoginModel loginModel) {
        this.loginView = loginView;
        this.loginModel = loginModel;
    }

    public void onCreate() {
        loginView.setContentView();
    }

    public void login(String account, String password) {
        loginModel.login(this, account, password);
    }

    public <T>  void switchView(Class<T> activityClass) {
        loginView.switchView(activityClass);
    }

    public void showMessage(String message) {
        loginView.showMessage(message);
    }
}
