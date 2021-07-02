package com.teamgogoal.presenter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.gson.JsonObject;
import com.teamgogoal.dto.ProfileDto;
import com.teamgogoal.jpa.entity.File;
import com.teamgogoal.model.FileModel;
import com.teamgogoal.model.ProfileModel;
import com.teamgogoal.service.action.HttpExceptionAction;
import com.teamgogoal.view.interfaces.ProfileView;

import java.io.IOException;
import java.util.Objects;

import okhttp3.ResponseBody;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class ProfilePresenter extends BasePresenter{

    private ProfileView profileView;

    private ProfileModel profileModel;

    private FileModel fileModel;

    public ProfilePresenter(ProfileView profileView) {
        this.profileView = profileView;
        this.profileModel = new ProfileModel();
        this.fileModel = new FileModel();
    }

    public void initProfile(Context context) {
        Observable<ProfileDto> profileObservable = profileModel.get();

        profileObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(profileDto-> profileView.initProfile(profileDto)
                , new HttpExceptionAction(this));
    }

    public void initHeadImage(Context context, int headImageId) {
        Observable<File> localHeadImageObservable = fileModel.getFromDb(context, headImageId);
        Observable<ResponseBody> apiHeadImageObservable = fileModel.get(headImageId);
        Observable<File> apiAndCreateLocalObservable =
                apiHeadImageObservable.flatMap((responseBody) -> {
                    try {

                        byte[] image = responseBody.bytes();

                        fileModel.createToLocalDb(context, headImageId, image)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe();

                        File file = File.builder().id(headImageId).image(image).build();
                        return Observable.just(file);
                    } catch (IOException e) {
                        Log.e("ProfilePresenter", e.toString());
                        throw new IllegalStateException(e.toString());
                    }
                });

        Observable<?> headImageObservable =
                localHeadImageObservable.flatMap((file) -> Objects.isNull(file) ? apiAndCreateLocalObservable : Observable.just(file));

        headImageObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result->{
                    File file = (File) result;
                    profileView.initHeadImage(BitmapFactory.decodeByteArray(file.getImage(), 0, file.getImage().length));
                });
    }

    public void uploadHeadImage(Context context, String filename, byte[] image) {
        Observable<File> apiObservable = fileModel.create(filename, image);

        apiObservable.flatMap((file)->{
            fileModel.createToLocalDb(context, file.getId(), image).subscribeOn(Schedulers.io()).subscribe();
            return Observable.just(file);
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(file->profileView.initHeadImageId(file.getId()));
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
