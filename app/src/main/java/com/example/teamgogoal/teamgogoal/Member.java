package com.example.teamgogoal.teamgogoal;

import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Member extends AppCompatActivity {
    Dialog dialog;
    String currTid,targetName;
    SocketTrans socketTrans = LoginActivity.socketTrans;
    LoginActivity.User user;
    GridView gridView;

    List<HashMap<String, Object>> member_list = new ArrayList<>();
    HashMap<String,Drawable> member_photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);

        Bundle bundle = getIntent().getExtras();
        user=LoginActivity.getUser();
        currTid = bundle.getString("tid");
        targetName=bundle.getString("targetName");

        gridView = (GridView) findViewById(R.id.attendMember);

        initView();
    }

    private void initView() {
        member_photo = TaskActivity.member_photo;
        for (Map.Entry<String, Drawable> entry : member_photo.entrySet()) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("account" , entry.getKey());
            map.put("personal_photo" , entry.getValue());
            member_list.add(map);
        }
        Member_ListAdapter adapter = new Member_ListAdapter(this);
        adapter.setData(member_list);
        gridView.setNumColumns(5);
        gridView.setAdapter(adapter);
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
