package com.teamgogoal.model;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.teamgogoal.presenter.TargetEditPresenter;
import com.teamgogoal.presenter.TargetPresenter;
import com.teamgogoal.service.TargetApiService;
import com.teamgogoal.service.action.HttpExceptionAction;
import com.teamgogoal.utils.TggRetrofitUtils;
import com.teamgogoal.view.activity.TargetActivity;

import java.lang.reflect.Type;
import java.util.List;
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
                    targetEditPresenter.updateTargetComplete();
                }, new HttpExceptionAction(targetEditPresenter));
    }

    public void updateTarget(TargetEditPresenter targetEditPresenter, Map<String, Object> params) {
        Observable<JsonObject> observable =
                TggRetrofitUtils.getTggService(TargetApiService.class).updateTarget(params);

        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resposne->{
                    targetEditPresenter.updateTargetComplete();
                }, new HttpExceptionAction(targetEditPresenter));
    }

    public void readTarget(TargetPresenter targetPresenter) {
        Observable<JsonObject> observable =
                TggRetrofitUtils.getTggService(TargetApiService.class).readTarget();

        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resposne->{
                    JsonArray dataJsonArray = (JsonArray) resposne.getAsJsonArray("targets");
                    JsonElement jsonElement = resposne.get("targets");
                    Type listType = new TypeToken<List<Map<String, Object>>>() {}.getType();
                    List<Map<String, Object>> datas = new Gson().fromJson(jsonElement, listType);
                    datas.stream().forEach(data->{
                        data.put("id", ((Double) data.get("id")).intValue());
                    });
                    targetPresenter.readData(datas);
                }, new HttpExceptionAction(targetPresenter));
    }

    public void readOneTarget(TargetEditPresenter targetEditPresenter, int id) {
        Observable<JsonObject> observable =
                TggRetrofitUtils.getTggService(TargetApiService.class).readOneTarget(id);

        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resposne->{
                    JsonElement jsonElement = resposne;
                    Type listType = new TypeToken<Map<String, Object>>() {}.getType();
                    Map<String, Object> data = new Gson().fromJson(jsonElement, listType);
                    data.put("id", ((Double) data.get("id")).intValue());
                    targetEditPresenter.readOneTargetComplete(data);
                }, new HttpExceptionAction(targetEditPresenter));
    }

    public void deleteTarget(TargetPresenter targetPresenter, int id) {
        Observable<JsonObject> observable =
                TggRetrofitUtils.getTggService(TargetApiService.class).deleteTarget(id);

        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resposne->{
                    targetPresenter.deleteComplete();
                }, new HttpExceptionAction(targetPresenter));
    }

}
