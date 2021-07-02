package com.teamgogoal.view.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.teamgogoal.presenter.ForgetPasswordPresenter;
import com.teamgogoal.utils.DialogUtils;
import com.teamgogoal.utils.EditTextUtils;
import com.teamgogoal.utils.ProgressDialogUtils;
import com.teamgogoal.utils.ToastUtils;
import com.teamgogoal.view.interfaces.BaseView;
import com.teamgogoal.view.interfaces.ForgetPasswordView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ForgetPasswordActivity extends AppCompatActivity implements ForgetPasswordView {

    @BindView(R.id.account)
    EditText account;
    @BindView(R.id.button2)
    Button button2;

    private ForgetPasswordPresenter forgetPasswordPresenter;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        ButterKnife.bind(this);

        forgetPasswordPresenter = new ForgetPasswordPresenter(this);

        progressDialog = ProgressDialogUtils.create(this);
    }

    @OnClick
    public void forgetPassword(View view) {
        progressDialog.show();
        forgetPasswordPresenter.forgetPassword(EditTextUtils.getText(account));
    }

    @Override
    public void forgetPasswordSubmitSuccess(String email) {
        String message = String.format("重置密碼已寄送至註冊信箱「%s」，請儘速登入並更改密碼", email);
        Dialog dialog = DialogUtils.showHit(this, "TeamGoGoal", message);

        dialog.findViewById(R.id.hitComfirm).setOnClickListener((view)->{
            dialog.dismiss();
            switchView(LoginActivity.class);
        });

        progressDialog.dismiss();

    }

    @OnClick(R.id.imageButton2)
    public void moveLoginActivity() {
        switchView(LoginActivity.class);
    }

    @Override
    public <T> void switchView(Class<T> activityClass) {
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
        progressDialog.dismiss();
    }

    @Override
    public void showMessage(String message) {
        ToastUtils.showShortMessage(this, message);
    }

    @Override
    public void dismissProgressDialog() {
        progressDialog.dismiss();
    }
}
