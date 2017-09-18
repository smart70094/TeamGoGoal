package com.example.teamgogoal.teamgogoal;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by hp on 2017/9/7.
 */

public class NofyService extends Service {
    private final String TAG = "NofyService";
    private Intent in;
    private int flags;
    private int startId;


    public IBinder onBind(Intent intent) {        return null;    }

    @Override
    public void onCreate() {
        Toast.makeText(getApplication(), "service", Toast.LENGTH_SHORT).show();
        Log.v(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.in = intent;
        this.flags = flags;
        this.startId = startId;
        new Thread(checkTime).start();
        Toast.makeText(getApplication(), "service",
                Toast.LENGTH_SHORT).show();
        Log.v(TAG, "onStart");
        return START_STICKY;
    }

    //Service 的功能，创建通知栏通知
    Runnable checkTime = new Runnable() {
        @Override
        public void run() {
            NotificationChannel channelMsg = new NotificationChannel(
                    "msg",
                    "Channel msg",
                    NotificationManager.IMPORTANCE_HIGH);
            channelMsg.setDescription("socketMsg");
            channelMsg.enableLights(true);
            channelMsg.enableVibration(true);
            NotificationManager notificationManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channelMsg);

            Notification.Builder builder =
                    new Notification.Builder(getApplicationContext())
                            .setSmallIcon(android.R.drawable.ic_notification_clear_all)
                            .setContentTitle("TeamGoGoal")
                            .setContentText("sadfadf")
                            .setChannelId("msg");
            notificationManager.notify(1, builder.build());
        }
    };

    @Override
    public void onDestroy() {
        this.onStartCommand(in, flags, startId);
    }
}


