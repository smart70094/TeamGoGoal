package com.example.teamgogoal.teamgogoal;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by hp on 2017/11/5.
 */

public class RegisterAlarmService extends IntentService {
    AlarmManager alarmManager;
    LoginActivity.User user=LoginActivity.getUser();
    public static HashMap<Integer,PendingIntent> alarmMap=new HashMap<Integer,PendingIntent>();
    public RegisterAlarmService() {
        super(".RegisterAlarmService");
    }

    protected void onHandleIntent(Intent intent) {
        //從intent取出data
        alarmManager = (AlarmManager) getSystemService(this.ALARM_SERVICE);
        String cmd=intent.getStringExtra("cmd");
        switch (cmd){
            case "loading":
                String state=intent.getStringExtra("state");
                loading(state);
                break;
            case "adding":
                String mid=intent.getStringExtra("mid");
                String taskName=intent.getStringExtra("missionName");
                String remindTime=intent.getStringExtra("remindTime");
                registerAlarm("adding",mid,taskName,remindTime);
                break;
        }

    }
    public void loading(String state){
        TaskDB db=new TaskDB(LoginActivity.getLocalHost());
        String data=db.readAlarmInfo(LoginActivity.getUser().account);
        Calendar mCal = Calendar.getInstance();
        java.util.Calendar c1=java.util.Calendar.getInstance();
        java.util.Calendar c2=java.util.Calendar.getInstance();

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        try {
            JSONArray array = new JSONArray(data);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                if(state.equals("N")){
                    String mid = obj.getString("mid");
                    String taskName = obj.getString("missionName").trim();
                    String remindTime = obj.getString("remindTime").trim();
                    registerAlarm("adding",mid,taskName,remindTime);
                }else{
                    String remindTime = obj.getString("remindTime").trim();
                    Date curDate  =new Date();
                    String curDateStr=sdf.format(curDate);

                    c1.setTime(sdf.parse(curDateStr));
                    c2.setTime(sdf.parse(remindTime));


                    if((c1. compareTo(c2))<0){
                        String mid = obj.getString("mid");
                        String taskName = obj.getString("missionName").trim();
                        registerAlarm("adding",mid,taskName,remindTime);
                    }
                }

            }
        } catch (Exception e) {
            Log.v("jim error in showTargetEvent：", e.toString());
        }
    }
    public void registerAlarm(String... dataList){
        String cmd=dataList[0];
        int mid=Integer.parseInt(dataList[1]);
        switch (cmd){
            case "adding":
                if(!alarmMap.containsKey(mid)){
                    //取出時間說定到calendar裡
                    /*String taskName = intent.getStringExtra("taskName");
                    String remindTime = intent.getStringExtra("remindTime");*/
                    String taskName = dataList[2];
                    String remindTime = dataList[3];
                    String timeArr[]=remindTime.trim().split(":");

                    int hour=Integer.parseInt(timeArr[0].trim());
                    int min=Integer.parseInt(timeArr[1].trim());
                    Calendar calendar = Calendar.getInstance();
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
