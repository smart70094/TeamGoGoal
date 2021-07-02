package com.teamgogoal.view.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.teamgogoal.dto.ProfileDto;
import com.teamgogoal.presenter.ProfilePresenter;
import com.teamgogoal.utils.BitmapUtils;
import com.teamgogoal.utils.DialogUtils;
import com.teamgogoal.utils.EditTextUtils;
import com.teamgogoal.utils.ProgressDialogUtils;
import com.teamgogoal.utils.ToastUtils;
import com.teamgogoal.view.interfaces.ProfileView;

import java.io.FileNotFoundException;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfileActivity extends AppCompatActivity implements ProfileView {

    @BindView(R.id.imageView_pic)
    ImageView imageViewPic;

    @BindView(R.id.nickName)
    TextView nickName;

    @BindView(R.id.account)
    TextView account;

    @BindView(R.id.password)
    TextView password;

    @BindView(R.id.mail)
    TextView mail;

    @BindView(R.id.button8)
    Button submit;

    private ProfilePresenter profilePresenter;

    private ProfileDto profileDto;

    private Dialog editNameDialog = null;

    private Dialog editPasswordDialog = null;

    private Dialog editMailDialog = null;

    private ProgressDialog progressDialog;

    private Bitmap headImageBitmap;

    private Integer headImageId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        ButterKnife.bind(this);

        progressDialog = ProgressDialogUtils.create(this);

        progressDialog.show();

        profilePresenter = new ProfilePresenter(this);
        profilePresenter.initProfile(this);
    }

    public void initProfile(ProfileDto profileDto) {
        this.profileDto = profileDto;
        nickName.setText(profileDto.getName());
        account.setText(profileDto.getAccount());
        mail.setText(profileDto.getEmail());

        headImageId = profileDto.getHeadImageId();
        profilePresenter.initHeadImage(this, profileDto.getHeadImageId());
    }

    @Override
    public void initHeadImage(Bitmap bitmap) {
        loadingHeadImage(bitmap);
        progressDialog.dismiss();
    }

    @Override
    public void initHeadImageId(Integer headImageId) {
        this.headImageId = headImageId;
        progressDialog.dismiss();
        DialogUtils.showHit(this, "修改成功", "圖片已上傳，儲存後生效");

    }

    @OnClick(R.id.imageButton5)
    public void updateNameEvent() {
        View updateNameView = LayoutInflater.from(this).inflate(R.layout.change_name_dialog, null);

        Button comfirmButton = updateNameView.findViewById(R.id.Comfirm);
        EditText namedEditText = updateNameView.findViewById(R.id.nameET);
        namedEditText.setText(profileDto.getName());

        AppCompatActivity appCompatActivity = this;
        comfirmButton.setOnClickListener((View) -> {
                profileDto.setName(namedEditText.getText().toString());
                nickName.setText(namedEditText.getText().toString());
                editNameDialog.dismiss();
                DialogUtils.showHit(appCompatActivity, "修改成功", "暱稱變更為" +  namedEditText.getText().toString() + "，儲存後生效");
        });

        if(Objects.isNull(editNameDialog))
            editNameDialog = DialogUtils.createDialog(this, updateNameView,  R.style.Translucent_NoTitle);
        else
            editNameDialog.show();
    }

    @OnClick(R.id.imageButton6)
    public void updatePasswordEvent() {
        View updatePasswordView = LayoutInflater.from(this).inflate(R.layout.change_password_dialog, null);

        Button changePwdConfirm = updatePasswordView.findViewById(R.id.changePwdConfirm);
        Button changePwdCancel = updatePasswordView.findViewById(R.id.changePwdCancel);

        changePwdConfirm.setOnClickListener((view) -> {
                EditText oldPassword = updatePasswordView.findViewById(R.id.oldPassword);
                EditText newPassword = updatePasswordView.findViewById(R.id.newPassword);
                EditText newPasswordConfirm = updatePasswordView.findViewById(R.id.newPasswordConfirm);

               if (!newPassword.getText().toString().equals(newPasswordConfirm.getText().toString()))
                    DialogUtils.showHit(this,"修改失敗", "新密碼與新密碼確認不同\n請重新輸入");

               progressDialog.show();

               profilePresenter.validatePassword(profileDto.getAccount(), EditTextUtils.getText(oldPassword), EditTextUtils.getText(newPassword));
        });

        changePwdCancel.setOnClickListener((view)->editPasswordDialog.dismiss());

        editPasswordDialog = DialogUtils.createDialog(this, updatePasswordView, R.style.Translucent_NoTitle);
    }

    @Override
    public void validatePasswordFailure() {
        progressDialog.dismiss();
        DialogUtils.showHit(this,"修改失敗", "舊密碼輸入錯誤\n請重新輸入");
    }

    @Override
    public void validatePasswordSuccess(String newPassword) {
        progressDialog.dismiss();
        editPasswordDialog.dismiss();
        password.setText(newPassword);
        DialogUtils.showHit(this,"修改失敗", "密碼變更成功，儲存後生效");
    }

    @OnClick(R.id.imageButton7)
    public void updateEmailEvent() {
        View updateMailView = LayoutInflater.from(this).inflate(R.layout.change_mail_dialog, null);

        Button comfirmMailButton = updateMailView.findViewById(R.id.comfirmMail);
        EditText mailEditText = updateMailView.findViewById(R.id.mailEditText);
        mailEditText.setText(profileDto.getEmail());

        AppCompatActivity appCompatActivity = this;
        comfirmMailButton.setOnClickListener((View) -> {
            profileDto.setEmail(mailEditText.getText().toString());
            mail.setText(mailEditText.getText().toString());
            editMailDialog.dismiss();
            DialogUtils.showHit(appCompatActivity, "修改成功", "Mail變更為" +  mailEditText.getText().toString() + "，儲存後生效");
        });

        if(Objects.isNull(editMailDialog))
            editMailDialog = DialogUtils.createDialog(this, updateMailView,  R.style.Translucent_NoTitle);
        else
            editMailDialog.show();
    }

    @OnClick(R.id.button8)
    public void submitEvent(View view) {
        ProfileDto profileDto =
                ProfileDto.builder()
                        .name(nickName.getText().toString())
                        .account(account.getText().toString())
                        .password(password.getText().toString())
                        .email(mail.getText().toString())
                        .headImageId(headImageId)
                        .build();

        progressDialog.show();

        profilePresenter.update(profileDto);
    }

    @Override
    public void updateSuccess() {
        progressDialog.dismiss();

        DialogUtils.showHit(this, "資料儲存成功", "修改資料已生效");
    }

    @OnClick(R.id.imageButton4)
    public void personal_photo_selector(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {

            progressDialog.show();

            Uri uri = data.getData();
            ContentResolver cr = this.getContentResolver();

            String filename = getFileName(uri);

            Log.i("ProfileActivity", filename);

            try {
                headImageBitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                loadingHeadImage(headImageBitmap);
                profilePresenter.uploadHeadImage(this, filename, BitmapUtils.toBytes(headImageBitmap));
            } catch (FileNotFoundException e) {
                Log.e("Exception", e.getMessage(), e);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void loadingHeadImage(Bitmap bitmap) {
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
        roundedBitmapDrawable.setCircular(true);
        imageViewPic.setImageDrawable(roundedBitmapDrawable);
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
