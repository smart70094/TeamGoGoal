package com.teamgogoal.service.action;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.teamgogoal.presenter.BasePresenter;
import com.teamgogoal.utils.ToastUtils;

import java.io.IOException;

import retrofit2.HttpException;
import rx.functions.Action1;

public class HttpExceptionAction implements Action1 {

    private BasePresenter basePresenter;

    public HttpExceptionAction(BasePresenter basePresenter) {
        this.basePresenter = basePresenter;
    }

    @Override
    public void call(Object o) {
        HttpException httpException = (HttpException) o;
        String message = getExceptionMessage(httpException);
        basePresenter.dismissProgressDialog();
        basePresenter.showMessage(message);
    }

    private String getExceptionMessage(HttpException httpException) {
        String message = "系統發生異常！";
        try {
            int code = httpException.code();
            if(code == 666 || code == 400) {
                String response = httpException.response().errorBody().string();
                JsonObject responseJson = new Gson().fromJson(response, JsonObject.class);
                message =  responseJson.get("Message").getAsString();
            }
        } catch (IOException e) {
            Log.e("IO", "read data failure", e);
        }

        return message;
    }
}
