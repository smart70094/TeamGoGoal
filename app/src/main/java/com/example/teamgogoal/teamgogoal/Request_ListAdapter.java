package com.example.teamgogoal.teamgogoal;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Request_ListAdapter extends BaseAdapter {
    RequestDB db;
    ViewHolder holder;
    Context context;
    private LayoutInflater myInflater;
    List<HashMap<String, String>> list = new ArrayList<>();

    public Request_ListAdapter(Context context,RequestDB db) {
        myInflater = LayoutInflater.from(context);
        this.context = context;
        this.db = db;
    }

    public void setData(List<HashMap<String, String>> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        if (convertView == null) {

            convertView = myInflater.inflate(R.layout.request_list, null);
            holder = new ViewHolder();
            holder.request_context = (TextView) convertView.findViewById(R.id.request_context);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String cmd = list.get(position).get("cmd").trim();
        String cmdContext = list.get(position).get("cmdContext").trim();
        String originator = list.get(position).get("originator").trim();

        switch(cmd) {
            case "request_ask":
                request_ask(originator, cmdContext,convertView,position);
                break;
        }


        return convertView;
    }

    private void request_ask(final String originator,final String cmdContext, View convertView, final int position) {
        holder.request_context.setText(originator + "邀請你加入任務" + cmdContext);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String id = list.get(position).get("rid");
                //final int id = v.getId();
                new AlertDialog.Builder(context)
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
                                        String rid = id;
                                        new DeleteRequest().execute(rid, cmdContext);
                                        list.remove(position);
                                    }
                                }).start();
                            }
                        })//設定結束的子視窗
                        .show();//呈現對話視窗
            }
        });
    }

    public class DeleteRequest extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            String rid = params[0];
            String tid = params[1];
            TargetDB targetdb = new TargetDB();
            targetdb.createParticipator(tid, LoginActivity.user.account);
            db.deleteRegisterRequest(rid);
            return null;
        }
    }

    static class ViewHolder {
        ImageView personal_photo;
        TextView request_context;
    }

}