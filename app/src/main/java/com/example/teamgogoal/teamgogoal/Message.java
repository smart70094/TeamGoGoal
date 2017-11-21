package com.example.teamgogoal.teamgogoal;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ToggleButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Message extends AppCompatActivity {
    final String localhost = LoginActivity.getLocalHost();
    ListView msg_listview;
    List<HashMap<String, String>> msg_list = new ArrayList<>();
    Message_ListAdapter msg_listadt;
    String currTid = "";
    LoginActivity.User user = LoginActivity.getUser();
    SocketTrans socketTrans = LoginActivity.socketTrans;
    AlertDialog dialog, hit_dialog;
    String send_type_cmd;
    HashMap<String, String> originator_uid;
    HashMap<String, Drawable> originator_photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        Bundle bundle = getIntent().getExtras();
        currTid = bundle.getString("tid");

        String phpurl = localhost + "searchMessage.php?uid=" + LoginActivity.user.uid + "&tid=" + currTid;
        new TransTask().execute(phpurl);


        phpurl = localhost + "searchUid.php?uid=" + LoginActivity.user.uid + "&tid=" + currTid;
        new UidTransTask().execute(phpurl);
    }


    private class TransTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            StringBuilder sb = new StringBuilder();
            try {
                URL url = new URL(params[0]);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(url.openStream()));
                String line = in.readLine();
                while (line != null) {
                    Log.d("HTTP", line);
                    sb.append(line);
                    line = in.readLine();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return sb.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("JSON", s);
            parseJSON(s);
        }

        private void parseJSON(String s) {
            try {
                JSONArray array = new JSONArray(s);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    HashMap<String, String> hashmap = new HashMap<>();
                    hashmap.put("context", obj.getString("context"));
                    hashmap.put("originator", obj.getString("originator"));
                    msg_list.add(hashmap);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private class UidTransTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            StringBuilder sb = new StringBuilder();
            try {
                URL url = new URL(params[0]);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(url.openStream()));
                String line = in.readLine();
                while (line != null) {
                    Log.d("HTTP", line);
                    sb.append(line);
                    line = in.readLine();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return sb.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("JSON", s);
            parseJSON(s);
            try {
                searhPersonalPhoto();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void parseJSON(String s) {
            originator_uid = new HashMap<>();
            try {
                JSONArray array = new JSONArray(s);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    originator_uid.put(obj.getString("originator"), obj.getString("uid"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void searhPersonalPhoto() throws ExecutionException, InterruptedException {
        originator_photo = new HashMap<>();
        for (Map.Entry<String, String> entry : originator_uid.entrySet()) {
            String imageUrl = localhost + "profilepicture/" + entry.getValue();
            Bitmap result = new PhotoTransTask().execute(imageUrl, entry.getKey()).get();
            originator_photo.put(entry.getKey(), toCircleImage(result));
        }
        initView();
    }

    private class PhotoTransTask extends AsyncTask<String, Void, Bitmap> {
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
    }

    protected Drawable toCircleImage(Bitmap bitmap) {

        //原图宽度
        int bitmapWidth = bitmap.getWidth();
        //原图高度
        int bitmapHeight = bitmap.getHeight();

        //转换为正方形后的宽高
        int bitmapSquareWidth = Math.min(bitmapWidth,bitmapHeight);

        //最终图像的宽高
        int newBitmapSquareWidth = bitmapSquareWidth;

        Bitmap roundedBitmap = Bitmap.createBitmap(newBitmapSquareWidth,newBitmapSquareWidth,Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(roundedBitmap);
        int x = bitmapSquareWidth - bitmapWidth;
        int y = bitmapSquareWidth - bitmapHeight;

        //裁剪后图像,注意X,Y要除以2 来进行一个中心裁剪
        canvas.drawBitmap(bitmap, x/2, y/2, null);

        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(),roundedBitmap);
        roundedBitmapDrawable.setCircular(true);
        return roundedBitmapDrawable;

    }

    private void initView() {
        msg_listview = (ListView) findViewById(R.id.msg_listview);
        msg_listadt = new Message_ListAdapter(this, originator_photo);
        msg_listadt.setData(msg_list);

        msg_listview.setAdapter(msg_listadt);
    }

    //------返回鍵------//
    public void cancel(View view) {
        finish();
    }

    //------傳送新訊息------//
    public void sendMessage(View view) {
        send_type_cmd = "";

        View dialog_view;

        dialog_view = LayoutInflater.from(this).inflate(R.layout.select_send_type, null);

        final ToggleButton type_noraml = dialog_view.findViewById(R.id.type_normal);
        final ToggleButton type_anonymous = dialog_view.findViewById(R.id.type_anonymous);
        final ToggleButton type_anonymousask = dialog_view.findViewById(R.id.type_anonymousask);
        Button typeConfirm = dialog_view.findViewById(R.id.typeConfirm);

        type_noraml.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    type_noraml.setTextColor(getResources().getColor(R.color.black));
                    type_anonymous.setChecked(false);
                    type_anonymousask.setChecked(false);
                } else {
                    type_noraml.setTextColor(getResources().getColor(R.color.basicColor));
                }
            }
        });

        type_anonymous.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    type_anonymous.setTextColor(getResources().getColor(R.color.black));
                    type_noraml.setChecked(false);
                    type_anonymousask.setChecked(false);
                } else {
                    type_anonymous.setTextColor(getResources().getColor(R.color.basicColor));
                }
            }
        });

        type_anonymousask.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    type_anonymousask.setTextColor(getResources().getColor(R.color.black));
                    type_noraml.setChecked(false);
                    type_anonymous.setChecked(false);
                } else {
                    type_anonymousask.setTextColor(getResources().getColor(R.color.basicColor));
                }
            }
        });

        typeConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type_noraml.isChecked()) {
                    send_type_cmd = "normal";
                } else if (type_anonymous.isChecked()) {
                    send_type_cmd = "anonymous";
                } else if (type_anonymousask.isChecked()) {
                    send_type_cmd = "anonymousask";
                } else {
                    send_type_cmd = "";
                }


                if (send_type_cmd.equals("")) {
                    Hit hit = new Hit("1", Message.this);
                    hit.set_hitTitle("提示");
                    hit.set_hitContent("請選擇訊息的傳送方式");
                    hit.get_hitConfirm().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            hit_dialog.dismiss();
                        }
                    });
                    hit_dialog = new AlertDialog.Builder(Message.this).setView(hit.get_view()).create();
                    hit_dialog.show();
                } else {
                    dialog.dismiss();
                    inputMessage();
                }
            }
        });


        dialog = new AlertDialog.Builder(this).setView(dialog_view).create();
        dialog.show();
    }


    //------填寫鼓勵訊息------//
    private void inputMessage() {
        View dialog_view;

        dialog_view = LayoutInflater.from(this).inflate(R.layout.input_message, null);

        final EditText msg_context = dialog_view.findViewById(R.id.msg_context);
        Button submitCheer = dialog_view.findViewById(R.id.submitCheer);

        submitCheer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String msgStr = msg_context.getText().toString();
                    Integer key = Integer.parseInt(currTid.trim());
                    //TaskDB.TaskDetail td = taskMap.get(key);

                    //-------預設送給456------//
                    socketTrans.setParams("register_cheer", user.account, "456", msgStr);
                    socketTrans.send(socketTrans.getParams());
                    dialog.dismiss();
                } catch (Exception e) {
                    Log.v("jim_cheerSubmit", e.toString());
                }
            }
        });

        dialog = new AlertDialog.Builder(this).setView(dialog_view).create();
        dialog.show();


    }
}
