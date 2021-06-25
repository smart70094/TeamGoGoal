package com.teamgogoal.utils;

import android.app.ProgressDialog;
import android.content.Context;

public class ProgressDialogUtils {

    public static ProgressDialog create(Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("處理中，請稍等");
        return progressDialog;
    }
}
