package com.example.teamgogoal.teamgogoal;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by hp on 2017/9/7.
 */

public class AlarmReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        String taskName=intent.getStringExtra("taskName");
        String data="你今天完成了「"+taskName+"」了嘛？";
        Intent nofyIntent=new Intent(context,NofyService.class);
        nofyIntent.putExtra("foo",data);
        context.startService(nofyIntent);
    }
}
