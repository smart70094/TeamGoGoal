package com.example.teamgogoal.teamgogoal;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Request_ListAdapter extends BaseAdapter {
    RequestDB db;
    TargetDB targetDB;
    ViewHolder holder;
    Context context;
    private LayoutInflater myInflater;
    List<HashMap<String, String>> list = new ArrayList<>();
    Handler handler = new Handler();
    SocketTrans socketTrans;
    AlertDialog dialog;

    public Request_ListAdapter(Context context, RequestDB db) {
        myInflater = LayoutInflater.from(context);
        this.context = context;
        this.db = db;
        targetDB = new TargetDB();
        socketTrans = LoginActivity.socketTrans;
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
        String tid = list.get(position).get("tid").trim();
        switch (cmd) {
            case "request_ask":
                request_ask(originator, cmdContext, tid, convertView, position);
                break;
            case "request_cheer":
                request_cheer(originator, cmdContext, convertView, position);
                break;
            case "request_update":
                request_update(originator, cmdContext, tid, convertView, position);
                break;
            case "request_delete":
                request_delete(originator, cmdContext, tid, convertView, position);
                break;
            case "request_getCheer":
                request_getCheer(originator, cmdContext, tid, convertView, position);

        }
        return convertView;
    }

    private void request_ask(final String originator, final String cmdContext, final String tid, View convertView, final int position) {

        holder.request_context.setText(cmdContext);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String id = list.get(position).get("rid");
                new AlertDialog.Builder(context)
                        .setTitle("TeamGoGoal")//設定視窗標題
                        .setIcon(R.mipmap.ic_launcher)//設定對話視窗圖示
                        .setMessage(cmdContext)//設定顯示的文字
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
                                        new DeleteRequest().execute(rid, tid);
                                        socketTrans.setParams("addParticipator", tid, LoginActivity.getUser().account);
                                        socketTrans.send();
                                        list.remove(position);
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                RequestActivity.request_listAdapter.notifyDataSetChanged();
                                            }
                                        });
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
            db.deleteRegisterRequest(rid);
            return null;
        }
    }

    private void request_delete(final String originator, final String cmdContext, final String tid, View convertView, final int position) {
        holder.request_context.setText(cmdContext);

        convertView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //產生視窗物件
                final String id = list.get(position).get("rid");
                new AlertDialog.Builder(context)
                        .setTitle("TeamGoGoal")//設定視窗標題
                        .setIcon(R.mipmap.ic_launcher)//設定對話視窗圖示
                        .setMessage(cmdContext)//設定顯示的文字
                        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String rid = id;
                                new DeleteRequest().execute(rid);
                                list.remove(position);
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        RequestActivity.request_listAdapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        })//設定結束的子視窗
                        .show();//呈現對話視窗
            }
        });
    }

    private void request_cheer(final String originator, final String cmdContext, View convertView, final int position) {
        holder.request_context.setText(originator + "寄給你一則訊息");

        convertView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //產生視窗物件
                final String id = list.get(position).get("rid");
                new AlertDialog.Builder(context)
                        .setTitle("TeamGoGoal")//設定視窗標題
                        .setIcon(R.mipmap.ic_launcher)//設定對話視窗圖示
                        .setMessage("來自" + originator + "的訊息\n" + cmdContext)//設定顯示的文字
                        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String rid = id;
                                new DeleteRequest().execute(rid);
                                list.remove(position);
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        RequestActivity.request_listAdapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        })//設定結束的子視窗
                        .show();//呈現對話視窗
            }
        });
    }

    private void request_getCheer(final String originator, final String cmdContext, final String tid, View convertView, final int position) {



        holder.request_context.setText(cmdContext);

        convertView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                View dialog_view;

                dialog_view = LayoutInflater.from(context).inflate(R.layout.input_message, null);

                final EditText msg_context = dialog_view.findViewById(R.id.msg_context);
                Button submitCheer = dialog_view.findViewById(R.id.submitCheer);

                submitCheer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            final String id = list.get(position).get("rid");
                            String msgStr = msg_context.getText().toString();
                            socketTrans.setParams("register_cheer", LoginActivity.user.account, originator, msgStr, tid);
                            socketTrans.send();
                            dialog.dismiss();
                            list.remove(position);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    RequestActivity.request_listAdapter.notifyDataSetChanged();
                                }
                            });
                            new DeleteRequest().execute(id);
                        } catch (Exception e) {
                            Log.v("jim_cheerSubmit", e.toString());
                        }
                    }
                });

                dialog = new AlertDialog.Builder(context, R.style.hitStyle).setView(dialog_view).create();
                dialog.show();
            }
        });
    }

    private void request_update(final String originator, final String cmdContext, final String tid, View convertView, final int position) {
        holder.request_context.setText(originator + "已更新「" + cmdContext + "」目標的資訊");
        convertView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                final String id = list.get(position).get("rid");
                new AlertDialog.Builder(context)
                        .setTitle("TeamGoGoal")//設定視窗標題
                        .setIcon(R.mipmap.ic_launcher)//設定對話視窗圖示
                        .setMessage(originator + "已更新「" + cmdContext + "」目標的資訊")//設定顯示的文字
                        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String rid = id;
                                new DeleteRequest().execute(rid, cmdContext);
                                list.remove(position);
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        RequestActivity.request_listAdapter.notifyDataSetChanged();
                                    }
                                });
                                Intent i = new Intent(context, TargetEventActivity.class);

                                i.putExtra("cmd", "loading");
                                i.putExtra("tid", tid);
                                context.startActivity(i);
                            }
                        })//設定結束的子視窗
                        .show();//呈現對話視窗
            }
        });


    }


    static class ViewHolder {
        ImageView personal_photo;
        TextView request_context;
    }

}