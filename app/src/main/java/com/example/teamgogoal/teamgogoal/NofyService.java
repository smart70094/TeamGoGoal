package com.example.teamgogoal.teamgogoal;

import android.app.Activity;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.v4.app.NotificationManagerCompat;


public class NofyService extends IntentService {
    public static  int NOTIF_ID = 0;
    public MySimpleReceiver receiverForSimple;

    // Must create a default constructor
    public NofyService() {
        super("simple-service");
    }

    // This describes what will happen when service is triggered
    @Override
    protected void onHandleIntent(Intent intent) {
        //timestamp =  System.currentTimeMillis();
        // Extract additional values from the bundle
        String val = intent.getStringExtra("foo");
        String subject=intent.getStringExtra("subject");
        Intent i=null;
        switch (subject){
            case "target":
                i=new Intent(this,TargetActivity.class);
                break;
            case "targetEvent":
                i=new Intent(this,TargetEventActivity.class);
                String tid=intent.getStringExtra("tid");
                i.putExtra("cmd","loading");
                i.putExtra("tid",tid);
                break;
            case "task":
                i=new Intent(this,TaskActivity.class);
                break;
            case "request":
                i=new Intent(this,RequestActivity.class);
                break;
        }

        int version=android.os.Build.VERSION.SDK_INT;
        if(i!=null){
            if(version>25)
                createNotification_v8(val,i);
            else
                createNotification_v7(val,i);
        }

    }

    // Construct compatible notification
    private void createNotification_v8(String val,Intent intent) {
        // Construct pending intent to serve as action for notification item


        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channelMsg = new NotificationChannel(
                "msg",
                "Channel msg",
                NotificationManager.IMPORTANCE_HIGH);
        channelMsg.setDescription("socketMsg");
        channelMsg.enableLights(true);
        channelMsg.enableVibration(true);
        mNotificationManager.createNotificationChannel(channelMsg);


        Notification.Builder builder =
                new Notification.Builder(this)
                        .setSmallIcon(android.R.drawable.ic_notification_clear_all)
                        .setContentTitle("TeamGoGoal")
                        .setContentText(val)
                        .setContentIntent(pIntent)
                        .setChannelId("msg");


        // mId allows you to update the notification later on.
        mNotificationManager.notify(NOTIF_ID++, builder.build());
    }
    private void createNotification_v7(String val,Intent intent) {
        PendingIntent viewPendingIntent =
                PendingIntent.getActivity(this, 0, intent, 0);
        Notification.Builder notificationBuilder =
                new Notification.Builder(this)
                        .setSmallIcon(android.R.drawable.ic_notification_clear_all)
                        .setContentTitle("TeamGoGoal")
                        .setContentText(val)
                        .setPriority(Notification.PRIORITY_MAX)
                        .setContentIntent(viewPendingIntent);
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);
        notificationManager.notify(NOTIF_ID++, notificationBuilder.build());
    }
}