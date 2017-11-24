package com.example.teamgogoal.teamgogoal;

import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
    GridView attendgridView,invitegridView;

    List<HashMap<String, Object>> member_list = new ArrayList<>();
    List<HashMap<String, Object>> invite_member_list = new ArrayList<>();
    HashMap<String,Drawable> member_photo,invite_photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);

        Bundle bundle = getIntent().getExtras();
        user=LoginActivity.getUser();
        currTid = bundle.getString("tid");
        targetName=bundle.getString("targetName");

        attendgridView = (GridView) findViewById(R.id.attendMember);
        invitegridView = (GridView) findViewById(R.id.inviteMember);
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
        Member_ListAdapter attend_adapter = new Member_ListAdapter(this);
        attend_adapter.setData(member_list);
        attendgridView.setNumColumns(5);
        attendgridView.setAdapter(attend_adapter);

        invite_photo = TaskActivity.invite_member_photo;
        for (Map.Entry<String, Drawable> entry : invite_photo.entrySet()) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("account" , entry.getKey());
            map.put("personal_photo" , entry.getValue());
            invite_member_list.add(map);
        }
        Invite_Member_ListAdapter invite_adapter = new Invite_Member_ListAdapter(this);
        invite_adapter.setData(invite_member_list);
        invitegridView.setNumColumns(5);
        invitegridView.setAdapter(invite_adapter);

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

        dialog = new AlertDialog.Builder(this,R.style.hitStyle).setView(dialog_view).create();
        dialog.show();
        Window dialogWindow = dialog.getWindow();
        WindowManager m = this.getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高度
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        p.height = (int) (d.getHeight() * 0.7); // 高度设置为屏幕的0.6，根据实际情况调整
        p.width = (int) (d.getWidth() * 0.8); // 宽度设置为屏幕的0.65，根据实际情况调整
        dialogWindow.setAttributes(p);



    }

    public void requestParticipator(View view){

    }
}
