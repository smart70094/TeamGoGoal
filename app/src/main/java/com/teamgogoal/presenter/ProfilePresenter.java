package com.teamgogoal.presenter;

import com.google.gson.JsonObject;
import com.teamgogoal.dto.ProfileDto;
import com.teamgogoal.model.ProfileModel;
import com.teamgogoal.service.action.HttpExceptionAction;
import com.teamgogoal.view.interfaces.ProfileView;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ProfilePresenter extends BasePresenter{

    private ProfileView profileView;

    private ProfileModel profileModel;

    public ProfilePresenter(ProfileView profileView) {
        this.profileView = profileView;
        this.profileModel = new ProfileModel();
    }

    public void initProfile() {
        Observable<ProfileDto> profileObservable = profileModel.get();

        profileObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(profileDto->profileView.initProfile(profileDto), new HttpExceptionAction(this));
    }

    public void validatePassword(String account, String oldPassword, String newPassword) {
        Observable<JsonObject> validatePasswordObservable = profileModel.getValidatePassword(account, oldPassword);

        validatePasswordObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response->{
                    Boolean status = response.get("status").getAsBoolean();

                    if(status)
                        profileView.validatePasswordSuccess(newPassword);
                    else
                        profileView.validatePasswordFailure();

                }, new HttpExceptionAction(this));
    }

    public void update(ProfileDto profileDto) {
        Observable<Void> updateObservable = profileModel.update(profileDto);

        updateObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((o)-> profileView.updateSuccess(), new HttpExceptionAction(this));
    }

    public void showMessage(String message) {
        profileView.showMessage(message);
    }

    @Override
    public void dismissProgressDialog() {
        profileView.dismissProgressDialog();
    }
}
