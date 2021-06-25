package com.teamgogoal.utils;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.teamgogoal.view.activity.R;

public class DialogUtils {

    public static Dialog createDialog(AppCompatActivity appCompatActivity, View view, int themeResId) {
        Dialog dialog = new AlertDialog.Builder(appCompatActivity, themeResId).setView(view).create();
        dialog.show();
        adjustLayoutParams(appCompatActivity, dialog);

        return dialog;
    }

    public static void showHit(AppCompatActivity appCompatActivity, String title, String content) {
        View hitView = LayoutInflater.from(appCompatActivity).inflate(R.layout.hit_dialog, null);
        final Dialog hitDialog = createDialog(appCompatActivity, hitView, R.style.hitStyle);

        TextView hitTitle = hitView.findViewById(R.id.hitTitle);
        TextView hitContent = hitView.findViewById(R.id.hitContent);
        TextView hitComfirm = hitView.findViewById(R.id.hitComfirm);

        hitTitle.setText(title);
        hitContent.setText(content);

        hitComfirm.setOnClickListener((view) -> hitDialog.dismiss());

        hitDialog.show();
    }

    public static void adjustLayoutParams(AppCompatActivity appCompatActivity, Dialog dialog) {
        Window dialogWindow = dialog.getWindow();
        WindowManager m = appCompatActivity.getWindowManager();
        Display display = m.getDefaultDisplay(); // 获取屏幕宽、高度
        WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        layoutParams.height = (int) (display.getHeight() * 0.7); // 高度设置为屏幕的0.6，根据实际情况调整
        layoutParams.width = (int) (display.getWidth() * 0.8); // 宽度设置为屏幕的0.65，根据实际情况调整
        dialogWindow.setAttributes(layoutParams);
    }
}
