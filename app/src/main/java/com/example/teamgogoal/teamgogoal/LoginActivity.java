package com.example.teamgogoal.teamgogoal;

import android.app.Notification;
import android.app.NotificationChannel;
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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
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

public class LoginActivity extends AppCompatActivity {
    //public static final String localhost="http://169.254.68.146/DB/";
    //public static final String ip="106.107.161.179";
    public static final String ip="114.26.229.5";
    //public static final String ip="1.165.110.246";
    public static final String localhost="http://"+ip+"/TeamGoGoal/";
    EditText accountTxt,passwordTxt;
    CheckBox chkRemeberAccount;

    Intent intent;
    public static  User user=null;
    public static SocketTrans socketTrans;
    private SharedPreferences settings;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        accountTxt=(EditText) findViewById(R.id.accountTxt);
        passwordTxt=(EditText) findViewById(R.id.passwordTxt);
        chkRemeberAccount=(CheckBox) findViewById(R.id.chkRemeberUser);
        settings=getSharedPreferences("account",0);
        accountTxt.setText(settings.getString("account",""));
        passwordTxt.setText(settings.getString("password",""));
        if(!(accountTxt.getText().toString().equals("") && passwordTxt.getText().toString().equals("")))
            chkRemeberAccount.setChecked(true);
        else
            chkRemeberAccount.setChecked(false);

