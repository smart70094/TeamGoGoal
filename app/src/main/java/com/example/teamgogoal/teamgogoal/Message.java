package com.example.teamgogoal.teamgogoal;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ToggleButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    HashMap<String, Drawable> member_photo;
    List<HashMap<String, Object>> unpick_list = new ArrayList<>();
    List<HashMap<String, Object>> pick_list = new ArrayList<>();
    Member_ListAdapter unpick_adapter, pick_adapter;
    Hit hit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        Bundle bundle = getIntent().getExtras();
        currTid = bundle.getString("tid");

        String phpurl = localhost + "searchMessage.php?uid=" + LoginActivity.user.uid + "&tid=" + currTid;
        new TransTask().execute(phpurl);
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
            initView();
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

    private void initView() {
        msg_listview = (ListView) findViewById(R.id.msg_listview);
        msg_listadt = new Message_ListAdapter(this);
        msg_listadt.setData(msg_list);

        msg_listview.setAdapter(msg_listadt);
    }

    //------返回鍵------//
    public void cancel(View view) {
        finish();
    }

    //------傳送新訊息------//
    public void sendMessage(View view) {
        unpick_list.clear();

        View dialog_view;

        dialog_view = LayoutInflater.from(this).inflate(R.layout.select_send_member, null);

        GridView unpickMember = dialog_view.findViewById(R.id.unpickMember);
        GridView pickMember = dialog_view.findViewById(R.id.pickMember);
        Button memberConfirm = dialog_view.findViewById(R.id.memberConfirm);

        member_photo = TaskActivity.member_photo;
        for (Map.Entry<String, Drawable> entry : member_photo.entrySet()) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("account", entry.getKey());
            map.put("personal_photo", entry.getValue());
            unpick_list.add(map);
        }

        unpickMember.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                pick_list.add(unpick_list.get(position));
                unpick_list.remove(position);

                unpick_adapter.notifyDataSetChanged();
                pick_adapter.notifyDataSetChanged();
            }
        });

        pickMember.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                unpick_list.add(pick_list.get(position));
                pick_list.remove(position);

                unpick_adapter.notifyDataSetChanged();
                pick_adapter.notifyDataSetChanged();
            }
        });

        memberConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pick_list.isEmpty()) {
                    hit = new Hit("1", Message.this);
                    hit.set_hitTitle("提示");
                    hit.set_hitContent("請選擇要傳送訊息的對象");
                    hit.get_hitConfirm().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            hit_dialog.dismiss();
                        }
                    });
                    hit_dialog = new AlertDialog.Builder(Message.this,R.style.hitStyle).setView(hit.get_view()).create();
                    hit_dialog.show();
                } else {
                    dialog.dismiss();
                    selectType();
                }
            }
        });

        unpick_adapter = new Member_ListAdapter(this);
        unpick_adapter.setData(unpick_list);
        unpickMember.setNumColumns(5);
        unpickMember.setAdapter(unpick_adapter);


        pick_adapter = new Member_ListAdapter(this);
        pick_adapter.setData(pick_list);
        pickMember.setNumColumns(5);
        pickMember.setAdapter(pick_adapter);


        dialog = new AlertDialog.Builder(this, R.style.hitStyle).setView(dialog_view).create();
        dialog.show();

        Window dialogWindow = dialog.getWindow();
        WindowManager m = this.getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高度
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        p.height = (int) (d.getHeight() * 0.8); // 高度设置为屏幕的0.6，根据实际情况调整
        p.width = (int) (d.getWidth() * 0.6); // 宽度设置为屏幕的0.65，根据实际情况调整
        dialogWindow.setAttributes(p);
    }

    //------選擇傳送類型------//
    public void selectType() {
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
                    hit = new Hit("1", Message.this);
                    hit.set_hitTitle("提示");
                    hit.set_hitContent("請選擇訊息的傳送方式");
                    hit.get_hitConfirm().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            hit_dialog.dismiss();
                        }
                    });
                    hit_dialog = new AlertDialog.Builder(Message.this,R.style.hitStyle).setView(hit.get_view()).create();
                    hit_dialog.show();
                } else {
                    dialog.dismiss();
                    inputMessage();
                }
            }
        });


        dialog = new AlertDialog.Builder(this, R.style.hitStyle).setView(dialog_view).create();
        dialog.show();
        Window dialogWindow = dialog.getWindow();
        WindowManager m = this.getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高度
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        p.height = (int) (d.getHeight() * 0.8); // 高度设置为屏幕的0.6，根据实际情况调整
        p.width = (int) (d.getWidth() * 0.6); // 宽度设置为屏幕的0.65，根据实际情况调整
        dialogWindow.setAttributes(p);
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
                    String subject = "";
                    for (HashMap<String, Object> entry : pick_list) {
                        subject += entry.get("account") + "-";
                    }
                    subject = subject.substring(0, subject.length() - 1);

                    switch (send_type_cmd) {
                        case "normal":
                            socketTrans.setParams("register_cheer", user.account, subject, msgStr, currTid);
                            socketTrans.send(socketTrans.getParams());
                            break;
                        case "anonymous":
                            break;
                        case "anonymousask":
                            break;
                    }
                    dialog.dismiss();
                } catch (Exception e) {
                    Log.v("jim_cheerSubmit", e.toString());
                }
            }
        });

        dialog = new AlertDialog.Builder(this, R.style.hitStyle).setView(dialog_view).create();
        dialog.show();
        Window dialogWindow = dialog.getWindow();
        WindowManager m = this.getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高度
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        p.height = (int) (d.getHeight() * 0.8); // 高度设置为屏幕的0.6，根据实际情况调整
        p.width = (int) (d.getWidth() * 0.6); // 宽度设置为屏幕的0.65，根据实际情况调整
        dialogWindow.setAttributes(p);

    }
}
