package com.teamgogoal.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.teamgogoal.view.activity.R;

public class AlertDialogUtils {

    public static AlertDialog buildComfirmAlertDialog(Context context, String title, String content, DialogInterface.OnClickListener comfirmEvent, DialogInterface.OnClickListener cancelEvent) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);

        return dialog.setTitle(title)
                .setMessage(content)
                .setPositiveButton("確定", comfirmEvent)
                .setNegativeButton("取消", cancelEvent)
                .show();
    }
}