        socketTrans=new SocketTrans();
        socketTrans.setActivity(LoginActivity.this);
        socketTrans.setNotification((NotificationManager)getSystemService(NOTIFICATION_SERVICE));

    }
    public void showNotification(String contentText){
        try{
            NotificationChannel channelMsg = new NotificationChannel(
                    "msg",
                    "Channel msg",
                    NotificationManager.IMPORTANCE_HIGH);
            channelMsg.setDescription("socketMsg");
            channelMsg.enableLights(true);
            channelMsg.enableVibration(true);
            NotificationManager notificationManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channelMsg);

            Notification.Builder builder =
                    new Notification.Builder(this)
                            .setSmallIcon(android.R.drawable.ic_notification_clear_all)
                            .setContentTitle("TeamGoGoal")
                            .setContentText(contentText)
                            .setChannelId("msg");
            notificationManager.notify(1, builder.build());
        }catch(Exception e){
            Log.v("jim_showNotification",e.toString());
        }
    }
    public static User getUser(){return user;}
    public static String getLocalHost(){return localhost;}
    public static String getIp(){return ip;}

    //帥哥峻禾的部分開始----------------------------------------------------------------------------------------------------------------------
    String CAresult = null;
    boolean CheckThreadWorking = true;

    public void creat(View view) {
        LayoutInflater inflater = LayoutInflater.from(LoginActivity.this);
        final View v = inflater.inflate(R.layout.create_new_account, null);

        CAresult = null;
        CheckThreadWorking = true;

        new AlertDialog.Builder(LoginActivity.this)
                .setTitle("請輸入欲註冊之帳號密碼")
                .setView(v)
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText acc = (EditText) (v.findViewById(R.id.Account));
                        EditText pass = (EditText) (v.findViewById(R.id.Password));
                        EditText name = (EditText) (v.findViewById(R.id.Name));
                        //viaParams("account=" + acc.getText().toString() + "&password=" + pass.getText().toString() + "&role=user");

                        new LoginActivity.CheckAccount().execute("account=" + acc.getText().toString() + "&password=" + pass.getText().toString() + "&name=" + name.getText().toString() + "&role=user");
                        int count=0;
                        while(CheckThreadWorking){
                            try {
                                Thread.sleep(100);
                                count++;
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if(count==10){
                                break;
                            }
                        }

                        if(!CAresult.matches(".*\\d+.*")){
                            new LoginActivity.CreateAccount().execute("account=" + acc.getText().toString() + "&password=" + pass.getText().toString() + "&name=" + name.getText().toString() + "&role=user");
                            Toast.makeText(getApplicationContext(), "你的帳號是" + acc.getText().toString() + "\n你的密碼是" + pass.getText().toString() + "\n你的暱稱是" + name.getText().toString() + "\nuid=" + CAresult, Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "你的帳號" + acc.getText().toString() + "已經被使用\n請更換帳號後重試" + "\nuid=" + CAresult, Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                .setNeutralButton("取消",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // TODO Auto-generated method stub
                        Toast.makeText(LoginActivity.this, "取消",Toast.LENGTH_SHORT).show();
                    }

                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                    }
                })
                .show();



    }
    //帥哥峻禾的部分結束----------------------------------------------------------------------------------------------------------------------








    private class TransTask  extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String account=params[0];
            String password=params[1];
            String urlParameters="table=account & account="+params[0]+" & password="+params[1];
            String php="connectAccountDB.php";
            byte[] postData = new byte[0];
            try {
                postData = urlParameters.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            int postDataLength = postData.length;
            String checkurl = LoginActivity.getLocalHost()+php;
            Log.v("localhost:",checkurl);
            StringBuilder sb=null;
            try {
                URL connectto = new URL(checkurl);
                HttpURLConnection conn = (HttpURLConnection) connectto.openConnection();
                conn.setRequestMethod( "POST" );
                conn.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
                conn.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty( "Accept-Charset", "UTF-8");
                conn.setRequestProperty( "Accept-Encoding", "UTF-8");
                conn.setUseCaches(false);
                conn.setAllowUserInteraction(false);
                conn.setInstanceFollowRedirects( false );
                conn.setDoOutput( true );


                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                wr.write(postData);
                wr.flush();
                wr.close();

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                sb = new StringBuilder();
                String line;

                while ((line = br.readLine()) != null) {
                    //Log.v("jim",line);
                    sb.append(line+"\n");
                }
                br.close();
                conn.disconnect();
            }
            catch (Exception e) {
                Log.v("jim",e.toString());
            }
            parseJSON(sb.toString());



            String cmd="loginFailure";
            try{
                if(user!=null){
                    socketTrans.setParams("register_online",user.account);
                    socketTrans.connection();
                    socketTrans.receiver();
                    cmd="connectSuccessful";
                }
            }catch(Exception e){
                cmd="ConnectFailure";
            }
            return cmd;
        }

        @Override
        protected void onPostExecute(String cmd) {
            super.onPostExecute(cmd);
            switch (cmd) {
                case "connectSuccessful":
                    intent = new Intent();

                    intent.setClass(LoginActivity.this, TargetActivity.class);
                    startActivity(intent);
                    Toast.makeText(LoginActivity.this, "登入成功", Toast.LENGTH_SHORT).show();
                    if(chkRemeberAccount.isChecked()){
                        settings=getSharedPreferences("account",0);
                        settings.edit()
                                .putString("account",accountTxt.getText().toString())
                                .putString("password",passwordTxt.getText().toString())
                                .commit();
                    }else{
                        settings=getSharedPreferences("account",0);
                        settings.edit()
                                .putString("account","")
                                .putString("password","")
                                .commit();
                    }

                    break;
                case "ConnectFailure":
                    Toast.makeText(LoginActivity.this, "網路出現問題", Toast.LENGTH_SHORT).show();
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
                for (int i=0; i<array.length(); i++){
                    JSONObject obj = array.getJSONObject(i);
                    String uid = obj.getString("uid");
                    String account = obj.getString("account");
                    String password = obj.getString("password");
                    String name = obj.getString("name");
                    String role = obj.getString("role");
                    Log.d("JSON:",uid+"/"+account+"/"+password+"/"+role+"/");
                    user = new User(uid,account,password,name,role);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class User implements  Cloneable{
        String uid,account,password,name,state;
        public User(String uid,String account,String password,String mame,String state){
            this.uid = uid;
            this.account = account;
            this.password = password;
            this.name = name;
            this.state=state;
        }
        protected User clone() throws CloneNotSupportedException {
            return (User) super.clone();
        }
    }
    public void login(View view) {

        String account=accountTxt.getText().toString();
        String password=passwordTxt.getText().toString();
        new TransTask().execute(account,password);
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
            Log.v("localhost:",checkurl);
            StringBuilder sb=null;
            try {
                URL connectto = new URL(checkurl);
                HttpURLConnection conn = (HttpURLConnection) connectto.openConnection();
                conn.setRequestMethod( "POST" );
                conn.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
                conn.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty( "Accept-Charset", "UTF-8");
                conn.setRequestProperty( "Accept-Encoding", "UTF-8");
                conn.setUseCaches(false);
                conn.setAllowUserInteraction(false);
                conn.setInstanceFollowRedirects( false );
                conn.setDoOutput( true );


                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                wr.write(postData);
                wr.flush();
                wr.close();

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                sb = new StringBuilder();
                String line;

                while ((line = br.readLine()) != null) {
                    Log.v("HaRuNa",line);
                    sb.append(line+"\n");
                }
                br.close();
                conn.disconnect();
            }
            catch (Exception e) {
                Log.v("HaRuNa",e.toString());
            }
            //return sb.toString();
            return null;
        }

    }

    public class CheckAccount extends AsyncTask<String, Void, String>{
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
            Log.v("localhost:",checkurl);
            StringBuilder sb=null;
            try {
                URL connectto = new URL(checkurl);
                HttpURLConnection conn = (HttpURLConnection) connectto.openConnection();
                conn.setRequestMethod( "POST" );
                conn.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
                conn.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty( "Accept-Charset", "UTF-8");
                conn.setRequestProperty( "Accept-Encoding", "UTF-8");
                conn.setUseCaches(false);
                conn.setAllowUserInteraction(false);
                conn.setInstanceFollowRedirects( false );
                conn.setDoOutput( true );


                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                wr.write(postData);
                wr.flush();
                wr.close();

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                sb = new StringBuilder();
                String line;

                while ((line = br.readLine()) != null) {
                    Log.v("HaRuNa",line);
                    sb.append(line);
                }
                br.close();
                conn.disconnect();
            }
            catch (Exception e) {
                Log.v("HaRuNa",e.toString());
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