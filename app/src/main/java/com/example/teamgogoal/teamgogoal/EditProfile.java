package com.example.teamgogoal.teamgogoal;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.teamgogoal.view.activity.R;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Created by -HaRuNa- on 2017/8/21.
 */

public class EditProfile extends AppCompatActivity{
    String localhost = LoginActivity.getLocalHost();
    LoginActivity.User user;

    private int serverResponseCode = 0;
    private ProgressDialog progressdialog = null;
    private String upLoadServerUri = null;
    private String imagepath = null;
    private int clickCount;

    public static final int EXTERNAL_STORAGE_REQ_CODE = 10;

    private TextView name;
    private ImageView imageview;
    private TextView account;
    private TextView mail;

    /*Dialog*/
    View dialog_view;

    private Dialog dialog;
    private Dialog hit_dialog;
    private Dialog personal_photo_selector_dialog;

    private tempFileManager tempImgFile = new tempFileManager();
    private boolean haveUploadImg;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        haveUploadImg = false;

        user = LoginActivity.getUser();
        name = (TextView) findViewById(R.id.nickName);
        account = (TextView) findViewById(R.id.account);
        mail = (TextView)findViewById(R.id.mail);
        imageview = (ImageView) findViewById(R.id.imageView_pic);


        clickCount = 0;
        String imageUrl = localhost + "profilepicture/" + user.uid;

        upLoadServerUri = localhost + "UploadToServer.php?uid=" + user.uid;

        name.setText(user.name);
        account.setText(user.account);
        mail.setText(user.email);


