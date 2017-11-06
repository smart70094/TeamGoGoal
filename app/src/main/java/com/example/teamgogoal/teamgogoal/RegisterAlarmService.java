package com.example.teamgogoal.teamgogoal;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.icu.util.Calendar;

import java.util.HashMap;

/**
 * Created by hp on 2017/11/5.
 */

public class RegisterAlarmService extends IntentService {
    AlarmManager alarmManager;
    public static HashMap<Integer,PendingIntent> alarmMap=new HashMap<Integer,PendingIntent>();
    public RegisterAlarmService() {
        super(".RegisterAlarmService");

    }

    protected void onHandleIntent(Intent intent) {
        //從intent取出data
        alarmManager = (AlarmManager) getSystemService(this.ALARM_SERVICE);
        String cmd=intent.getStringExtra("cmd");
        int mid = Integer.parseInt(intent.getStringExtra("mid"));
        switch (cmd){
            case "adding":
                if(!alarmMap.containsKey(mid)){
                    //取出時間說定到calendar裡
                    String taskName = intent.getStringExtra("taskName");
                    String remindTime = intent.getStringExtra("remindTime");
                    String timeArr[]=remindTime.trim().split(":");
                    int hour=Integer.parseInt(timeArr[0].trim());
                    int min=Integer.parseInt(timeArr[1].trim());
                    android.icu.util.Calendar calendar = android.icu.util.Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, hour);
                    calendar.set(Calendar.MINUTE, min);

                    Intent i = new Intent(getApplicationContext(), AlarmReceiver.class);
                    i.putExtra("taskName",taskName);

                    PendingIntent pending = PendingIntent.getBroadcast(this, mid, i, PendingIntent.FLAG_UPDATE_CURRENT);

                    //alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pending);
                    alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                            AlarmManager.INTERVAL_DAY, pending);
                    alarmMap.put(mid,pending);
                }
                break;
            case "cancel":
                if(alarmMap.containsKey(mid)){
                    PendingIntent pending =alarmMap.get(mid);
                    alarmManager.cancel(pending);
                    alarmMap.remove(pending);
                }
                break;
        }
    }
}
