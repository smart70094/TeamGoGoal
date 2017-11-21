package com.example.teamgogoal.teamgogoal;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Member extends AppCompatActivity {
    Dialog dialog;
    String currTid,targetName;
    SocketTrans socketTrans = LoginActivity.socketTrans;
    LoginActivity.User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);

        Bundle bundle = getIntent().getExtras();
        user=LoginActivity.getUser();
        currTid = bundle.getString("tid");
        targetName=bundle.getString("targetName");
    }

    public void addMember(View view) {

        View dialog_view;

        dialog_view = LayoutInflater.from(this).inflate(R.layout.add_member_dialog, null);


        //成員ID取得變數 memberID
        final EditText memberID = dialog_view.findViewById(R.id.memberID);
        Button addMemberBtn = dialog_view.findViewById(R.id.addMemberBtn);

        addMemberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //按鈕事件處理
                String ParticipatorStr=memberID.getText().toString();
                ParticipatorStr=ParticipatorStr.replace(",","-");

                socketTrans.setParams("register_request",user.account,ParticipatorStr,targetName,currTid);
                socketTrans.send();
                Toast.makeText(view.getContext(), "邀請成功", Toast.LENGTH_SHORT).show();
            }
        });

        dialog = new AlertDialog.Builder(this).setView(dialog_view).create();
        dialog.show();
    }

    public void requestParticipator(View view){

    }
}
