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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TargetActivity extends AppCompatActivity {
    AlertDialog.Builder dialog;
    AlertDialog msg = null;
    String localhost = LoginActivity.getLocalHost();
    LoginActivity.User user;
    TargetDB db;
    EditText targetNameEt, targeContentEt, startTimeEt, endTimeEt, dreamEt;
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


    //8/20:AutoCompleteTextView
    MultiAutoCompleteTextView participatorTxt;

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


            /*
            // Date:8/20-監聽文字變更開始
            participatorTxt = (MultiAutoCompleteTextView) addTargetMsg.findViewById(R.id.multiAutoCompleteTextView);
            participatorTxt.setDropDownHeight(200); //設定高度
            participatorTxt.setThreshold(1);
            participatorTxt.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
            // 初始化搜尋資料
            initmactv();
*/
            // Date:8/20-監聽文字變更結束


            //participatorTxt=(EditText) addTargetMsg.findViewById(R.id.participatorTxt);
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

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) { // 攔截返回鍵
            targetMap.clear();
            finish();
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        loading();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    protected void loading() {
        synchronized (this) {
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
                if (currID.equals("")) addTarget();
                else update(Integer.parseInt(currID));
                break;
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

    protected void addTarget() {
        try {
            if ((targetNameEt.getText().toString().equals("") || targeContentEt.getText().toString().equals("") || startTimeEt.getText().toString().equals("") || endTimeEt.getText().toString().equals("") || participatorTxt.getText().toString().equals(""))) {
                Toast.makeText(this, "請輸入完整資訊", Toast.LENGTH_SHORT).show();
            } else {
                String param1 = targetNameEt.getText().toString();
                String param2 = targeContentEt.getText().toString();
                String param3 = startTimeEt.getText().toString();
                String param4 = endTimeEt.getText().toString();
                String param5 = "N";
                String param6 = LoginActivity.user.account;
                String param7 = participatorTxt.getText().toString();
                param7 += user.account + ",";

                nextID = nextID.trim();
                TargetDB.TargetDetail td = new TargetDB.TargetDetail(nextID, param1, param2, param3, param4, param5, param6, param7, "0", "0");
                new DbOperationTask().execute("createTarget",param1, param2, param3, param4, param6,param7);

                HashMap<String, String> tg_hashmap = new HashMap<>();
                tg_hashmap.put("tid", nextID);
                tg_hashmap.put("planet_imv", "null");
                tg_hashmap.put("targetName", param1);

                TargetData.add(tg_hashmap);
                target_listAdapter.notifyDataSetChanged();


                int k = Integer.parseInt(nextID);
                //tll.setId(k);
                targetMap.put(k, td);

                //帥哥峻禾部分
                socketTrans.setParams("initial_target", nextID, param4);
                socketTrans.send(socketTrans.getParams());

                msg.dismiss();
                Toast.makeText(this, "新增成功", Toast.LENGTH_SHORT).show();
                initial();
            }

        } catch (Exception e) {
            Log.v("jim1", e.toString());
        }
    }

    protected void initial() {
        targetNameEt.setText("");
        targeContentEt.setText("");
        startTimeEt.setText("");
        endTimeEt.setText("");
        participatorTxt.setText("");
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
                    read(id);
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
                        read(id);
                        break;
                    case "修改":
                        read(id);
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

    protected void read(int id) {
        TargetDB.TargetDetail td = targetMap.get(id);
        targetNameEt.setText(td.targetName);
        targeContentEt.setText(td.targetContent);
        startTimeEt.setText(td.startTime);
        endTimeEt.setText(td.endTime);
        participatorTxt.setText(td.participator);
        currID = Integer.toString(id).trim();
        showTarget();
    }

    protected void update(int id) {
        String param1 = targetNameEt.getText().toString();
        String param2 = targeContentEt.getText().toString();
        String param3 = startTimeEt.getText().toString();
        String param4 = endTimeEt.getText().toString();
        String param5 = targetMap.get(id).state;
        String param6 = LoginActivity.user.account;
        String param7 = participatorTxt.getText().toString();

        int key = Integer.parseInt(currID.trim());
        TargetDB.TargetDetail td = targetMap.get(key);
        String param8 = td.participator;
        td.targetName = param1;
        td.targetContent = param2;
        td.startTime = param3;
        td.endTime = param4;
        td.participator = param8;

        //String param8=participatorTxt.getText().toString();
        new DbOperationTask().execute("updateTarget", currID, param1, param2, param3, param4, param5, param6, param7, param8);
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
                        db.createParticipator(params[1], params[6]);
                        nextID = db.targetIndex();
                    }catch(Exception e){
                        Log.v("jim_createTarget:",e.toString());
                    }
                    break;
                case "updateTarget":
                    db.updateTarget(params[1], params[2], params[3], params[4], params[5], params[6], params[7], params[8], params[9]);
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
            //TargetUIStructure targetUIS = targetMap.get(id);
            Intent intent = new Intent();
            /*
            intent.putExtra("tid", targetUIS.td.tid.trim());
            intent.putExtra("t_name", targetUIS.td.targetName.trim());
                    */
            intent.putExtra("tid", Integer.toString(tid));
            intent.putExtra("t_name", targetName);
            //intent.putExtras(bundle);
            intent.setClass(TargetActivity.this, TaskActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            Log.v("jim_enterTaskActivity", e.toString());
        }
    }

    //-----------------帥哥建興開始-----------------


    // Date:8/20 尋找帳號開始---------------------------
    private void initmactv() {
        String phpurl = localhost + "searchID.php";
        new TransTask().execute(phpurl);
    }

    class TransTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(TargetActivity.this);
            pd.setMessage("Processing...");
            pd.show();
        }

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
            List list = parseJSON(s);
            initListView(list);
        }

        private List parseJSON(String s) {
            List list = new ArrayList();
            try {
                JSONArray array = new JSONArray(s);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    list.add(obj.getString("account"));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            pd.dismiss();
            return list;
        }
    }

    public void initListView(List list) {
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        participatorTxt.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
    // Date:8/20 尋找帳號結束---------------------------


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
        finish();
        Intent intent = new Intent(this,TargetActivity.class);
        startActivity(intent);
    }
}
