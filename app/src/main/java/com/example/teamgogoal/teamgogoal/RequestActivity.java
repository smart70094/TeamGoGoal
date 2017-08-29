package com.example.teamgogoal.teamgogoal;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class RequestActivity extends AppCompatActivity {
    static Map<Integer,RequestUIStructure> requestMap=new HashMap<Integer,RequestUIStructure>();
    LinearLayout requestll;
    LoginActivity.User user;
    RequestDB db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        db=new RequestDB();
        requestll=(LinearLayout) findViewById(R.id.requestll);
        user=LoginActivity.getUser();
    }
    protected void loading(){
        synchronized(this) {
            new LoadingRequest().execute();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loading();
    }

    private class LoadingRequest  extends AsyncTask<Void,Void,String>{

        @Override
        protected String doInBackground(Void... voids) {
            return db.readRequest();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try{
                JSONArray array = new JSONArray(s);
                for (int i=0; i<array.length(); i++){
                    JSONObject obj = array.getJSONObject(i);
                    String rid = obj.getString("rid");
                    Integer key=Integer.parseInt(rid);
                    if(!requestMap.containsKey(key)){
                        String subject=obj.getString("subject");
                        String cmd=obj.getString("cmd");
                        final String cmdContext=obj.getString("cmdContext");
                        final String originator=obj.getString("originator");
                        RequestDB.RequestDetail rd=new RequestDB.RequestDetail(rid,subject,cmd,cmdContext,originator);

                        LinearLayout ll=new LinearLayout(RequestActivity.this);
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        layoutParams.setMargins(30, 20, 30, 0);
                        ll.setOrientation(LinearLayout.HORIZONTAL);
                        ll.setLayoutParams(layoutParams);
                        ll.setId(key);
                        TextView txt = new TextView(RequestActivity.this);
                        final String targetId = cmdContext.trim();
                        if(cmd.trim().equals("request_ask")) {
                            ll.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    //產生視窗物件
                                    final int id = v.getId();
                                    new AlertDialog.Builder(RequestActivity.this)
                                            .setTitle("TeamGoGoal")//設定視窗標題
                                            .setIcon(R.mipmap.ic_launcher)//設定對話視窗圖示
                                            .setMessage(originator + "邀請你加入任務" + cmdContext)//設定顯示的文字
                                            .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            })//設定結束的子視窗
                                            .setNegativeButton("加入", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    new Thread(new Runnable() {
                                                        public void run() {
                                                            String rid = Integer.toString(id).trim();
                                                            new DeleteRequest().execute(rid, targetId);
                                                        }
                                                    }).start();
                                                }
                                            })//設定結束的子視窗
                                            .show();//呈現對話視窗
                                }
                            });
                            txt.setText(originator + "邀請你加入任務" + cmdContext);
                        }else if(cmd.trim().equals("request_cheer")){
                            ll.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    //產生視窗物件
                                    final int id = v.getId();
                                    new AlertDialog.Builder(RequestActivity.this)
                                            .setTitle("TeamGoGoal")//設定視窗標題
                                            .setIcon(R.mipmap.ic_launcher)//設定對話視窗圖示
                                            .setMessage("來自"+originator + "的訊息\n" + cmdContext)//設定顯示的文字
                                            .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    String rid = Integer.toString(id).trim();
                                                    new DeleteRequest().execute(rid, targetId);
                                                }
                                            })//設定結束的子視窗
                                            .show();//呈現對話視窗
                                }
                            });
                            txt.setText("來自"+originator + "的訊息" + cmdContext);
                        }
                        ll.addView(txt);
                        requestll.addView(ll);
                        RequestUIStructure requestUIStructure = new RequestUIStructure(ll, txt, rd);
                        requestMap.put(key, requestUIStructure);
                    }
                }
            }catch(Exception e){

            }
        }


    }
    private class DeleteRequest extends AsyncTask<String,Void,String> {
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try{
                Integer key=Integer.parseInt(s.trim());
                RequestUIStructure requestUIS=requestMap.get(key);
               LinearLayout ll=requestUIS.ll;
                ((ViewGroup) ll.getParent()).removeView(ll);
                requestMap.remove(key);
            }catch(Exception e){
                Log.v("jim_deleteRequest",e.toString());
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String rid=params[0];
            String tid=params[1];
            TargetDB targetdb=new TargetDB();
            targetdb.createParticipator(tid,user.account);
            db.deleteRegisterRequest(rid);
            return rid;
        }
    }
    public class RequestUIStructure{
        LinearLayout ll;
        TextView txt;
        RequestDB.RequestDetail rd;
        RequestUIStructure(LinearLayout ll,TextView txt,RequestDB.RequestDetail rd){
            this.ll=ll;
            this.txt=txt;
            this.rd=rd;
        }
    }


}
