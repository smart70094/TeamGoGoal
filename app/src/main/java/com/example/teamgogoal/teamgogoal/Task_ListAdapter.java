package com.example.teamgogoal.teamgogoal;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Task_ListAdapter extends BaseAdapter {
    String localhost = LoginActivity.getLocalHost();
    Context context;
    ViewHolder holder;
    private LayoutInflater myInflater;
    List<HashMap<String, String>> list = new ArrayList<>();

    public Task_ListAdapter(Context context) {
        myInflater = LayoutInflater.from(context);
        this.context = context;
    }

    public void setData(List<HashMap<String, String>> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {

            convertView = myInflater.inflate(R.layout.task_list, null);
            holder = new ViewHolder();
            holder.personal_photo = (ImageView) convertView.findViewById(R.id.personal_photo);
            holder.missionName = (TextView) convertView.findViewById(R.id.missionName);
            holder.linearlayout_bg = (LinearLayout) convertView.findViewById(R.id.LinearLayout_bg);
            holder.complete_btn = (ImageButton) convertView.findViewById(R.id.complete_btn);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String auth = list.get(position).get("auth").trim();
        String authID = list.get(position).get("authID").trim();
        String state = list.get(position).get("state").trim();

        //set mission Name
        String imageUrl = localhost + "profilepicture/" + authID;
        new TransTask().execute(imageUrl);


        // set mission Name
        holder.missionName.setText(list.get(position).get("missionName"));

        //set self mission or other misson
        if(auth.equals(LoginActivity.user.account)){
            holder.linearlayout_bg.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_record_msg));

            if(state.equals("Y")){
                holder.complete_btn.setVisibility(View.INVISIBLE);
            }else {
                holder.complete_btn.setVisibility(View.VISIBLE);
            }
        }else{
            holder.linearlayout_bg.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_task_other_complete));
            holder.complete_btn.setVisibility(View.INVISIBLE);
        }



        holder.missionName.setText(list.get(position).get("missionName"));

        //圖片
        //holder.dateAndTime.setText(list.get(position).get("date"));


        return convertView;
    }

    private class TransTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);
                return bitmap;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            holder.personal_photo.setImageDrawable(toCircleImage(result));
            super.onPostExecute(result);
        }
    }

    protected Drawable toCircleImage(Bitmap bp) {
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), bp);
        roundedBitmapDrawable.setCircular(true);
        return roundedBitmapDrawable;
    }

    static class ViewHolder {
        ImageView personal_photo;
        TextView missionName;
        LinearLayout linearlayout_bg;
        ImageButton complete_btn;
    }

}