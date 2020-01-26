package com.teamgogoal.utils;

import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.EditText;

import java.time.format.DateTimeFormatter;
import java.util.Calendar;

public class TimeUtils {

    public static void showTimePickerDialog(Context context, EditText editText) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);

        new TimePickerDialog(context, (view, hourOfDay, minute)-> {
            String timeMoment = String.format("%02d:%02d", hourOfDay, minute);
            editText.setText(timeMoment);
            Log.v("jim_alarm", hourOfDay + ":" + minute);

        }, hour, min, false).show();
    }


    public static String buildTimeFormat(String time) {
        if(time.length() != 4)
            return "";

        String hh = time.substring(0,2);
        String mm = time.substring(2,4);

        return String.format("%s:%s", hh, mm);
    }

    public static void main(String[] args) {
        String time = "1200";
        System.out.println(buildTimeFormat(time));
    }
}