        new TransTask().execute(imageUrl);

    }

    public void EditProfileEvent(View view) {
        if(haveUploadImg){
            progressdialog = ProgressDialog.show(EditProfile.this, "", "檔案上傳中...", true);
            Toast.makeText(EditProfile.this, "上傳圖片" + imagepath, Toast.LENGTH_SHORT).show();
            new Thread(new Runnable() {
                public void run() {
                    uploadFile(imagepath);
                }
            }).start();
        }
    }



    private class TransTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);
                return bitmap;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            imageview.setImageDrawable(toCircleImage(result));
            super.onPostExecute(result);
        }
    }

    private void showHit(String title, String content, final boolean success) {

        View dialog_view;

        dialog_view = LayoutInflater.from(this).inflate(R.layout.hit_dialog, null);

        TextView hitTitle = dialog_view.findViewById(R.id.hitTitle);
        TextView hitContent = dialog_view.findViewById(R.id.hitContent);
        TextView hitComfirm = dialog_view.findViewById(R.id.hitComfirm);

        hitTitle.setText(title);
        hitContent.setText(content);

        hitComfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hit_dialog.dismiss();
                if (success) {
                    dialog.dismiss();
                }else{
                    hit_dialog.dismiss();
                }
            }
        });

        hit_dialog = new AlertDialog.Builder(this, R.style.hitStyle).setView(dialog_view).create();
        hit_dialog.show();

        Window dialogWindow = hit_dialog.getWindow();
        WindowManager m = this.getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高度
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        p.height = (int) (d.getHeight() * 0.7); // 高度设置为屏幕的0.6，根据实际情况调整
        p.width = (int) (d.getWidth() * 0.5); // 宽度设置为屏幕的0.65，根据实际情况调整
        dialogWindow.setAttributes(p);
    }

    //修改暱稱
    public void ChangeName(View view) {

        View dialog_view;
        dialog_view = LayoutInflater.from(this).inflate(R.layout.change_name_dialog, null);

        Button Comfirm = (Button) dialog_view.findViewById(R.id.Comfirm);
        final EditText nameET = (EditText) dialog_view.findViewById(R.id.nameET);

        nameET.setText(user.name);


        Comfirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                new ChangeNameTask().execute("uid=" + user.uid + "&name=" + nameET.getText()).toString();
                user.name = nameET.getText().toString();
                name.setText(nameET.getText().toString());
                dialog.dismiss();
                showHit("修改成功", "暱稱變更為" +  nameET.getText().toString(), true);
            }
        });

        dialog = new AlertDialog.Builder(this, R.style.Translucent_NoTitle).setView(dialog_view).create();
        dialog.show();
        Window dialogWindow = dialog.getWindow();
        WindowManager m = this.getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高度
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        p.height = (int) (d.getHeight() * 0.7); // 高度设置为屏幕的0.6，根据实际情况调整
        p.width = (int) (d.getWidth() * 0.8); // 宽度设置为屏幕的0.65，根据实际情况调整
        dialogWindow.setAttributes(p);
    }

    public class ChangeNameTask extends AsyncTask<String, Void, Void> {
        @Override

        protected Void doInBackground(String... params) {
            byte[] postData = new byte[0];
            String urlParameters = params[0];
            try {
                postData = urlParameters.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            int postDataLength = postData.length;
            String checkurl = localhost + "changeName.php";
            Log.v("localhost:", checkurl);
            StringBuilder sb = null;
            try {
                URL connectto = new URL(checkurl);
                HttpURLConnection conn = (HttpURLConnection) connectto.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("Accept-Charset", "UTF-8");
                conn.setRequestProperty("Accept-Encoding", "UTF-8");
                conn.setUseCaches(false);
                conn.setAllowUserInteraction(false);
                conn.setInstanceFollowRedirects(false);
                conn.setDoOutput(true);


                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                wr.write(postData);
                wr.flush();
                wr.close();

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                sb = new StringBuilder();
                String line;

                while ((line = br.readLine()) != null) {
                    Log.v("HaRuNa", line);
                    sb.append(line + "\n");
                }
                br.close();
                conn.disconnect();
            } catch (Exception e) {
                Log.v("HaRuNa", e.toString());
            }
            //return sb.toString();
            return null;
        }
    }

    //修改密碼
    public void ChangePassword(View view) {

        dialog_view = LayoutInflater.from(this).inflate(R.layout.change_password_dialog, null);

        Button changePwdConfirm = (Button) dialog_view.findViewById(R.id.changePwdConfirm);
        Button changePwdCancel = (Button) dialog_view.findViewById(R.id.changePwdCancel);


        changePwdConfirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText oldPassword = (EditText) (dialog_view.findViewById(R.id.oldPassword));
                EditText newPassword = (EditText) (dialog_view.findViewById(R.id.newPassword));
                EditText newPasswordConfirm = (EditText) (dialog_view.findViewById(R.id.newPasswordConfirm));

                if (!oldPassword.getText().toString().equals(user.password)) {
                    showHit("修改失敗", "舊密碼輸入錯誤\n請重新輸入", false);
                } else if (!newPassword.getText().toString().equals(newPasswordConfirm.getText().toString())) {
                    showHit("修改失敗", "新密碼與新密碼確認不同\n請重新輸入", false);
                } else {
                    new EditProfile.ChangePasswordTask().execute("uid=" + user.uid + "&password=" + newPassword.getText().toString());
                    user.password = newPassword.getText().toString();
                    showHit("修改成功", "密碼變更成功", true);
                }
            }
        });

        changePwdCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog = new AlertDialog.Builder(this, R.style.Translucent_NoTitle).setView(dialog_view).create();
        dialog.show();
        Window dialogWindow = dialog.getWindow();
        WindowManager m = this.getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高度
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        p.height = (int) (d.getHeight() * 0.7); // 高度设置为屏幕的0.6，根据实际情况调整
        p.width = (int) (d.getWidth() * 0.8); // 宽度设置为屏幕的0.65，根据实际情况调整
        dialogWindow.setAttributes(p);
    }

    public class ChangePasswordTask extends AsyncTask<String, Void, Void> {
        @Override

        protected Void doInBackground(String... params) {
            byte[] postData = new byte[0];
            String urlParameters = params[0];
            try {
                postData = urlParameters.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            int postDataLength = postData.length;
            String checkurl = localhost + "changePassword.php";
            Log.v("localhost:", checkurl);
            StringBuilder sb = null;
            try {
                URL connectto = new URL(checkurl);
                HttpURLConnection conn = (HttpURLConnection) connectto.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("Accept-Charset", "UTF-8");
                conn.setRequestProperty("Accept-Encoding", "UTF-8");
                conn.setUseCaches(false);
                conn.setAllowUserInteraction(false);
                conn.setInstanceFollowRedirects(false);
                conn.setDoOutput(true);


                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                wr.write(postData);
                wr.flush();
                wr.close();

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                sb = new StringBuilder();
                String line;

                while ((line = br.readLine()) != null) {
                    Log.v("HaRuNa", line);
                    sb.append(line + "\n");
                }
                br.close();
                conn.disconnect();
            } catch (Exception e) {
                Log.v("HaRuNa", e.toString());
            }
            //return sb.toString();
            return null;
        }
    }

    //照片部分---------------------------------------------------------------------------------------------------------------------------
    public void personal_photo_selector(View view) {
        View dialog_view;

        dialog_view = LayoutInflater.from(this).inflate(R.layout.camera_selector, null);

        Button button_selectpic = (Button) dialog_view.findViewById(R.id.button_selectpic);

        button_selectpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                personal_photo_selector_dialog.dismiss();
                if (android.support.v4.content.ContextCompat.checkSelfPermission(EditProfile.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    android.support.v4.app.ActivityCompat.requestPermissions(EditProfile.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_REQ_CODE);
                } else {
                    getPic();
                }
                if (clickCount > 0) {
                    tempImgFile.destory();
                }
                clickCount++;
            }
        });

        personal_photo_selector_dialog = new AlertDialog.Builder(this, R.style.hitStyle).setView(dialog_view).create();
        personal_photo_selector_dialog.show();
        Window dialogWindow = dialog.getWindow();
        WindowManager m = this.getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高度
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        p.height = (int) (d.getHeight() * 0.3); // 高度设置为屏幕的0.6，根据实际情况调整
        p.width = (int) (d.getWidth() * 0.5); // 宽度设置为屏幕的0.65，根据实际情况调整
        dialogWindow.setAttributes(p);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            getPic();
        }
    }

    public void getPic() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Complete action using"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            imageview.setImageURI(selectedImageUri);
            tempImgFile.setFileUri(selectedImageUri);
            tempImgFile.uri2tempFile(this);
            imagepath = tempImgFile.getFilePath();
            haveUploadImg = true;
        }else{
            haveUploadImg = false;
        }
    }

    public int uploadFile(String sourceFilePath) {
        String fileName = sourceFilePath;
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1024 * 1024;
        File sourceFile = new File(sourceFilePath);

        if (!sourceFile.isFile()) {
            progressdialog.dismiss();
            Log.e("uploadFile", "Source File not exist :" + imagepath);
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(EditProfile.this, "Source File not exist :" + imagepath, Toast.LENGTH_SHORT).show();
                }
            });
            return 0;
        } else {
            try {

                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUri);
                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + fileName + "\"" + lineEnd);

                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // showTargetEvent file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                if (serverResponseCode == 200) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            String msg = "File Upload Completed.\n\n See uploaded file your server. \n\n";
                            Toast.makeText(EditProfile.this, "檔案上傳成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();
            } catch (MalformedURLException ex) {
                progressdialog.dismiss();
                ex.printStackTrace();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(EditProfile.this, "MalformedURLException", Toast.LENGTH_SHORT).show();
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                progressdialog.dismiss();
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(EditProfile.this, "Got Exception : see logcat ", Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("Upload file Exception", "Exception : " + e.getMessage(), e);
            }
            progressdialog.dismiss();
            haveUploadImg = false;
            return serverResponseCode;
        }
    }


    protected Drawable toCircleImage(Bitmap bp) {
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bp);
        roundedBitmapDrawable.setCircular(true);
        return roundedBitmapDrawable;
    }

    public void toTarget(View view) {
        finish();
    }

    public void toMemory(View view) {
        finish();
        Intent intent = new Intent(this, Review.class);
        startActivity(intent);
    }

    public void toRequest(View view) {
        finish();
        Intent intent = new Intent();
        intent.setClass(this, RequestActivity.class);
        startActivity(intent);
    }

    public void toQuestion(View view) {
        finish();
        Intent intent = new Intent();
        intent.setClass(this, Question.class);
        startActivity(intent);
    }
}