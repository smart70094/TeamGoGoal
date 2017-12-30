package com.example.teamgogoal.teamgogoal;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

/**
 * Created by Gary on 2017/12/28.
 */

public class Photo_Handle {

    public static HashMap<String, Drawable> user_photo;


    private static String imageUrl = LoginActivity.getLocalHost() + "profilepicture/";
    private static Context context;

    public Photo_Handle(Context context) {
        this.context = context;
        user_photo = new HashMap<>();
    }

    public Drawable getPhotoByID(String authID) throws ExecutionException, InterruptedException {
        if (user_photo.get(authID) != null) {
            return user_photo.get(authID);
        } else {
            new PhotoTransTask().execute(imageUrl + authID, authID);
            return null;
        }
    }
    /*public Drawable getPhotoByAccount(String account) throws ExecutionException, InterruptedException {
        //String authID =


        if (user_photo.get(authID) != null) {
            return user_photo.get(authID);
        } else {
            new PhotoTransTask().execute(imageUrl + authID, authID);
            return null;
        }
    }*/

    private class PhotoTransTask extends AsyncTask<String, Void, Holder> {

        @Override
        protected Holder doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);
                return new Holder(params[1], bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Holder holder) {
            super.onPostExecute(holder);
            user_photo.put(holder.getaccount(), holder.getCircleBmap());
        }
    }

    protected static Drawable toCircleImage(Bitmap bitmap) {

        //原图宽度
        int bitmapWidth = bitmap.getWidth();
        //原图高度
        int bitmapHeight = bitmap.getHeight();

        //转换为正方形后的宽高
        int bitmapSquareWidth = Math.min(bitmapWidth, bitmapHeight);

        //最终图像的宽高
        int newBitmapSquareWidth = bitmapSquareWidth;

        Bitmap roundedBitmap = Bitmap.createBitmap(newBitmapSquareWidth, newBitmapSquareWidth, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(roundedBitmap);
        int x = bitmapSquareWidth - bitmapWidth;
        int y = bitmapSquareWidth - bitmapHeight;

        //裁剪后图像,注意X,Y要除以2 来进行一个中心裁剪
        canvas.drawBitmap(bitmap, x / 2, y / 2, null);

        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), roundedBitmap);
        roundedBitmapDrawable.setCircular(true);
        return roundedBitmapDrawable;

    }

    class Holder {
        String account;
        Bitmap bmap;

        public Holder(String account, Bitmap bmap) {
            this.account = account;
            this.bmap = bmap;
        }

        public String getaccount() {
            return account;
        }

        public Bitmap getbmap() {
            return bmap;
        }

        public Drawable getCircleBmap() {
            return toCircleImage(bmap);
        }

    }

}
