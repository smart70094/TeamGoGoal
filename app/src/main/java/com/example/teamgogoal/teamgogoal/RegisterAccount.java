package com.example.teamgogoal.teamgogoal;

import android.app.Dialog;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register_account);
        CAresult = null;
        CheckThreadWorking = true;
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
                new CreateAccount().execute("account=" + account + "&password=" + password + "&name=" + name + "&role=user" +"&email="+email,email);
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

    //帥哥峻禾的部分開始----------------------------------------------------------------------------------------------------------------------
    class CreateAccount extends AsyncTask<String, Void, Void> {

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
            String checkurl = localhost + "createAccount.php";
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

            try {
                String uid=  sb.toString().trim();
                String email=params[1].trim();
                SocketTrans socketTrans=new SocketTrans();
                socketTrans.connection();
                socketTrans.setParams("register_email",email,uid,localhost+"activeAccount?uid="+uid.trim());
                socketTrans.send();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
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
