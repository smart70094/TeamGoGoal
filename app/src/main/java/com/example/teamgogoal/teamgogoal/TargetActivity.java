package com.example.teamgogoal.teamgogoal;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TargetActivity extends AppCompatActivity {
    AlertDialog dialog;
    LoginActivity.User user;
    TargetDB db;
    Intent intent;


    /*---Date:1015 rebuild----*/
    List<HashMap<String, String>> TargetData = new ArrayList<>();
    private Target_ListAdapter target_listAdapter;
    ListView target_listview;
    int map_id;
    /*---Date:1015 rebuild----*/

    SocketTrans socketTrans = LoginActivity.socketTrans;
    static Map<Integer, TargetDB.TargetDetail> targetMap = new HashMap<Integer, TargetDB.TargetDetail>();

    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_target);
            db = new TargetDB();
            user = LoginActivity.getUser();

        } catch (Exception e) {
            Log.v("jim", e.toString());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loading();
    }

    protected void loading() {
        synchronized (this) {
            targetMap.clear();
            TargetData.clear();
            new DbOperationTask().execute("readTarget");
        }
    }

    //下方按鈕-新增目標
    public void addTarget(View view) {
        Intent intent = new Intent();
        intent.setClass(this, TargetEventActivity.class);
        intent.putExtra("cmd", "addTarget");
        startActivity(intent);
    }

    protected void fresh(Map<String, TargetDB.TargetDetail> map) {
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            /*------Date:1015 rebuild-----*/
            Map.Entry<String, TargetDB.TargetDetail> set = (Map.Entry) it.next();

            String s = set.getValue().tid.trim();
            Integer key = Integer.parseInt(s);


            if (!targetMap.containsKey(key)) {
                HashMap<String, String> tg_hashmap = new HashMap<>();
                tg_hashmap.put("tid", s);
                tg_hashmap.put("planet_imv", "null");
                tg_hashmap.put("targetName", set.getValue().targetName);
                tg_hashmap.put("targetDate", set.getValue().startTime.trim().replace("-", ".") + "-" + set.getValue().endTime.trim().replace("-", "."));
                tg_hashmap.put("allmission", set.getValue().allmission);
                tg_hashmap.put("completemission", set.getValue().completemission);
                TargetData.add(tg_hashmap);
                TargetDB.TargetDetail td = set.getValue();
                targetMap.put(key, td);
            }
            /*------Date:1015 rebuild-----*/
        }

        target_listview = (ListView) findViewById(R.id.listview_target);
        target_listAdapter = new Target_ListAdapter(this);
        target_listAdapter.setData(TargetData);
        target_listview.setAdapter(target_listAdapter);
        target_listAdapter.notifyDataSetChanged();


        // 目標項目-短按事件:瀏覽任務
        target_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                enterTaskActivity(Integer.valueOf(TargetData.get(i).get("tid")), TargetData.get(i).get("targetName"));
            }
        });

        // 目標項目-長按事件:目標詳細事項(詳細、修改、刪除)
        target_listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                map_id = i;
                final Integer id = Integer.valueOf(TargetData.get(i).get("tid"));
                String auth = targetMap.get(id).auth.trim();
                if (auth.equals(user.account)) {

                    View dialog_view = LayoutInflater.from(TargetActivity.this).inflate(R.layout.target_selector, null);


                    Button readTarget = (Button) dialog_view.findViewById(R.id.readTarget);
                    Button modifyTarget = (Button) dialog_view.findViewById(R.id.modifyTarget);
                    Button deleteTarget = (Button) dialog_view.findViewById(R.id.deleteTarget);


                    readTarget.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showTargetEvent(id, "readTarget");
                            dialog.dismiss();
                        }
                    });

                    modifyTarget.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showTargetEvent(id, "modifyTarget");
                            dialog.dismiss();
                        }
                    });

                    deleteTarget.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            delete(id);
                            dialog.dismiss();
                        }
                    });
                    dialog = new AlertDialog.Builder(TargetActivity.this, R.style.hitStyle).setView(dialog_view).create();
                    dialog.show();

                } else {
                    showTargetEvent(id, "readTarget");
                }
                return true;
            }

        });

    }

    protected void delete(int id) {
        try {
            TargetDB.TargetDetail td = targetMap.get(id);
            targetMap.remove(id);
            TargetData.remove(map_id);
            target_listAdapter.notifyDataSetChanged();
            if (user.account.equals(td.auth.trim()))
                new DbOperationTask().execute("deleteParticipator_all", td.tid);

            else
                new DbOperationTask().execute("deleteParticipator", td.tid, user.account);
        } catch (Exception e) {
            Log.v("jim1", e.toString());
        }
    }

    protected void showTargetEvent(int id, String cmd) {
        TargetDB.TargetDetail td = targetMap.get(id);


        Intent intent = new Intent();
        intent.putExtra("cmd", cmd);
        intent.putExtra("tid", td.tid);
        intent.putExtra("targetName", td.targetName);
        intent.putExtra("targetContent", td.targetContent);
        intent.putExtra("startTime", td.startTime);
        intent.putExtra("endTime", td.endTime);

        intent.setClass(TargetActivity.this, TargetEventActivity.class);
        startActivity(intent);
    }

    //background run
    private class DbOperationTask extends AsyncTask<String, Void, Void> {
        protected Void doInBackground(String... params) {
            String cmd = params[0];
            switch (cmd) {
                case "readTarget":
                    final Map<String, TargetDB.TargetDetail> t;
                    t = db.readTarget();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            try {
                                fresh(t);
                            } catch (Exception e) {
                                Log.v("jim_DbOperationTask", e.toString());
                            }
                        }
                    });
                    break;
                case "deleteParticipator_all":
                    db.deleteTargetAll(params[1]);
                    break;
                case "deleteParticipator":
                    db.deleteParticipator(params[1], params[2]);
                    break;
            }
            return null;
        }
    }

    public void enterTaskActivity(int tid, String targetName) {
        try {
            Intent intent = new Intent();
            intent.putExtra("tid", Integer.toString(tid));
            intent.putExtra("targetName", targetName);
            intent.setClass(TargetActivity.this, TaskActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            Log.v("jim_enterTaskActivity", e.toString());
        }
    }

    //帥哥峻禾部分
    public void toEditProfile(View view) {
        intent = new Intent();
        intent.setClass(TargetActivity.this, EditProfile.class);
        startActivity(intent);
    }

    public void toRequest(View view) {
        intent = new Intent();
        intent.setClass(TargetActivity.this, RequestActivity.class);
        startActivity(intent);
    }

    public void checkReview(View view) {
        Intent intent = new Intent();
        intent.setClass(this, Review.class);

        startActivity(intent);
    }


    public void fresh_activity(View view) {
        loading();
    }
}
