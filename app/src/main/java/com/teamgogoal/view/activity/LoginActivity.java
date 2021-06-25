package com.teamgogoal.view.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.teamgogoal.model.LoginModel;
import com.teamgogoal.presenter.LoginPresenter;
import com.teamgogoal.utils.ProgressDialogUtils;
import com.teamgogoal.utils.ToastUtils;
import com.teamgogoal.view.interfaces.LoginView;

public class LoginActivity extends AppCompatActivity implements LoginView {

    private LoginPresenter loginPresenter;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginPresenter = new LoginPresenter(this, new LoginModel());
        loginPresenter.onCreate();

        progressDialog = ProgressDialogUtils.create(this);
    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_login);
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

    @Override
    public void dismissProgressDialog() {
        progressDialog.dismiss();
    }
}
