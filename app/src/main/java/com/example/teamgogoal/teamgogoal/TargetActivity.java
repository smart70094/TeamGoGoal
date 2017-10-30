package com.example.teamgogoal.teamgogoal;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
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
    static Map<Integer, TargetUIStructure> targetMap = new HashMap<Integer, TargetUIStructure>();

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

            // Date:8/20-監聽文字變更開始
            participatorTxt = (MultiAutoCompleteTextView) addTargetMsg.findViewById(R.id.multiAutoCompleteTextView);
            participatorTxt.setDropDownHeight(200); //設定高度
            participatorTxt.setThreshold(1);
            participatorTxt.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
            // 初始化搜尋資料
            initmactv();

            // Date:8/20-監聽文字變更結束


            //participatorTxt=(EditText) addTargetMsg.findViewById(R.id.participatorTxt);
            submitTargetBtn = (Button) addTargetMsg.findViewById(R.id.submitTargetBtn);
            clearTargetBtn = (Button) addTargetMsg.findViewById(R.id.clearMessageBtn);
            cannelBtn = (Button) addTargetMsg.findViewById(R.id.cannelBtn);
            dreamEt = (EditText) addTargetMsg.findViewById(R.id.dreamET);
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

    public void registerAlarm() {
        /*Thread thread=new Thread(new Runnable(){
            @Override
            public void run() {
                final Intent notificationIntent = new Intent(this, AlarmReceiver.class);
                String NOTIFICATION_ID="";
                String NOTIFICATION="";
                notificationIntent.putExtra(NOTIFICATION_ID, 1);
                notificationIntent.putExtra(NOTIFICATION, notification);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                //set time
                android.icu.util.Calendar calendar = android.icu.util.Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(android.icu.util.Calendar.HOUR_OF_DAY, 12);
                calendar.set(android.icu.util.Calendar.MINUTE, 03);


                alarmManager.set(AlarmManager.RTC_WAKEUP,  calendar.getTimeInMillis(), pendingIntent);
            }
        });
        thread.start();*/
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
                else update();
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
                String param5 = "no";
                String param6 = LoginActivity.user.account;
                String param7 = dreamEt.getText().toString();
                String param8 = participatorTxt.getText().toString();
                param8 += user.account + ",";

                nextID = nextID.trim();
                TargetDB.TargetDetail td = new TargetDB.TargetDetail(nextID, param1, param2, param3, param4, param5, param6, param7, param8,"0","0");
                new DbOperationTask().execute("createTarget", nextID, param1, param2, param3, param4, param5, param6, param7, param8);


                HashMap<String, String> tg_hashmap = new HashMap<>();
                tg_hashmap.put("tid", nextID);
                tg_hashmap.put("planet_imv", "null");
                tg_hashmap.put("targetName", param1);

                TargetData.add(tg_hashmap);
                target_listAdapter.notifyDataSetChanged();


                int k = Integer.parseInt(nextID);
                //tll.setId(k);
                TargetUIStructure targetUIS = new TargetUIStructure(td, new LinearLayout(this), null, new TextView(this));
                targetMap.put(k, targetUIS);

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

    protected ImageView toCircleImage(int imgId, ImageView img) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imgId);
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
        roundedBitmapDrawable.setCircular(true);
        img.setImageDrawable(roundedBitmapDrawable);
        return img;
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

                TargetUIStructure targetUIS = new TargetUIStructure(set.getValue(), new LinearLayout(this), null, new TextView(this));
                targetMap.put(key, targetUIS);
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

                String auth = targetMap.get(id).td.auth.trim();

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
                        submitTargetBtn.setEnabled(false);
                        clearTargetBtn.setEnabled(false);
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
            TargetUIStructure targetUIS = targetMap.get(id);
            TargetDB.TargetDetail td = targetUIS.td;
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
        TargetUIStructure targetUIS = targetMap.get(id);
        TargetDB.TargetDetail td = targetUIS.td;
        targetNameEt.setText(td.targetName);
        targeContentEt.setText(td.targetContent);
        startTimeEt.setText(td.startTime);
        endTimeEt.setText(td.endTime);
        participatorTxt.setText(td.participator);
        dreamEt.setText(td.dream);
        currID = Integer.toString(id).trim();

        showTarget();
    }

    protected void update() {
        String param1 = targetNameEt.getText().toString();
        String param2 = targeContentEt.getText().toString();
        String param3 = startTimeEt.getText().toString();
        String param4 = endTimeEt.getText().toString();
        String param5 = "no";
        String param6 = LoginActivity.user.account;
        String param7 = dreamEt.getText().toString();
        String param8 = participatorTxt.getText().toString();

        int key = Integer.parseInt(currID.trim());
        TargetUIStructure targetUIS = targetMap.get(key);
        String param9 = targetUIS.td.participator;
        targetUIS.td.targetName = param1;
        targetUIS.td.targetContent = param2;
        targetUIS.td.startTime = param3;
        targetUIS.td.endTime = param4;
        targetUIS.td.dream = param7;
        targetUIS.td.participator = param8;
        targetUIS.txtName.setText(param1);

        //String param8=participatorTxt.getText().toString();
        new DbOperationTask().execute("updateTarget", currID, param1, param2, param3, param4, param5, param6, param7, param8, param9);
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
                                Log.v("jim11", e.toString());
                            }
                        }
                    });

                    nextID = db.targetIndex();
                    break;
                case "createTarget":
                    db.createTarget(params[1], params[2], params[3], params[4], params[5], params[6], params[7], params[8]);
                    db.createParticipator(params[1], params[9]);

                    nextID = db.targetIndex();
                    break;
                case "updateTarget":
                    db.updateTarget(params[1], params[2], params[3], params[4], params[5], params[6], params[7], params[8], params[9], params[10]);
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

    public class TargetUIStructure implements Serializable {
        TargetDB.TargetDetail td;
        LinearLayout ll;
        ImageView img;
        TextView txtName;

        TargetUIStructure(TargetDB.TargetDetail td, LinearLayout ll, ImageView img, TextView txtNam) {
            this.td = td;
            this.ll = ll;
            this.img = img;
            this.txtName = txtNam;
        }
    }


    //-----------------帥哥建興開始-----------------
    public void checkReview(View view) {
        Intent intent = new Intent();
        intent.setClass(this, Review.class);

        startActivity(intent);
    }

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
}
