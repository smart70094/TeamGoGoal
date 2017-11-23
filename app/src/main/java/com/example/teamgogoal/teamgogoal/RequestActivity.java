package com.example.teamgogoal.teamgogoal;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RequestActivity extends AppCompatActivity {
    LoginActivity.User user;
    RequestDB db;

    List<HashMap<String, String>> request_list = new ArrayList<>();
    static Request_ListAdapter request_listAdapter;
    ListView request_listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        request_listview = (ListView) findViewById(R.id.request_list);
        db = new RequestDB();
        user = LoginActivity.getUser();
        loading();
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) { // 攔截返回鍵
            finish();
        }
        return true;
    }

    protected void loading() {
        synchronized (this) {
            new LoadingRequest().execute();
        }
    }

    private class LoadingRequest extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            return db.readRequest();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONArray array = new JSONArray(s);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);

                    HashMap<String, String> request_hashmap = new HashMap<>();
                    request_hashmap.put("rid", obj.getString("rid"));
                    request_hashmap.put("cmd", obj.getString("cmd"));
                    request_hashmap.put("cmdContext", obj.getString("cmdContext"));
                    request_hashmap.put("originator", obj.getString("originator"));
                    request_hashmap.put("tid", obj.getString("tid"));
                    request_list.add(request_hashmap);

                }
                request_listAdapter = new Request_ListAdapter(RequestActivity.this, db);
                request_listAdapter.setData(request_list);
                request_listview.setAdapter(request_listAdapter);
                request_listAdapter.notifyDataSetChanged();

            } catch (Exception e) {

            }
        }
    }

    public void toEditProfile(View view) {
        Intent intent = new Intent();
        intent.setClass(RequestActivity.this, EditProfile.class);
        startActivity(intent);
    }

    public void checkReview(View view) {
        Intent intent = new Intent();
        intent.setClass(RequestActivity.this, Review.class);
        startActivity(intent);
    }


}
