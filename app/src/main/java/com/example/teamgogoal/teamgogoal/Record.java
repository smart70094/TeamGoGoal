package com.example.teamgogoal.teamgogoal;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import com.teamgogoal.view.activity.R;

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
import java.util.concurrent.ExecutionException;

public class Record extends AppCompatActivity {
    ListView recordListview,messageListView;
    private List<String> RecordData = new ArrayList<String>();
    private Record_ListAdapter record_listAdapter;
    private Record_Message_ListAdapter record_message_listAdapter;
    private String userID, tid, target;
    private int planet_imv;
    private ImageView imageview;
    private TextView recordTitle;
    private TabHost tabhost;
    List<HashMap<String, String>> record_list = new ArrayList<>();
    List<HashMap<String, String>> record_message_list = new ArrayList<>();
    String localhost = LoginActivity.getLocalHost();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        imageview = (ImageView) findViewById(R.id.imageView);
        recordTitle = (TextView) findViewById(R.id.RecordTitle);

        tabhost = (TabHost) findViewById(R.id.tabHost);
        tabhost.setup();

        tabhost.addTab(setupTab("tab1", "心情日記", R.id.tab1));
        tabhost.addTab(setupTab("tab2", "蒐集鼓勵", R.id.tab2));

        Intent intent = getIntent();
        //取得傳遞過來的資料
        userID = intent.getStringExtra("userID");
        tid = intent.getStringExtra("tid");
        target = intent.getStringExtra("target");
        planet_imv = intent.getIntExtra("planet_imv", 0);

        imageview.setImageResource(planet_imv);
        recordTitle.setText(target);
        //step1:找出回顧資料
        String phpurl = localhost + "searchRecord.php?uid=" + userID + "&tid=" + tid;
        String result = null;
        try {
            result = new TransTask().execute(phpurl).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        initRecordListView(result);
        //step2:找出訊息資料
        phpurl = localhost + "searchMessage.php?uid=" + userID + "&tid=" + tid;
        result = null;
        try {
            result = new TransTask().execute(phpurl).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        initMessageListView(result);
    }



    private TabHost.TabSpec setupTab(String name, String label, int content) {

        View tab = LayoutInflater.from(this).inflate(R.layout.review_custom_tab, null);
        TextView text = (TextView) tab.findViewById(R.id.text);
        text.setText(label);

        TabHost.TabSpec spec = tabhost.newTabSpec(name).setIndicator(tab).setContent(content);
        return spec;
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
    }

    private void initRecordListView(String s) {
        try {
            JSONArray array = new JSONArray(s);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                HashMap<String, String> hashmap = new HashMap<>();
                hashmap.put("date", "2017/07/12");
                hashmap.put("recordText", obj.getString("record"));
                record_list.add(hashmap);
                String text = obj.getString("record");

                Log.d("JSON:", text + "/");
                RecordData.add(text);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        recordListview = (ListView) findViewById(R.id.listview1);

        record_listAdapter = new Record_ListAdapter(this);
        record_listAdapter.setData(record_list);

        recordListview.setAdapter(record_listAdapter);

    }

    private void initMessageListView(String s) {
        try {
            JSONArray array = new JSONArray(s);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                HashMap<String, String> hashmap = new HashMap<>();
                hashmap.put("context",obj.getString("context"));
                hashmap.put("originator",obj.getString("originator"));
                hashmap.put("date", obj.getString("date"));
                record_message_list.add(hashmap);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        messageListView = (ListView) findViewById(R.id.listview2);

        record_message_listAdapter = new Record_Message_ListAdapter(this);
        record_message_listAdapter.setData(record_message_list);

        messageListView.setAdapter(record_message_listAdapter);
    }

    public void cancel(View view) {
        finish();
    }
}
