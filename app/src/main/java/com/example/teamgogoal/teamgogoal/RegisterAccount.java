package com.example.teamgogoal.teamgogoal;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RegisterAccount extends AppCompatActivity {
    final String localhost = LoginActivity.getLocalHost();
    String CAresult;

    boolean CheckThreadWorking;
    EditText acc;
    EditText pass;
    EditText nameET;
    EditText emailET;
    Dialog dialog;
    SocketTrans socketTrans;
    //照片
    Dialog personal_photo_selector_dialog;
    ImageView personal_photo;
    public static final int EXTERNAL_STORAGE_REQ_CODE = 10;
    int clickCount;
    String imagepath = null;
    tempFileManager tempImgFile = new tempFileManager();
    Boolean haveUpLoadImg;
    String upLoadServerUri = null;
    private int serverResponseCode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_account);
        socketTrans=LoginActivity.socketTrans;
        CAresult = null;
        CheckThreadWorking = true;


        personal_photo = (ImageView) findViewById(R.id.personal_photo);
        acc = (EditText) findViewById(R.id.Account);
        pass = (EditText) findViewById(R.id.Password);
        nameET = (EditText) findViewById(R.id.Name);
        emailET=(EditText) findViewById(R.id.emailET);

        /*星球旋轉動畫*/
        ImageView iv = (ImageView) this.findViewById(R.id.imageView3);

        Animation am = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        am.setDuration(10000);
        am.setRepeatCount(Animation.INFINITE);
        am.setInterpolator(new LinearInterpolator());
        am.setStartOffset(0);
        iv.setAnimation(am);
        am.startNow();

        ScrollView scrollView = (ScrollView) this.findViewById(R.id.scrollView1);
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });



    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.transition.slide_out_right);
    }

    public void creat(View view) {

        CAresult = null;
        CheckThreadWorking = true;
        String account=acc.getText().toString().trim();
        String password=pass.getText().toString().trim();
        String name=nameET.getText().toString().trim();
        String email=emailET.getText().toString().trim();

        String title = "";
        String content = "";
        Boolean success = false;
        if(account.equals("") || password.equals("") || name.equals("") || email.equals("")){
            title = "TeamGoGoal";
            content = "請輸入完整資料";
            success = false;
        }else{
            new CheckAccount().execute("account=" + account);
            int count = 0;
            while (CheckThreadWorking) {
                try {
                    Thread.sleep(100);
                    count++;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (count == 10) {
                    break;
                }
            }

            if (!CAresult.matches(".*\\d+.*")) {

                socketTrans.setParams("addAccount",account,password,name,email);
                socketTrans.setActivity(this);
                socketTrans.send();


                title = "註冊成功";
                content = "你的帳號是" + acc.getText().toString() + "\n你的密碼是" + pass.getText().toString() + "\n你的暱稱是" + nameET.getText().toString()+"\n"+"請去信箱開通您的帳號";

                success = true;
            } else {
                title = "註冊失敗";
                content = "你的帳號" + acc.getText().toString() + "已經被使用\n請更換帳號後重試";
                success = false;
            }
        }

        showCompleteMsg(title, content, success);
    }

    private void showCompleteMsg(String title, String content, final boolean success) {

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
                if (success) {
                    dialog.dismiss();
                    dialog = null;
                    Intent i = new Intent();
                    i.setClass(RegisterAccount.this, LoginActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.transition.slide_out_right,R.transition.animo_no);
                } else {
                    dialog.dismiss();
                }
            }
        });

        dialog = new AlertDialog.Builder(this).setView(dialog_view).create();
        dialog.show();
    }

    public void cancel(View view) {
        RegisterAccount.this.finish();
    }

    public void personal_photo_selector(View view) {
        View dialog_view;

        dialog_view = LayoutInflater.from(this).inflate(R.layout.camera_selector, null);

        Button button_selectpic = (Button) dialog_view.findViewById(R.id.button_selectpic);
        Button uploadButton = (Button) dialog_view.findViewById(R.id.uploadButton);

        button_selectpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                personal_photo_selector_dialog.dismiss();
                if (android.support.v4.content.ContextCompat.checkSelfPermission(RegisterAccount.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    android.support.v4.app.ActivityCompat.requestPermissions(RegisterAccount.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_REQ_CODE);
                } else {
                    getPic();
                }
                if (clickCount > 0) {
                    tempImgFile.destory();
                }
                clickCount++;
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    public void run() {
                        //uploadFile(imagepath);
                    }
                }).start();
            }
        });

        personal_photo_selector_dialog = new AlertDialog.Builder(this, R.style.Translucent_NoTitle).setView(dialog_view).create();
        personal_photo_selector_dialog.show();
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
            personal_photo.setImageURI(selectedImageUri);
            tempImgFile.setFileUri(selectedImageUri);
            tempImgFile.uri2tempFile(this);
            imagepath = tempImgFile.getFilePath();
            haveUpLoadImg = true;
        }else{
            haveUpLoadImg = false;
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
            Log.e("uploadFile", "Source File not exist :" + imagepath);
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(RegisterAccount.this, "Source File not exist :" + imagepath, Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(RegisterAccount.this, "檔案上傳成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();
            } catch (MalformedURLException ex) {
                ex.printStackTrace();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(RegisterAccount.this, "MalformedURLException", Toast.LENGTH_SHORT).show();
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(RegisterAccount.this, "Got Exception : see logcat ", Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("Upload file Exception", "Exception : " + e.getMessage(), e);
            }
            return serverResponseCode;
        }
    }

    public class CheckAccount extends AsyncTask<String, Void, String> {
        @Override

        protected String doInBackground(String... params) {
            byte[] postData = new byte[0];
            String urlParameters = params[0];
            try {
                postData = urlParameters.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            int postDataLength = postData.length;
            String checkurl = localhost + "checkAccount.php";
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
                    sb.append(line);
                }
                br.close();
                conn.disconnect();
            } catch (Exception e) {
                Log.v("HaRuNa", e.toString());
            }

            CAresult = sb.toString();
            CheckThreadWorking = false;
            return sb.toString();

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            CAresult = s;
        }
    }
    //帥哥峻禾的部分結束----------------------------------------------------------------------------------------------------------------------
}
