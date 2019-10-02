package com.teamgogoal.utils;

import android.content.Context;
import android.widget.Toast;

import com.example.teamgogoal.teamgogoal.LoginActivity;

public class ToastUtils {

    public static <T> void showShortMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
