package com.example.teamgogoal.teamgogoal;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hp on 2017/8/4.
 */

public  class SocketTrans {     //執行緒
    private static Socket clientSocket=null;        //客戶端的socket
    private OutputStream out;            //取得網路輸出串流
    private static BufferedReader br;            //取得網路輸入串流
    private static Map<String,Socket> map=new HashMap<String,Socket>();
    String cmd;
    String subject;
    String originator;
    String param;
    static String result=null;
    public Context context;
    public NotificationManager notificationManager;
    protected void setActivity(Context context){
        this.context=context;
    }
    protected void setNotification(NotificationManager notificationManager){
        this.notificationManager=notificationManager;
    }
    protected   void setParams(String cmd,String originator){
        this.cmd=cmd;
        this.originator=originator;
        param=cmd+","+originator+"\n";
    }
    protected void setParams(String cmd,String originaotr,String subject){
        this.cmd=cmd;
        this.originator=originaotr;
        this.subject=subject;
        param=cmd+","+originator+","+subject+"\n";
    }
    protected  void setParams(String cmd,String originator,String subject,String tid){
        this.cmd=cmd;
        this.originator=originator;
        this.subject=subject;
        param=cmd+","+originator+","+subject+","+tid+"\n";
    }
    protected  String getParams(){
        return param;
    }
    protected  void connection() throws Exception {
        String serverIp = LoginActivity.getIp();
        int serverPort = 12345;
        clientSocket = new Socket(serverIp, serverPort);
        clientSocket.setKeepAlive(true);
        br= new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = clientSocket.getOutputStream();
        send(param);
    }

    protected static void setResult(String s){
        result=s;
    }
    protected  void send(final String s){
        Thread t=new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    out = clientSocket.getOutputStream();
                    byte b[] = s.getBytes();
                    out.write(b);
                    out.flush();
                }catch(Exception e){
                    Log.v("jim_send",e.toString());
                }
            }
        });
        t.start();
    }
    protected void receiver() {
        Thread  t=new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                     String s=null;
                    while (clientSocket.isConnected()) {
                       if(br.ready()){
                            s = br.readLine().trim();
                            setResult(s);
                            if(!s.equals(""))
                                showNotification(s);
                       }
                    }
                }catch(Exception e){
                    Log.v("jim_receiver",e.toString());
                }
            }
        });
        t.start();
    }
    protected String getResult() {
        try{
            while(result==null){
                Thread.sleep(500);
            }
            String t_result=result;
            result=null;
            return t_result.trim();
        }catch(Exception e){
            Log.v("jim_getResult",e.toString());
        }
        return null;
    }
    public void showNotification(String contentText){
        NotificationChannel channelMsg = new NotificationChannel(
                "msg",
                "Channel msg",
                NotificationManager.IMPORTANCE_HIGH);
        channelMsg.setDescription("socketMsg");
        channelMsg.enableLights(true);
        channelMsg.enableVibration(true);
        //NotificationManager notificationManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channelMsg);

        Notification.Builder builder =
                new Notification.Builder(context)
                        .setSmallIcon(android.R.drawable.ic_notification_clear_all)
                        .setContentTitle("TeamGoGoal")
                        .setContentText(contentText)
                        .setChannelId("msg");
        notificationManager.notify(1, builder.build());
    }
    public void close(){
        try {
            br.close();
            out.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
