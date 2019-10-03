package com.teamgogoal.model;

import com.google.gson.JsonObject;
import com.teamgogoal.presenter.TargetEditPresenter;
import com.teamgogoal.service.TargetApiService;
import com.teamgogoal.service.action.HttpExceptionAction;
import com.teamgogoal.utils.TggRetrofitUtils;

import java.util.Map;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class TargetModel {

    public void addTarget(TargetEditPresenter targetEditPresenter, Map<String, Object> params) {
        Observable<JsonObject> observable =
                TggRetrofitUtils.getTggService(TargetApiService.class).addTarget(params);

        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resposne->{
                    targetEditPresenter.addTargetComplete();
                }, new HttpExceptionAction(targetEditPresenter));
    }

}
