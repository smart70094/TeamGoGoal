package com.teamgogoal.model;

import com.google.gson.JsonObject;
import com.teamgogoal.presenter.LoginPresenter;
import com.teamgogoal.service.LoginApiService;
import com.teamgogoal.service.action.HttpExceptionAction;
import com.teamgogoal.service.action.LoginAction;
import com.teamgogoal.utils.TggRetrofitUtils;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LoginModel {

    public void login(LoginPresenter loginPresenter, String account, String password) {
        LoginApiService loginApiService = TggRetrofitUtils.getTggService(LoginApiService.class);
        Observable<JsonObject> loginObservable = loginApiService.login(account, password);
        invokeLoginApi(loginPresenter, loginObservable);
    }

    private void invokeLoginApi(LoginPresenter loginPresenter, Observable<JsonObject> observable) {
        observable.subscribeOn(Schedulers.io()) // 在子线程中进行Http访问
                .observeOn(AndroidSchedulers.mainThread()) // UI线程处理返回接口
                .subscribe(new LoginAction(loginPresenter), new HttpExceptionAction(loginPresenter));

    }

    private JsonObject getLoginJsonData(String account, String password) {
        JsonObject jsonData = new JsonObject();
        jsonData.addProperty("Account", account);
        jsonData.addProperty("Password", password);

        return  jsonData;
    }
}
