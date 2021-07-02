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
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.teamgogoal.dto.ProfileDto;
import com.teamgogoal.presenter.RegisterAccountPresenter;
import com.teamgogoal.utils.BitmapUtils;
import com.teamgogoal.utils.DialogUtils;
import com.teamgogoal.utils.EditTextUtils;
import com.teamgogoal.utils.ProgressDialogUtils;
import com.teamgogoal.utils.ToastUtils;
import com.teamgogoal.view.interfaces.RegisterAccountView;

import java.io.FileNotFoundException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterAccountActivity extends AppCompatActivity implements RegisterAccountView {

    @BindView(R.id.imageButton2)
    ImageButton imageButton2;
    @BindView(R.id.personal_photo)
    ImageView personalPhoto;
    @BindView(R.id.Name)
    EditText Name;
    @BindView(R.id.Account)
    EditText Account;
    @BindView(R.id.Password)
    EditText Password;
    @BindView(R.id.emailET)
    EditText emailET;
    @BindView(R.id.button3)
    Button button3;
    @BindView(R.id.imageView3)
    ImageView imageView3;
    private RegisterAccountPresenter registerAccountPresenter;

    private ProgressDialog progressDialog;

    private Integer headImageId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_account);
        ButterKnife.bind(this);

        registerAccountPresenter = new RegisterAccountPresenter(this);

        progressDialog = ProgressDialogUtils.create(this);

        planetRotateAnimation();
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

    @OnClick(R.id.imageButton2)
    public void moveLoginActivity(View view) {
        switchView(LoginActivity.class);
    }

    private void planetRotateAnimation() {
        Animation am = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        am.setDuration(10000);
        am.setRepeatCount(Animation.INFINITE);
        am.setInterpolator(new LinearInterpolator());
        am.setStartOffset(0);
        imageView3.setAnimation(am);
        am.startNow();
    }

    @OnClick(R.id.personal_photo)
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
                Bitmap headImageBitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), headImageBitmap);
                roundedBitmapDrawable.setCircular(true);
                personalPhoto.setImageDrawable(roundedBitmapDrawable);
                registerAccountPresenter.uploadHeadImage(this, filename, BitmapUtils.toBytes(headImageBitmap));
            } catch (FileNotFoundException e) {
                Log.e("Exception", e.getMessage(), e);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private String getFileName(Uri uri) {
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

    @Override
    public void initHeadImageId(Integer headImageId) {
        this.headImageId = headImageId;
        progressDialog.dismiss();
        Log.i("RegisterAccountActivity", "Upload Head Image Id:" + this.headImageId);
    }

    @OnClick(R.id.button3)
    public void registerAccountSubmit() {
        ProfileDto profileDto =
                ProfileDto.builder()
                        .name(EditTextUtils.getText(Name))
                        .account(EditTextUtils.getText(Account))
                        .password(EditTextUtils.getText(Password))
                        .email(EditTextUtils.getText(emailET))
                        .headImageId(headImageId)
                        .build();

        progressDialog.show();

        registerAccountPresenter.registerAccount(profileDto);
    }

    @Override
    public void registerAccountSuccess() {
        progressDialog.dismiss();

        Dialog dialog = DialogUtils.showHit(this, "TeamGoGoal", "註冊成功，請至Email啟動帳號，即可開始使用");

        dialog.findViewById(R.id.hitComfirm).setOnClickListener((view)->{
            dialog.dismiss();
            switchView(LoginActivity.class);
        });
    }
}
