package com.example.teamgogoal.teamgogoal;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by hp on 2017/8/4.
 */

public class SocketTrans {     //執行緒
    private static Socket clientSocket = null;        //客戶端的socket
    private OutputStream out;            //取得網路輸出串流
    private static BufferedReader br;            //取得網路輸入串流
    private static ArrayList<String> waitDealData = new ArrayList<String>();
    String param;
    public Context context;
    public NotificationManager notificationManager;

    protected void setActivity(Context context) {
        this.context = context;
    }

    protected void setNotification(NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }

    public void setParams(String... parms) {
        param = "";
        for (String s : parms) param += s + ",";
        param = param.substring(0, param.length() - 1) + "\n";
    }

    protected String getParams() {
        return param;
    }

    protected void connection() throws Exception {
        String serverIp = LoginActivity.getIp();
        int serverPort = 12345;
        clientSocket = new Socket(serverIp, serverPort);
        clientSocket.setKeepAlive(true);
        br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = clientSocket.getOutputStream();
        send(param);
    }

    protected void send(final String s) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    out = clientSocket.getOutputStream();
                    byte b[] = s.getBytes();
                    out.write(b);
                    out.flush();
                } catch (Exception e) {
                    Log.v("jim_send", e.toString());
                }
            }
        });
        t.start();
    }

    protected void send() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    out = clientSocket.getOutputStream();
                    byte b[] = param.getBytes();
                    out.write(b);
                    out.flush();
                } catch (Exception e) {
                    Log.v("jim_send", e.toString());
                }
            }
        });
        t.start();
    }

    protected void receiver() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String str = null;
                    String strArr[] = null;
                    while (clientSocket.isConnected()) {
                        if (br.ready()) {
                            str = br.readLine().trim();
                            strArr = str.split(",");
                            String cmd = strArr[0];
                            switch (cmd) {
                                case "message":
                                    message(strArr[1], strArr[2]);
                                    break;
                                case "updateTarget":
                                    updateTargetMessage(strArr[1], strArr[2]);
                                    break;
                                case "addAccount":
                                    createDefaultPhoto(strArr[1]);
                                    break;
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.v("jim_receiver", e.toString());
                }
            }
        });
        t.start();
    }

    public void message(String... dataList) {
        String subject = dataList[0];
        String text = dataList[1];
        Intent intent = new Intent(context, NofyService.class);
        intent.putExtra("foo", text);
        intent.putExtra("subject", subject);
        context.startService(intent);
    }

    public void updateTargetMessage(String... dataList) {
        String tid = dataList[0];
        String text = dataList[1];
        Intent intent = new Intent(context, NofyService.class);
        intent.putExtra("foo", text);
        intent.putExtra("subject", "targetEvent");
        intent.putExtra("tid", tid);
        context.startService(intent);
    }

    public void createDefaultPhoto(final String uid) {
        String phpurl = LoginActivity.getLocalHost() + "createPersonPhoto.php?&uid=" + uid;
        new TransTask_createPhoto().execute(phpurl);
    }

    private class TransTask_createPhoto extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            StringBuilder sb = new StringBuilder();
            try {
                URL url = new URL(params[0]);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(url.openStream()));
                String line = in.readLine();
                while (line != null) {
                    Log.d("HTTP", line);
                    sb.append(line);
                    line = in.readLine();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }


    }

    public void close() throws IOException {
        br.close();
        out.close();
        clientSocket.close();
    }
}
