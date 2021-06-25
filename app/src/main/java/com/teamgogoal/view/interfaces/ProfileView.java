package com.teamgogoal.view.interfaces;

import com.teamgogoal.dto.ProfileDto;

public interface ProfileView {

    void showMessage(String message);

    void dismissProgressDialog();

    void initProfile(ProfileDto profileDto);

    void validatePasswordFailure();

    void validatePasswordSuccess(String newPassword);

    void updateSuccess();
}
