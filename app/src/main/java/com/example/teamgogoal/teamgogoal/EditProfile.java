package com.example.teamgogoal.teamgogoal;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class EditProfile extends Activity implements OnClickListener {
    String localhost = LoginActivity.getLocalHost();
    LoginActivity.User user;

    private Button uploadButton, btnselectpic;
    private int serverResponseCode = 0;
    private ProgressDialog dialog = null;
    private String upLoadServerUri = null;
    private String imagepath = null;
    private int clickCount;

    public static final int EXTERNAL_STORAGE_REQ_CODE = 10;

    private EditText name;
    private ImageView imageview;
    private TextView account;
    private Dialog changePwdDialog;
    private Dialog hit_dialog;

    private tempFileManager tempImgFile = new tempFileManager();


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        user = LoginActivity.getUser();
        name = (EditText) findViewById(R.id.nickName);
        account = (TextView) findViewById(R.id.account);
        uploadButton = (Button) findViewById(R.id.uploadButton);
        btnselectpic = (Button) findViewById(R.id.button_selectpic);
        imageview = (ImageView) findViewById(R.id.imageView_pic);

        clickCount = 0;
        String imageUrl = localhost + "profile picture/" + user.uid;

        btnselectpic.setOnClickListener(this);
        uploadButton.setOnClickListener(this);
        upLoadServerUri = localhost + "UploadToServer.php?uid=" + user.uid;
        //upLoadServerUri = "http://along.event2007.com/m/UploadToServer.php";

        //account.setText("帳號：" + user.account);
        name.setText(user.name);
        account.setText(user.account);
        new AsyncTask<String, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(String... params) {
                String url = params[0];
                return getBitmapFromURL(url);
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                imageview.setImageDrawable(toCircleImage(result));
                super.onPostExecute(result);
            }
        }.execute(imageUrl);
    }

    public Bitmap getBitmapFromURL(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
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

    public void ChangeName(View view) {
        new EditProfile.ChangeNameTask().execute("uid=" + user.uid + "&name=" + name.getText().toString());
        Toast.makeText(getApplicationContext(), "暱稱變更為" + name.getText().toString(), Toast.LENGTH_SHORT).show();
    }

    public void ChangePassword(View view) {

        View dialog_view;
        dialog_view = LayoutInflater.from(this).inflate(R.layout.hit_dialog, null);
        Hit hit = new Hit();
        hit.setHitTitle((TextView) dialog_view.findViewById(R.id.hitTitle));
        hit.setHtiContent((TextView) dialog_view.findViewById(R.id.hitContent));
        hit.setConfirm((Button) dialog_view.findViewById(R.id.hitComfirm));

        LayoutInflater inflater = LayoutInflater.from(EditProfile.this);
        final View v = inflater.inflate(R.layout.change_password, null);

        Button changePwdConfirm = (Button) v.findViewById(R.id.changePwdConfirm);
        Button changePwdCancel = (Button) v.findViewById(R.id.changePwdCancel);


        changePwdConfirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText oldPassword = (EditText) (v.findViewById(R.id.oldPassword));
                EditText newPassword = (EditText) (v.findViewById(R.id.newPassword));
                EditText newPasswordConfirm = (EditText) (v.findViewById(R.id.newPasswordConfirm));

                if (!oldPassword.getText().toString().equals(user.password)) {
                    showCompleteMsg("修改失敗", "舊密碼輸入錯誤\n請重新輸入",false);
                } else if (!newPassword.getText().toString().equals(newPasswordConfirm.getText().toString())) {
                    showCompleteMsg("修改失敗", "新密碼與新密碼確認不同\n請重新輸入",false);
                } else {
                    new EditProfile.ChangePasswordTask().execute("uid=" + user.uid + "&password=" + newPassword.getText().toString());
                    user.password = newPassword.getText().toString();
                    showCompleteMsg("修改成功", "密碼變更成功",true);
                }
            }
        });


        changePwdCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                changePwdDialog.dismiss();
            }
        });

        changePwdDialog = new AlertDialog.Builder(this).setView(v).create();
        changePwdDialog.show();
    }

    private void showCompleteMsg(String title, String content,final boolean success) {

        View dialog_view;

        dialog_view = LayoutInflater.from(this).inflate(R.layout.hit_dialog, null);

        Hit hit = new Hit();
        hit.setHitTitle((TextView) dialog_view.findViewById(R.id.hitTitle));
        hit.setHtiContent((TextView) dialog_view.findViewById(R.id.hitContent));
        hit.setConfirm((Button) dialog_view.findViewById(R.id.hitComfirm));

        hit.getHitTitle().setText(title);
        hit.gethtiContent().setText(content);

        hit.getConfirm().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hit_dialog.dismiss();
                if(success){
                    changePwdDialog.dismiss();
                }


            }});

        hit_dialog = new AlertDialog.Builder(this).setView(dialog_view).create();
        hit_dialog.show();
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

    //上傳照片部分---------------------------------------------------------------------------------------------------------------------------
    public void getPic() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Complete action using"), 1);
    }

    @Override
    public void onClick(View arg0) {
        if (arg0 == btnselectpic) {
            if (android.support.v4.content.ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                android.support.v4.app.ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_REQ_CODE);
            } else {
                getPic();
            }
            if (clickCount > 0) {
                tempImgFile.destory();
            }
            clickCount++;
        } else if (arg0 == uploadButton) {
            dialog = ProgressDialog.show(EditProfile.this, "", "檔案上傳中...", true);
            new Thread(new Runnable() {
                public void run() {
                    uploadFile(imagepath);
                }
            }).start();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            imageview.setImageURI(selectedImageUri);
            tempImgFile.setFileUri(selectedImageUri);
            tempImgFile.uri2tempFile(this);
            imagepath = tempImgFile.getFilePath();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            getPic();
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
            dialog.dismiss();
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

                // read file and write it into form...
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
                dialog.dismiss();
                ex.printStackTrace();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(EditProfile.this, "MalformedURLException", Toast.LENGTH_SHORT).show();
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                dialog.dismiss();
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(EditProfile.this, "Got Exception : see logcat ", Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("Upload file Exception", "Exception : " + e.getMessage(), e);
            }
            dialog.dismiss();
            //tempImgFile.destory();
            //tempImgFile = null;
            return serverResponseCode;

        }
    }

    protected Drawable toCircleImage(Bitmap bp) {
        // 邊框, 影子寬度
        int borderWidth = 1;
        int shadowWidth = 25;

        // 邊框, 影子顏色
        int borderColor= Color.BLACK;
        int shadowColor=Color.BLUE;

        // 取得圖片
        Resources mResources = getResources();
        Bitmap srcBitmap = bp;

        // 以圖片大小為尺寸, 建立一塊畫布
        int srcBitmapWidth = srcBitmap.getWidth();
        int srcBitmapHeight = srcBitmap.getHeight();
        int dstBitmapWidth = Math.min(srcBitmapWidth,srcBitmapHeight)+borderWidth*2;
        Bitmap dstBitmap = Bitmap.createBitmap(dstBitmapWidth,dstBitmapWidth, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(dstBitmap);
        canvas.drawColor(Color.YELLOW);
        canvas.drawBitmap(srcBitmap, (dstBitmapWidth - srcBitmapWidth) / 2, (dstBitmapWidth - srcBitmapHeight) / 2, null);

        // 在畫布上畫邊線
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(borderWidth * 2);
        paint.setColor(borderColor);
        canvas.drawCircle(canvas.getWidth() / 2, canvas.getHeight() / 2, canvas.getWidth() / 2, paint);

        // 在畫布上畫影子
        paint.setColor(shadowColor);
        paint.setStrokeWidth(shadowWidth);
        canvas.drawCircle(canvas.getWidth()/2,canvas.getHeight()/2,canvas.getWidth()/2,paint);

        // 將圖片切圓角
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(mResources, dstBitmap);
        roundedBitmapDrawable.setCircular(true);

        return roundedBitmapDrawable;
    }
}