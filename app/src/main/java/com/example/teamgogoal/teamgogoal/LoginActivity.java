package com.example.teamgogoal.teamgogoal;

import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LoginActivity extends AppCompatActivity {
    //public static final String localhost="http://169.254.68.146/DB/";
    public static final String ip = "192.168.0.101";
    //public static final String ip="111.253.228.128";
    public static final String localhost = "http://" + ip + "/TeamGoGoal/";
    EditText accountTxt, passwordTxt;
    Intent intent;
    public static User user = null;
    public static SocketTrans socketTrans;
    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        accountTxt = (EditText) findViewById(R.id.accountTxt);
        passwordTxt = (EditText) findViewById(R.id.passwordTxt);
        settings = getSharedPreferences("account", 0);
        accountTxt.setText(settings.getString("account", ""));
        passwordTxt.setText(settings.getString("password", ""));
       if(settings.getString("lastDate", "").equals("")) {
            settings.edit()
                    .putString("lastDate", "2016-11-20")
                    .commit();
        }





        socketTrans = new SocketTrans();
        socketTrans.setActivity(LoginActivity.this);
        socketTrans.setNotification((NotificationManager) getSystemService(NOTIFICATION_SERVICE));



        if(!accountTxt.getText().toString().equals("") && !passwordTxt.getText().toString().equals("")){
            login(null);
        }



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

    public static User getUser() {
        return user;
    }

    public static String getLocalHost() {
        return localhost;
    }

    public static String getIp() {
        return ip;
    }

    //帥哥峻禾的部分開始----------------------------------------------------------------------------------------------------------------------

    public void creat(View view) {
        Intent i = new Intent();
        i.setClass(this, RegisterAccount.class);
        startActivity(i);
        overridePendingTransition(R.transition.slide_in_right, R.transition.animo_no);
    }


    //影片測試
    public void toTestVideo(View view) {
        finish();
        Intent intent = new Intent(this,videoTest.class);
        startActivity(intent);
    }

    //帥哥峻禾的部分結束----------------------------------------------------------------------------------------------------------------------

    private class TransTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String cmd = "loginFailure";
            try {
                String account = params[0];
                String password = params[1];
                String urlParameters = "table=account & account=" + account + " & password=" + password;
                String php = "connectAccountDB.php";
                String result = viaParams(urlParameters, php);
                parseJSON(result);
                if (user != null) {
                    socketTrans.setParams("register_online", user.account);
                    socketTrans.connection();
                    socketTrans.receiver();
                    cmd = "connectSuccessful";
                }
            } catch (Exception e) {
                cmd = "ConnectFailure";
            }
            return cmd;
        }

        @Override
        protected void onPostExecute(String cmd) {
            super.onPostExecute(cmd);
            switch (cmd) {
                case "connectSuccessful":

                    //提醒設定




                    intent = new Intent();
                    intent.setClass(LoginActivity.this, TargetActivity.class);
                    startActivity(intent);
                    Toast.makeText(LoginActivity.this, "登入成功", Toast.LENGTH_SHORT).show();

                    settings=getSharedPreferences("account",0);

                    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
                    String date=sdf.format(new java.util.Date());
                    Date curDate=null;
                    try {
                        Date lastDate = sdf.parse(settings.getString("lastDate",""));
                        curDate=sdf.parse(date);
                        Intent i=new Intent(LoginActivity.this,RegisterAlarmService.class);
                        if(curDate.after(lastDate))
                            i.putExtra("state","N");
                        else
                            i.putExtra("state","Y");
                        i.putExtra("cmd","loading");
                        startService(i);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    settings.edit()
                            .putString("account",accountTxt.getText().toString())
                            .putString("password",passwordTxt.getText().toString())
                            .putString("lastDate",sdf.format(curDate))
                            .commit();


                    break;
                case "ConnectFailure":
                    Toast.makeText(LoginActivity.this, "網路出現問題", Toast.LENGTH_SHORT).show();

                    settings=getSharedPreferences("account",0);

                    settings.edit()
                            .putString("account","")
                            .putString("password","")
                            .commit();

                    finish();
                    break;
                case "loginFailure":
                    Toast.makeText(LoginActivity.this, "帳號或密碼錯誤請重新登入", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        private void parseJSON(String s) {
            try {
                JSONArray array = new JSONArray(s);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    String uid = obj.getString("uid");
                    String account = obj.getString("account");
                    String password = obj.getString("password");
                    String name = obj.getString("name");
                    String email=obj.getString("email");
                    String role = obj.getString("role");
                    Log.d("JSON:", uid + "/" + account + "/" + password + "/" + role + "/");
                    user = new User(uid, account, password, name, email,role);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void forgetPassword(View view) {
        //Test Data
        String account = "123";
        String email = "smart70094@yahoo.com.tw";

        /*
        from interface get data
        String account=view.getAccount();
        String email=view.getEmail();
        */
        new forgetPasswordThread().execute(account, email);
    }

    private class forgetPasswordThread extends AsyncTask<String, Void, Void> {
        protected Void doInBackground(String... params) {
            try {
                String account = params[0];
                String email = params[1];
                SocketTrans socket = new SocketTrans();
                socket.connection();
                socket.setParams("register_forgetPassword", account, email);
                socket.send();
                //return socketTrans.getResult();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(LoginActivity.this, "帳密已寄到您的信箱！\n請去您的信箱收信", Toast.LENGTH_SHORT).show();
        }
    }



    public class User implements Cloneable {
        String uid, account, password, name, state,email;

        public User(String uid, String account, String password, String name, String email,String state) {
            this.uid = uid;
            this.account = account;
            this.password = password;
            this.name = name;
            this.email=email;
            this.state = state;
        }

        protected User clone() throws CloneNotSupportedException {
            return (User) super.clone();
        }
    }

    public void login(View view) {

        String account = accountTxt.getText().toString();
        String password = passwordTxt.getText().toString();
        new TransTask().execute(account, password);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub

        if (keyCode == KeyEvent.KEYCODE_BACK) { // 攔截返回鍵
            new AlertDialog.Builder(LoginActivity.this)
                    .setTitle("確認視窗")
                    .setMessage("你不要走")
                    .setIcon(android.R.drawable.sym_def_app_icon)
                    .setPositiveButton("確定",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                    .setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO Auto-generated method stub

                                }
                            }).show();
        }
        return true;
    }

    public String viaParams(String urlParameters, String php) throws Exception {
        byte[] postData = new byte[0];
        try {
            postData = urlParameters.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        int postDataLength = postData.length;
        String checkurl = LoginActivity.getLocalHost() + php;
        Log.v("localhost:", checkurl);
        StringBuilder sb = null;

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
            //Log.v("jim",line);
            sb.append(line + "\n");
        }
        br.close();
        conn.disconnect();
        return sb.toString();
    }
}