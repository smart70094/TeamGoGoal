package com.teamgogoal.presenter;

import com.google.gson.JsonObject;
import com.teamgogoal.dto.ForgetPasswordDto;
import com.teamgogoal.service.ForgetPasswordApiService;
import com.teamgogoal.service.action.HttpExceptionAction;
import com.teamgogoal.utils.TggRetrofitUtils;
import com.teamgogoal.view.interfaces.ForgetPasswordView;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ForgetPasswordPresenter extends BasePresenter {

    private ForgetPasswordView forgetPasswordView;

    private ForgetPasswordApiService forgetPasswordApiService;

    public ForgetPasswordPresenter (ForgetPasswordView forgetPasswordView) {
        this.forgetPasswordView = forgetPasswordView;
        forgetPasswordApiService = TggRetrofitUtils.getTggService(ForgetPasswordApiService.class);
    }

    public void forgetPassword(String account) {
        ForgetPasswordDto forgetPasswordDto = ForgetPasswordDto.builder().account(account).build();
        Observable<JsonObject> forgetPasswordObservable = forgetPasswordApiService.forgetPassword(forgetPasswordDto);

        forgetPasswordObservable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(responseJson -> {
                String email = responseJson.get("email").getAsString();
                forgetPasswordView.forgetPasswordSubmitSuccess(email);
            }, new HttpExceptionAction(this));

    }

    @Override
    public void showMessage(String message) {
        forgetPasswordView.showMessage(message);
    }

    @Override
    public void dismissProgressDialog() {
        forgetPasswordView.dismissProgressDialog();
    }
}
