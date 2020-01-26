package com.teamgogoal.utils;

import android.widget.EditText;

public class EditTextUtils {

    public static String getText(EditText editText) {
        return editText.getText().toString();
    }

    public boolean isNullOrEmpty(EditText editText) {
        return StringUtils.isNullOrEmpty(EditTextUtils.getText(editText));
    }
}
