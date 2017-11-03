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
    public static final int NOTIF_ID = 56;
    long timestamp;
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
        String title=intent.getStringExtra("title");
        // Extract the receiver passed into the service
        ResultReceiver rec = intent.getParcelableExtra("receiver");
        // Sleep a bit first
        //sleep(3000);
        // Send result to activity
        //sendResultValue(rec, val);
        // Let's also create notification

        int version=android.os.Build.VERSION.SDK_INT;
        if(version>25)
            createNotification_v8(val);
        else
            createNotification_v7(val);

    }

    // Send result to activity using ResultReceiver
    private void sendResultValue(ResultReceiver rec, String val) {
        // To send a message to the Activity, create a pass a Bundle
        Bundle bundle = new Bundle();
        bundle.putString("resultValue", "My Result Value. You Passed in: " + val + " with timestamp: " + timestamp);
        // Here we call send passing a resultCode and the bundle of extras
        rec.send(Activity.RESULT_OK, bundle);
    }

    // Construct compatible notification
    private void createNotification_v8(String val) {
        // Construct pending intent to serve as action for notification item
        Intent intent = new Intent(this, TargetActivity.class);
        intent.putExtra("message", "Launched via notification with message: " + val + " and timestamp " + timestamp);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Create notification
        String longText = "Intent service has a new message with: " + val + " and a timestamp of: " + timestamp;
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
        mNotificationManager.notify(NOTIF_ID, builder.build());
    }
    private void createNotification_v7(String val) {
        int notificationId = 001;
        String id = "my_channel_01";
        Intent viewIntent = new Intent();
        viewIntent.putExtra("notificationID",123);
        PendingIntent viewPendingIntent =
                PendingIntent.getActivity(this, 0, viewIntent, 0);
        Notification.Builder notificationBuilder =
                new Notification.Builder(this)
                        .setSmallIcon(android.R.drawable.ic_notification_clear_all)
                        .setContentTitle("TeamGoGoal")
                        .setContentText(val)
                        .setPriority(Notification.PRIORITY_MAX)
                        .setContentIntent(viewPendingIntent);
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);
        notificationManager.notify(notificationId, notificationBuilder.build());
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}