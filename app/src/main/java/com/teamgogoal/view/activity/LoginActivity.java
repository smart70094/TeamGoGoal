package com.teamgogoal.view.activity;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.EditText;
import android.widget.ImageView;

import com.teamgogoal.model.LoginModel;
import com.teamgogoal.presenter.LoginPresenter;
import com.teamgogoal.utils.ProgressDialogUtils;
import com.teamgogoal.utils.ToastUtils;
import com.teamgogoal.view.interfaces.LoginView;

import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity implements LoginView {

    private LoginPresenter loginPresenter;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginPresenter = new LoginPresenter(this, new LoginModel());

        progressDialog = ProgressDialogUtils.create(this);

        planetRotateAnimation();

        startNotificationService();
    }

    @Override
    public void startNotificationService() {
        Intent intent = new Intent(this, NotificationService.class);
        startService(intent);
    }

    @Override
    public void login(View view) {
        progressDialog.show();
        loginPresenter.login(getAccount(), getPassword());
    }

    @Override
    public <T> void switchView(Class<T> activityClass) {
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
        progressDialog.dismiss();
    }

    @Override
    public void showMessage(String message) {
        ToastUtils.showShortMessage(LoginActivity.this, message);
    }

    public String getAccount() {
        return getAccountEditTtext().getText().toString();
    }

    public String getPassword() {
        return getPasswordEditText().getText().toString();
    }

    public EditText getAccountEditTtext() {
        return findViewById(R.id.accountTxt);
    }

    public EditText getPasswordEditText() {
        return findViewById(R.id.passwordTxt);
    }

    private void planetRotateAnimation() {
        ImageView iv = this.findViewById(R.id.imageView3);
        Animation am = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        am.setDuration(10000);
        am.setRepeatCount(Animation.INFINITE);
        am.setInterpolator(new LinearInterpolator());
        am.setStartOffset(0);
        iv.setAnimation(am);
        am.startNow();
    }

    @Override
    public void dismissProgressDialog() {
        progressDialog.dismiss();
    }

    @OnClick(R.id.button4)
    public void moveForgetPassword(View view) {
        switchView(ForgetPasswordActivity.class);
    }

    @OnClick(R.id.button5)
    public void moveRegisterAccount(View view) {
        switchView(RegisterAccountActivity.class);
    }
}
