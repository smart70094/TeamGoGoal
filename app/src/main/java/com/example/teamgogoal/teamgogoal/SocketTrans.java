package com.example.teamgogoal.teamgogoal;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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

public class SocketTrans extends Activity{     //執行緒
    private static Socket clientSocket=null;        //客戶端的socket
    private OutputStream out;            //取得網路輸出串流
    private static BufferedReader br;            //取得網路輸入串流
    private static Map<String,Socket> map=new HashMap<String,Socket>();
    String cmd;
    String subject;
    String originator;
    String param;
    static String result=null;
    public static Context context;
    SocketTrans(Context context){
        this.context=context;
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
    protected  void connection() {
        // TODO Auto-generated method stub
                try{
                    String serverIp = LoginActivity.getIp();
                    int serverPort = 12345;
                    clientSocket = new Socket(serverIp, serverPort);
                    clientSocket.setKeepAlive(true);
                    br= new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    out = clientSocket.getOutputStream();
                    send(param);
                }catch(Exception e){
                    Log.v("jim_send",e.toString());
                }


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
                            s = br.readLine();
                            setResult(s);
                            Log.v("jim_receiver",s);


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
            return t_result;
        }catch(Exception e){
            Log.v("jim_getResult",e.toString());
        }
        return null;
    }

    protected void showNotification(String content){
        /*new Thread(new Runnable(){


            @Override
            public void run() {
                runOnUiThread(new Runnable(){
                    @Override
                    public void run() {

                    }
                });
            }
        });*/
       /* NotificationManager notificationManger = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent();
        //intent.setClass(MainActivity.this, HandleNotification.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //PendingIntent pendingIntent =
        //		PendingIntent.getActivity(this, NOTIFICATION_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new Notification.Builder(this)
                .setSmallIcon(android.R.drawable.sym_def_app_icon)
                .setContentTitle("Hi")
                .setContentText("Nice to meet you.")
                //.setContentIntent(pendingIntent)
                .setPriority(Notification.PRIORITY_MAX)
                .build(); // available from API level 11 and onwards
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notificationManger.notify(0, notification);*/
    }
}
