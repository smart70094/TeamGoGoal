package com.example.teamgogoal.teamgogoal;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.teamgogoal.view.activity.R;

public class ForgetPassword extends AppCompatActivity {
    AlertDialog dialog = null;
    EditText account,email;
    Button submitForgetPwd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        account = (EditText) findViewById(R.id.account);
        email = (EditText)findViewById(R.id.email);


    }

    //------左上返回健------//
    public void cancel(View view) {
        finish();
    }

    //------提交按鈕------//
    public void submitForgetPwd(View view) {
        String _account = account.getText().toString();
        String _email = email.getText().toString();

        if(_account.equals("")){
            showHit("1","請輸入您的帳號");
        }else if(_email.equals("")){
            showHit("1","請輸入您的信箱");
        }else if(!_email.contains("@")){
            showHit("1","信箱格式不正確");
        }
        else{
            new forgetPasswordThread().execute(_account, _email);
        }
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
            showHit("1","帳密已寄到您的信箱\n請至您的信箱收信");
        }
    }

    //------顯示提示窗------//
    private void showHit(String numBtn,String context) {
        Hit hit = new Hit(numBtn, this);

        hit.set_hitTitle("提示");
        hit.set_hitContent(context);


        hit.get_hitConfirm().setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog = new AlertDialog.Builder(this, R.style.hitStyle).setView(hit.get_view()).show();
    }


}
