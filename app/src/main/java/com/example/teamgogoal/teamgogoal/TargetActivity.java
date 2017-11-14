package com.example.teamgogoal.teamgogoal;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TargetActivity extends AppCompatActivity {
    AlertDialog.Builder dialog;
    AlertDialog msg = null;
    LoginActivity.User user;
    TargetDB db;
    EditText targetNameEt, targeContentEt, startTimeEt, endTimeEt;
    Button submitTargetBtn, clearTargetBtn, cannelBtn;
    View addTargetMsg;

    String nextID = "", currID = "";
    Intent intent;


    /*---Date:1015 rebuild----*/
    List<HashMap<String, String>> TargetData = new ArrayList<>();
    private Target_ListAdapter target_listAdapter;
    ListView target_listview;
    int map_id;
    /*---Date:1015 rebuild----*/

    /*---Date:1019 處理中*/
    private ProgressDialog pd;
    /*---Date:1019 處理中*/

    SocketTrans socketTrans = LoginActivity.socketTrans;
    static Map<Integer, TargetDB.TargetDetail> targetMap = new HashMap<Integer, TargetDB.TargetDetail>();

    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_target);
            db = new TargetDB();
            LayoutInflater factory = LayoutInflater.from(this);
            addTargetMsg = factory.inflate(R.layout.activity_target_add_msg, null);
            dialog = new AlertDialog.Builder(TargetActivity.this, R.style.Translucent_NoTitle);
            targetNameEt = (EditText) addTargetMsg.findViewById(R.id.targetNameTxt);
            targeContentEt = (EditText) addTargetMsg.findViewById(R.id.targetContent);
            startTimeEt = (EditText) addTargetMsg.findViewById(R.id.startTimeTxt);
            endTimeEt = (EditText) addTargetMsg.findViewById(R.id.EndTimeTxt);

            submitTargetBtn = (Button) addTargetMsg.findViewById(R.id.submitTargetBtn);
            clearTargetBtn = (Button) addTargetMsg.findViewById(R.id.clearMessageBtn);
            cannelBtn = (Button) addTargetMsg.findViewById(R.id.cannelBtn);
            dialog.setView(addTargetMsg);

            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    initial();
                }
            });
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

    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.showAddTargetBtn:
                showTarget();
                //participatorTxt.setText(user.account);
                break;
            case R.id.submitTargetBtn:
                /*if (currID.equals("")) addTarget();
                else update(Integer.parseInt(currID));
                break;*/
            case R.id.clearMessageBtn:
                initial();
                break;
            case R.id.selectStartTimeBtn:
                selectDate(R.id.startTimeTxt);
                break;
            case R.id.selectEndTimeBtn:
                selectDate(R.id.EndTimeTxt);
                break;
            case R.id.cannelBtn:
                cancel();
                break;
        }
    }



    protected void showTarget() {
        try {
            if (msg == null) {
                msg = dialog.show();

            } else {
                msg.show();
            }
        } catch (Exception e) {
            Log.v("jim", e.toString());
        }
    }

    public void addTarget(View view) {
        Intent intent = new Intent();
        intent.setClass(this,TargetEventActivity.class);
        intent.putExtra("cmd","addTarget");
        startActivity(intent);
    }

    protected void initial() {
        targetNameEt.setText("");
        targeContentEt.setText("");
        startTimeEt.setText("");
        endTimeEt.setText("");
        submitTargetBtn.setEnabled(true);
        submitTargetBtn.setText("新增任務");
        clearTargetBtn.setEnabled(true);
        cannelBtn.setEnabled(true);

    }

    protected void cancel() {
        initial();
        msg.dismiss();
    }

    protected void selectDate(final int txtID) {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        new DatePickerDialog(TargetActivity.this, new DatePickerDialog.OnDateSetListener() {
            int id = txtID;
            public void onDateSet(DatePicker view, int year, int month, int day) {
                String format = setDateFormat(year, month, day);
                EditText txt = (EditText) addTargetMsg.findViewById(id);
                txt.setText(format);
            }
        }, mYear, mMonth, mDay).show();

    }

    private String setDateFormat(int year, int monthOfYear, int dayOfMonth) {
        return String.valueOf(year) + "-"
                + String.valueOf(monthOfYear + 1) + "-"
                + String.valueOf(dayOfMonth);
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
                tg_hashmap.put("targetDate", set.getValue().startTime.trim().replace("-",".") + "-" + set.getValue().endTime.trim().replace("-","."));
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

        target_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                enterTaskActivity(Integer.valueOf(TargetData.get(i).get("tid")), TargetData.get(i).get("targetName"));
            }
        });

        target_listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                map_id = i;
                Integer id = Integer.valueOf(TargetData.get(i).get("tid"));
                String auth = targetMap.get(id).auth.trim();
                if (auth.equals(user.account)) {
                    final AlertDialog mutiItemDialog = getMutiItemDialog(new String[]{"詳細", "修改", "刪除"}, id);
                    mutiItemDialog.show();
                } else {
                    submitTargetBtn.setEnabled(false);
                    clearTargetBtn.setEnabled(false);
                    showTargetEvent(id,"readTarget");
                }
                return true;
            }

        });

    }

    public AlertDialog getMutiItemDialog(final String[] cmd, final int id) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //設定對話框內的項目
        builder.setItems(cmd, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int index) {
                switch (cmd[index]) {
                    case "詳細":
                        showTargetEvent(id, "readTarget");
                        break;
                    case "修改":
                        showTargetEvent(id, "modifyTarget");
                        submitTargetBtn.setText("更新資料");
                        clearTargetBtn.setEnabled(false);
                        currID = Integer.toString(id).trim();
                        break;
                    case "刪除":
                        delete(id);
                        break;
                }

            }
        });
        return builder.create();
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
        intent.putExtra("cmd",cmd);
        intent.putExtra("tid",td.tid);
        intent.putExtra("targetName",td.targetName);
        intent.putExtra("targetContent",td.targetContent);
        intent.putExtra("startTime",td.startTime);
        intent.putExtra("endTime",td.endTime);

        intent.setClass(TargetActivity.this,TargetEventActivity.class);
        startActivity(intent);
    }

    protected void update(int id) {
        String param1 = targetNameEt.getText().toString();
        String param2 = targeContentEt.getText().toString();
        String param3 = startTimeEt.getText().toString();
        String param4 = endTimeEt.getText().toString();
        String param5 = targetMap.get(id).state;
        String param6 = LoginActivity.user.account;

        int key = Integer.parseInt(currID.trim());
        TargetDB.TargetDetail td = targetMap.get(key);

        td.targetName = param1;
        td.targetContent = param2;
        td.startTime = param3;
        td.endTime = param4;


        //String param8=participatorTxt.getText().toString();
        new DbOperationTask().execute("updateTarget", currID, param1, param2, param3, param4, param5, param6);
        currID = "";

        TargetData.get(map_id).put("targetName", param1);
        target_listAdapter.notifyDataSetChanged();


        msg.dismiss();
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

                    nextID = db.targetIndex();
                    break;
                case "createTarget":
                    try{
                        db.createTarget(params[1], params[2], params[3], params[4], params[5]);
                        //db.createParticipator(params[1], params[6]);
                        nextID = db.targetIndex();
                    }catch(Exception e){
                        Log.v("jim_createTarget:",e.toString());
                    }
                    break;
                case "updateTarget":
                    //db.updateTarget(params[1], params[2], params[3], params[4], params[5], params[6], params[7], params[8], params[9]);
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
       /* Intent intent = new Intent(this,TargetActivity.class);
        startActivity(intent);*/
    }
}
