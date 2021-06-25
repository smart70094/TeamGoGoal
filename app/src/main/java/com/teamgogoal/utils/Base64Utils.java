package com.teamgogoal.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class Base64Utils {

    public static String encodeImage(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();

        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

        return GzipUtils.compress(encoded);
    }

    public static byte[] decodeImageToByteArray(String base64String) {
        return  Base64.decode(GzipUtils.decompress(base64String), Base64.DEFAULT);
    }

    public static Bitmap decodeImageToBitmap(String base64String) {
        byte[] imageByteArray = decodeImageToByteArray(base64String);
        return BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
    }
}
