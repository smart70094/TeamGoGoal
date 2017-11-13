package com.example.teamgogoal.teamgogoal;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.annotation.Target;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hp on 2017/8/4.
 */

public  class SocketTrans {     //執行緒
    private static Socket clientSocket=null;        //客戶端的socket
    private OutputStream out;            //取得網路輸出串流
    private static BufferedReader br;            //取得網路輸入串流
    private static ArrayList<String> waitDealData=new ArrayList<String>();
    /*String cmd;
    String subject;
    String originator;*/
    String param;
    //static String result=null;
    public Context context;
    public NotificationManager notificationManager;
    public MySimpleReceiver receiverForSimple;
    protected void setActivity(Context context){
        this.context=context;
    }
    protected void setNotification(NotificationManager notificationManager){
        this.notificationManager=notificationManager;
    }

    public void setParams(String... parms){
        param="";
        for(String s:parms)  param+=s+",";
        param=param.substring(0,param.length()-1)+"\n";
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
    protected  void send(){
        Thread t=new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    out = clientSocket.getOutputStream();
                    byte b[] = param.getBytes();
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
                           waitDealData.add(s);
                            if(!s.equals("")) {
                                Intent i = new Intent(context,NofyService.class);
                                // Add extras to the bundle
                                i.putExtra("foo", s);
                                i.putExtra("receiver", receiverForSimple);
                                // Start the service

                                context.startService(i);
                            }
                       }
                    }
                }catch(Exception e){
                    Log.v("jim_receiver",e.toString());
                }
            }
        });
        t.start();
    }

    public void close() throws IOException {
        br.close();
        out.close();
        clientSocket.close();
    }

    protected String getResult() {
        try{
            String result=null;
            while(waitDealData.get(0)==null || waitDealData.get(0).equals("")){
                result=waitDealData.get(0);
                if(result.equals("") && waitDealData.size()>0) waitDealData.remove(0);
                Thread.sleep(500);
             }
            result=waitDealData.get(0);
            waitDealData.remove(0);
            return result.trim();
        }catch(Exception e){
            Log.v("jim_getResult",e.toString());
        }
        return null;
    }
}
