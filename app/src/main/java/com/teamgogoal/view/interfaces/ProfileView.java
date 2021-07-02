package com.teamgogoal.view.interfaces;

import android.graphics.Bitmap;

import com.teamgogoal.dto.ProfileDto;
import com.teamgogoal.jpa.entity.File;

public interface ProfileView {

    void showMessage(String message);

    void dismissProgressDialog();

    void initProfile(ProfileDto profileDto);

    void initHeadImage(Bitmap bitmap);

    void initHeadImageId(Integer headImageId);

    void validatePasswordFailure();

    void validatePasswordSuccess(String newPassword);

    void updateSuccess();
}
