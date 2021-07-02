package com.teamgogoal.service.action;

import com.google.gson.JsonObject;
import com.teamgogoal.presenter.LoginPresenter;
import com.teamgogoal.utils.TggRetrofitUtils;
import com.teamgogoal.view.activity.TargetActivity;

import rx.functions.Action1;

public class LoginAction implements Action1<JsonObject> {

    private LoginPresenter loginPresenter;

    public LoginAction(LoginPresenter loginPresenter) {
        this.loginPresenter = loginPresenter;
    }

    @Override
    public void call(JsonObject jsonData) {
        String token = jsonData.get("authorization").getAsString();
        String id = jsonData.get("id").getAsString();

        TggRetrofitUtils.setToken(token);
        TggRetrofitUtils.setId(id);

        loginPresenter.startNotificationService();
        loginPresenter.showMessage("登入成功！");
        loginPresenter.switchView(TargetActivity.class);
    }
}
