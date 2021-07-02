package com.teamgogoal.presenter;

import android.content.Context;

import com.teamgogoal.dto.ProfileDto;
import com.teamgogoal.jpa.entity.File;
import com.teamgogoal.model.FileModel;
import com.teamgogoal.model.RegisterAccountModel;
import com.teamgogoal.service.action.HttpExceptionAction;
import com.teamgogoal.view.interfaces.RegisterAccountView;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RegisterAccountPresenter extends BasePresenter {

    private RegisterAccountView registerAccountView;

    private RegisterAccountModel registerAccountModel;

    private FileModel fileModel;

    public RegisterAccountPresenter(RegisterAccountView registerAccountView) {
        this.registerAccountView = registerAccountView;
        this.fileModel = new FileModel();
        this.registerAccountModel = new RegisterAccountModel();
    }

    @Override
    public void showMessage(String message) {
        registerAccountView.showMessage(message);
    }

    @Override
    public void dismissProgressDialog() {
        registerAccountView.dismissProgressDialog();
    }

    public void uploadHeadImage(Context context, String filename, byte[] image) {

        Observable<File> apiObservable = fileModel.create(filename, image);

        apiObservable.flatMap((file)->{
            fileModel.createToLocalDb(context, file.getId(), image).subscribeOn(Schedulers.io()).subscribe();
            return Observable.just(file);
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(file->registerAccountView.initHeadImageId(file.getId()), new HttpExceptionAction(this));
    }

    public void registerAccount(ProfileDto profileDto) {
        Observable<Void> apiObservable = registerAccountModel.registerAccount(profileDto);

        apiObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((object)->registerAccountView.registerAccountSuccess(), new HttpExceptionAction(this));
    }
}
