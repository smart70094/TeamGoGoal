package com.example.teamgogoal.teamgogoal;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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

public class Record extends AppCompatActivity {
    ListView listview;
    private List<String> RecordData = new ArrayList<String>();
    private listadapter listAdapter;
    private String userID, tid, target;
    private int planet_imv;
    private ImageView imageview;
    private TextView textview;
    List<HashMap<String, String>> list = new ArrayList<>();
    String localhost = LoginActivity.getLocalHost();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        imageview = (ImageView) findViewById(R.id.imageView);
        textview = (TextView) findViewById(R.id.textView);
        Intent intent = getIntent();
        //取得傳遞過來的資料
        userID = intent.getStringExtra("userID");
        tid = intent.getStringExtra("tid");
        target = intent.getStringExtra("target");
        planet_imv = intent.getIntExtra("planet_imv", 0);

        imageview.setImageResource(planet_imv);
        textview.setText(target);
        //step1:找出回顧資料
        String phpurl = localhost + "searchRecord.php?userName=" + userID + "&targetID=" + tid;
        new TransTask().execute(phpurl);

        listview = (ListView) findViewById(R.id.listview1);

        listAdapter = new listadapter(this);
        listAdapter.setData(list);

        listview.setAdapter(listAdapter);
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
                    hashmap.put("date", "2017/07/12");
                    hashmap.put("recordText", obj.getString("record"));
                    list.add(hashmap);
                    String text = obj.getString("record");

                    Log.d("JSON:", text + "/");
                    RecordData.add(text);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
