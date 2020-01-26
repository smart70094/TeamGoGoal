package com.teamgogoal.utils;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.EditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
     private static SimpleDateFormat yyyymmddFormat = new SimpleDateFormat( "yyyyMMdd" );
    private static SimpleDateFormat dotFormat = new SimpleDateFormat( "yyyy.MM.dd" );

    public static String addDotDate(String yyyymmdd) {
        Date newDate= null;
        try {
            newDate = yyyymmddFormat.parse(yyyymmdd);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dotFormat.format(newDate);
    }

    public static void showDatePickerDialog(Context context, EditText editText) {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        new DatePickerDialog(context, (view, year, month, day)-> {
            String format = String.format("%d-%02d-%02d", year, month + 1, day);
            editText.setText(format);
        }, mYear, mMonth, mDay).show();
    }

    public static void main(String[] args) {
        String startDate = "20191001";
        String endDate = "20191031";

        System.out.println(addDotDate(startDate));

    }
}
