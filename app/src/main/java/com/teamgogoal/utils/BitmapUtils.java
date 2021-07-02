package com.teamgogoal.utils;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

public class BitmapUtils {

    public static byte[] toBytes(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        bitmap.recycle();
        return byteArray;
    }
}
