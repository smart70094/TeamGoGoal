package com.teamgogoal.websocket;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;

import com.teamgogoal.utils.ToastUtils;
import com.teamgogoal.view.activity.LoginActivity;
import com.teamgogoal.view.activity.R;
import com.teamgogoal.view.activity.TargetActivity;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import tech.gusavila92.websocketclient.WebSocketClient;

public class TggWebsocket extends WebSocketClient{

    private Map reqAndResMap = new ConcurrentHashMap<String, String>();

    private Context context;

    private NotificationManager notificationManager;

    public TggWebsocket(Context context, NotificationManager notificationManager, String url) throws URISyntaxException {
        super(new URI(url));
        this.context = context;
        this.notificationManager = notificationManager;
    }

    @Override
    public void onOpen() {
        System.out.println("opening!");
    }

    @Override
    public void onTextReceived(String message) {
        new Handler(Looper.getMainLooper()).post(()-> {
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                            .setContentTitle("測試訊息")
                            .setContentText(message);
            int NOTIFICATION_ID = 12345;

            Intent targetIntent = new Intent(context, TargetActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(contentIntent);
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        });
    }

    @Override
    public void onBinaryReceived(byte[] data) {
        System.out.println("on binary received");
    }

    @Override
    public void onPingReceived(byte[] data) {
        System.out.println("on ping receiverd");
    }

    @Override
    public void onPongReceived(byte[] data) {
        System.out.println("on pong receiverd");
    }

    @Override
    public void onException(Exception e) {
        System.out.println("exception:" + e.toString());
    }

    @Override
    public void onCloseReceived() {
        System.out.println("close");
    }

    public static void main(String[] args) throws URISyntaxException {
        TggWebsocket tggWebsocket = new TggWebsocket(null, null, "wss://teamgogoal.herokuapp.com/teamgogoal/123456678");
        tggWebsocket.setConnectTimeout(10000);
        tggWebsocket.setReadTimeout(60000);
        tggWebsocket.enableAutomaticReconnection(5000);
        tggWebsocket.connect();

        String requestString =
                "{\n" +
                "\"handler\":\"sendMessage\",\n" +
                "\"abc\":\"123\"\n" +
                "}";

        tggWebsocket.send(requestString);


    }
}
