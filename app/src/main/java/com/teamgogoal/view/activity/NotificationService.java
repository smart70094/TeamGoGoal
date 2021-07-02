package com.teamgogoal.view.activity;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.teamgogoal.utils.ToastUtils;
import com.teamgogoal.websocket.TggWebsocket;
import com.teamgogoal.websocket.TggWebsocketUtils;

import java.net.URISyntaxException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NotificationService extends Service {

    private ExecutorService executorService;

    private NotificationManager notificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        executorService = Executors.newSingleThreadExecutor();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            TggWebsocketUtils.createWebosocketConnection(getApplicationContext(), notificationManager);

            String requestString =
                    "{\n" +
                            "\"handler\":\"sendMessage\",\n" +
                            "\"abc\":\"123\"\n" +
                            "}";
            executorService.execute(()->{
                TggWebsocketUtils.send(requestString);
            });

        } catch (Exception e) {
            Log.e("NotificationService", e.toString());
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
