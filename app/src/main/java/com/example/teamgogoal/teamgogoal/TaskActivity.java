package com.example.teamgogoal.teamgogoal;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TaskActivity extends AppCompatActivity {

    TaskDB db;
    //LinearLayout taskll;
    TextView taskTitle;
    EditText taskNameTxt, taskContent, remindTimeTxt, cheerEt;
    Button submit, submitTaskBtn, clearTaskMessageBtn, cannelTaskBtn;
    Map<Integer, TaskUIStructure> taskMap = new HashMap<Integer, TaskUIStructure>();
    Spinner spinner;
    String currTid = "", nextID = "", currID = "";
    LoginActivity.User user;
    AlertDialog.Builder cheerDialog;
    AlertDialog taskMsg = null, msg = null, dialog = null;   //dialog建興的
    AlertDialog.Builder msgDialog = null;
    View addTaskMsg;
    final String[] list = {"earth", "jupiter", "mars"};
    SocketTrans socketTrans = LoginActivity.socketTrans;
    //進度條-建興
    CircularProgressBar circularProgressBar;
    int percentage;
    //進度條結束


    /*---Date:1015 rebuild----*/
    List<HashMap<String, String>> taskDate = new ArrayList<>();
    private Task_ListAdapter task_listAdapter;
    ListView task_listview;
    int map_id;
    /*---Date:1015 rebuild----*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        try {
            Bundle bundle = getIntent().getExtras();
            user = LoginActivity.getUser();
            currTid = bundle.getString("tid");

            String t_name = bundle.getString("t_name");
            db = new TaskDB(LoginActivity.getLocalHost() + "readmission.php");

            LayoutInflater factory = LayoutInflater.from(this);
            addTaskMsg = factory.inflate(R.layout.activity_task_add_msg, null);
            //taskll = (LinearLayout) findViewById(R.id.taskll);

            taskNameTxt = (EditText) addTaskMsg.findViewById(R.id.taskNameTxt);

            taskContent = (EditText) addTaskMsg.findViewById(R.id.taskContent);
            spinner = (Spinner) addTaskMsg.findViewById(R.id.spinner);
            ArrayAdapter<String> lunchList = new ArrayAdapter<>(TaskActivity.this,
                    android.R.layout.simple_spinner_dropdown_item,
                    list);
            spinner.setAdapter(lunchList);
            remindTimeTxt = (EditText) addTaskMsg.findViewById(R.id.remindTimeTxt);
            submitTaskBtn = (Button) addTaskMsg.findViewById(R.id.submitTaskBtn);
            cannelTaskBtn = (Button) addTaskMsg.findViewById(R.id.cannelTaskBtn);
            clearTaskMessageBtn = (Button) addTaskMsg.findViewById(R.id.clearTaskMessageBtn);
            msgDialog = new AlertDialog.Builder(TaskActivity.this, R.style.Translucent_NoTitle).setView(addTaskMsg);
            msgDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    initial();
                }
            });
            new DbOperationTask().execute("read");
            LayoutInflater factoryCheerMsg = LayoutInflater.from(this);
            View cheerMsg = factoryCheerMsg.inflate(R.layout.activity_cheer_msg, null);
            submit = (Button) cheerMsg.findViewById(R.id.cheerBtn);
            cheerEt = (EditText) cheerMsg.findViewById(R.id.cheerEt);
            cheerDialog = new AlertDialog.Builder(TaskActivity.this);
            cheerDialog.setView(cheerMsg);
            cheerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    msg.dismiss();
                }
            });
            taskTitle = (TextView) findViewById(R.id.taskTitle);
            taskTitle.setText(t_name);
            //進度條-建興
            circularProgressBar = (CircularProgressBar) findViewById(R.id.yourCircularProgressbar);
            percentage = 0;
            initDataBase();
            //進度條結束

            // ListView 滾動監聽事件
            task_listview = (ListView) findViewById(R.id.listview_task);

            task_listview.setOnScrollListener(new ListView.OnScrollListener() {
                ImageView more_botton = (ImageView) findViewById(R.id.more_arrow);

                @Override
                public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                }

                @Override
                public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totallItemCout) {
                    if (task_listview.getLastVisiblePosition() == (totallItemCout - 1)) {
                        more_botton.setVisibility(View.INVISIBLE);
                    } else {
                        more_botton.setVisibility(View.VISIBLE);
                    }
                }
            });


        } catch (Exception e) {
            Log.v("jim_Task_onCreate", e.toString());
        }
    }

    protected void showCheerMsg() {
        try {
            if (msg == null) {
                msg = cheerDialog.show();
            } else {
                msg.show();
            }
        } catch (Exception e) {
            Log.v("jim_TaskActivity_showCheerMsg", e.toString());
        }
    }

    protected void showAddTaskMsg() {
        try {
            if (taskMsg == null) {
                taskMsg = msgDialog.show();
            } else {
                taskMsg.show();
            }
        } catch (Exception e) {
            Log.v("jim_TaskActivity_showAddTaskMsg", e.toString());
        }
    }


    private class DbOperationTask extends AsyncTask<String, Void, Void> {
        protected Void doInBackground(String... params) {
            String cmd = params[0];
            switch (cmd) {
                case "read":
                    final Map<String, TaskDB.TaskDetail> t;
                    t = db.read(currTid);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            try {
                                fresh(t);
                            } catch (Exception e) {
                                Log.v("jim11", e.toString());
                            }
                        }
                    });

                    nextID = db.taskIndex();
                    break;
                case "createTask":
                    db.create(params[1], params[2], params[3], params[4], params[5], params[6], params[7], params[8]);
                    nextID = db.taskIndex();
                    break;
                case "delete":
                    db.delete(params[1]);
                    break;
                case "targetCount":
                    // tempID=db.targetCount();
                    break;
                case "updateTask":

                    db.update(params[1], params[2], params[3], params[4], params[5], params[6]);

                    break;
            }
            return null;
        }

    }

    protected void delete(int id) {
        try {

            TaskActivity.TaskUIStructure taskUIS = taskMap.get(id);
            TaskDB.TaskDetail td = taskUIS.td;
            new TaskActivity.DbOperationTask().execute("delete", td.mid);
            taskMap.remove(id);
            taskDate.remove(map_id);
            task_listAdapter.notifyDataSetChanged();

        } catch (Exception e) {
            Log.v("jim1", e.toString());
        }
    }

    protected void update() {
        String param1 = taskNameTxt.getText().toString();
        String param2 = taskContent.getText().toString();
        String param3 = remindTimeTxt.getText().toString();
        String param4 = currTid;
        String param5 = spinner.getSelectedItem().toString();

        int key = Integer.parseInt(currID.trim());
        TaskActivity.TaskUIStructure taskUIS = taskMap.get(key);
        taskUIS.td.missionName = param1;
        taskUIS.td.missionContent = param2;
        taskUIS.td.remindTime = param3;
        taskUIS.td.tid = param4;
        taskUIS.td.planet = param5;
        taskUIS.txtName.setText(param2 + "      執行者:" + taskUIS.td.auth);
        new DbOperationTask().execute("updateTask", currID, param1, param2, param3, param4, param5);
        currID = "";

        taskDate.get(map_id).put("missionName", param1);
        task_listAdapter.notifyDataSetChanged();

        taskMsg.dismiss();
    }

    protected void addTask() {

        if (taskNameTxt.getText().toString().equals("") || taskContent.getText().toString().equals("") || remindTimeTxt.getText().toString().equals("")) {
            Toast.makeText(this, "請輸入完整資料", Toast.LENGTH_SHORT).show();
        } else {

            String param1 = taskNameTxt.getText().toString();
            String param2 = taskContent.getText().toString();
            String param3 = remindTimeTxt.getText().toString();
            String param4 = currTid;
            String param5 = spinner.getSelectedItem().toString();
            String param6 = "no";
            String param7 = user.account;

            nextID = nextID.trim();
            TaskDB.TaskDetail td = new TaskDB.TaskDetail(nextID, param1, param2, param3, param4, param5, param6, param7, "null");

            new TaskActivity.DbOperationTask().execute("createTask", nextID, param1, param2, param3, param4, param5, param6, param7);


            HashMap<String, String> tk_hashmap = new HashMap<>();
            tk_hashmap.put("mid", nextID);
            tk_hashmap.put("personal_photo", "null");
            tk_hashmap.put("missionName", param1);

            taskDate.add(tk_hashmap);
            task_listAdapter.notifyDataSetChanged();


            int k = Integer.parseInt(nextID.trim());
            TaskUIStructure targetUIS = new TaskUIStructure(td, new LinearLayout(this), null, new TextView(this));
            taskMap.put(k, targetUIS);
            Toast.makeText(this, "新增任務成功", Toast.LENGTH_SHORT).show();
            taskMsg.dismiss();
        }
    }


    protected void fresh(Map<String, TaskDB.TaskDetail> map) {
        Iterator it = map.entrySet().iterator();

        while (it.hasNext()) {

                  /*------Date:1015 rebuild-----*/


            Map.Entry<String, TaskDB.TaskDetail> set = (Map.Entry) it.next();

            String s = set.getValue().mid.trim();
            Integer key = Integer.parseInt(s);


            if (!taskMap.containsKey(key)) {
                HashMap<String, String> tk_hashmap = new HashMap<>();
                tk_hashmap.put("mid", s);
                tk_hashmap.put("personal_photo", "null");
                tk_hashmap.put("missionName", set.getValue().missionName);
                tk_hashmap.put("state",set.getValue().state);
                tk_hashmap.put("auth",set.getValue().auth);
                taskDate.add(tk_hashmap);

                TaskUIStructure taskUIS = new TaskUIStructure(set.getValue(), new LinearLayout(this), null, new TextView(this));
                taskMap.put(key, taskUIS);
            }

            /*------Date:1015 rebuild-----*/

        }

        task_listAdapter = new Task_ListAdapter(this);
        task_listAdapter.setData(taskDate);

        task_listview.setAdapter(task_listAdapter);

        task_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                currID = taskDate.get(i).get("mid");
                showCheerMsg();
            }
        });

        task_listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                map_id = i;
                int id = Integer.valueOf(taskDate.get(i).get("mid"));
                String auth = taskMap.get(id).td.auth.trim();
                if (auth.equals(user.account.trim())) {
                    final AlertDialog mutiItemDialog = getMutiItemDialog(new String[]{"詳細", "修改", "刪除", "撰寫回顧"}, id);
                    mutiItemDialog.show();
                } else {
                    submitTaskBtn.setEnabled(false);
                    clearTaskMessageBtn.setEnabled(false);
                    peek(id);
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
                        peek(id);
                        submitTaskBtn.setEnabled(false);
                        clearTaskMessageBtn.setEnabled(false);
                        cannelTaskBtn.setEnabled(false);
                        break;
                    case "修改":
                        peek(id);
                        submitTaskBtn.setText("更新資料");
                        clearTaskMessageBtn.setEnabled(false);
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

    protected void peek(int id) {
        showAddTaskMsg();
        TaskUIStructure taskUIS = taskMap.get(id);
        taskNameTxt.setText(taskUIS.td.missionName);
        taskContent.setText(taskUIS.td.missionContent);
        remindTimeTxt.setText(taskUIS.td.remindTime);
        int pos = -1;
        for (int i = 0; i < list.length; i++) {
            if (taskUIS.td.planet.equals(list[i])) {
                pos = i;
                break;
            }
        }
        pos -= 1;
        spinner.setSelection(pos);
    }

    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.showAddTaskBtn:
                showAddTaskMsg();
                break;
            case R.id.selectRemindTime:
                alarm();
                break;
            case R.id.cannelTaskBtn:
                cancel();
                break;
            case R.id.submitTaskBtn:
                if (currID.equals("")) addTask();
                else update();
                break;
            case R.id.cheerBtn:
                cheerSubmit();
                break;
        }
    }

    public void cheerSubmit() {
        try {
            String msgStr = cheerEt.getText().toString();
            Integer key = Integer.parseInt(currID.trim());
            TaskUIStructure taskUIS = taskMap.get(key);

            socketTrans.setParams("register_cheer", user.account, taskUIS.td.auth.trim(), msgStr);
            socketTrans.send(socketTrans.getParams());
            msg.dismiss();
        } catch (Exception e) {
            Log.v("jim_cheerSubmit", e.toString());
        }
    }

    protected void alarm() {
        final EditText remindTime = remindTimeTxt;
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        // Create a new instance of TimePickerDialog and return it
        new TimePickerDialog(TaskActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                remindTime.setText(hourOfDay + ":" + minute);
                Log.v("jim_alarm", hourOfDay + ":" + minute);
            }
        }, hour, minute, false).show();
    }

    protected void initial() {
        taskNameTxt.setText("");
        taskContent.setText("");
        remindTimeTxt.setText("");
        spinner.setSelection(0);
        submitTaskBtn.setEnabled(true);
        clearTaskMessageBtn.setEnabled(true);
        cannelTaskBtn.setEnabled(true);
        submitTaskBtn.setText("新增任務");
    }

    protected void cancel() {
        initial();
        taskMsg.dismiss();
    }

    public class TaskUIStructure {
        TaskDB.TaskDetail td;
        LinearLayout ll;
        ImageView img;
        TextView txtName;

        TaskUIStructure(TaskDB.TaskDetail td, LinearLayout ll, ImageView img, TextView txtNam) {
            this.td = td;
            this.ll = ll;
            this.img = img;
            this.txtName = txtNam;
        }
    }

    //------------帥哥建興的--------------
    private void completeTask(final Integer key) {
        final View dialog_view;
        Complete_Task_DialogHolder holder = new Complete_Task_DialogHolder();

        dialog_view = LayoutInflater.from(this).inflate(R.layout.complete_task_dialog, null);

        holder.confirm = (Button) dialog_view.findViewById(R.id.button5);
        holder.cancel = (Button) dialog_view.findViewById(R.id.button6);


        holder.confirm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String mid = Integer.toString(key);
                Log.d("hhhhhhhhhhh", mid);
                //String phpurl = LoginActivity.getLocalHost() + "updateTaskState.php?mid=" + mid;
                String phpurl = LoginActivity.getLocalHost() + "updateTaskState.php?mid=" + mid + "&tid=" + currTid + "&uid=" + LoginActivity.getUser().uid + "&partnerid=" + taskMap.get(key).td.collaborator;
                new TransTask().execute(phpurl);
            }
        });

        holder.cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog = new AlertDialog.Builder(this)
                .setView(dialog_view)
                .show();
    }

    static class Complete_Task_DialogHolder {
        Button confirm;
        Button cancel;
    }

    //--------------資料庫連接 Code   ---------------------//

    private class TransTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String wholeDocument = null;
            URL url = null;
            try {
                url = new URL(params[0]);
                Log.d("CC", url.toString());
                URLConnection conn = url.openConnection();
                conn.setRequestProperty("Accept-Charset", "utf8");
                InputStreamReader isr = new InputStreamReader(conn.getInputStream(), "utf8");
                BufferedReader in = new BufferedReader(isr);

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    wholeDocument += inputLine;
                }
                isr.close();
                in.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return wholeDocument;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("JSON", s);
            dialog.dismiss();
        }
    }


    //--------------資料庫連接 Code   進度條搜尋百分比---------------------//

    private void initDataBase() {
        String phpurl = LoginActivity.getLocalHost() + "searchPercent.php?tid=" + currTid;
        new TransTask_searchPercent().execute(phpurl);
    }

    private class TransTask_searchPercent extends AsyncTask<String, Void, String> {

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
            parseJSON(s);
            initView();
        }

        private void parseJSON(String s) {
            try {
                JSONArray array = new JSONArray(s);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    double all_mission = obj.getDouble("all_mission");
                    double complete_mission = obj.getDouble("complete_mission");
                    double _percentage = (complete_mission / all_mission) * 100;

                    percentage = (int) (_percentage + 0.5);
                    Log.d("dd", Double.toString(percentage));
                    //percentage = (complete_mission / all_mission) * 100;
                    //Log.d("ddddddddddddddddp",Integer.toString(percentage));

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private void initView() {
            circularProgressBar.setColor(ContextCompat.getColor(TaskActivity.this, R.color.processbar));
            circularProgressBar.setBackgroundColor(ContextCompat.getColor(TaskActivity.this, R.color.processbar_bg));
            circularProgressBar.setProgressBarWidth(18);
            circularProgressBar.setBackgroundProgressBarWidth(circularProgressBar.getProgressBarWidth());
            int animationDuration = 2500; // 2500ms = 2,5s
            circularProgressBar.setProgressWithAnimation(percentage, animationDuration); // Default duration = 1500ms
        }

    }


    //---------------- 元素搜尋---------------------//


    public void checkElement(View view) {
        String phpurl = LoginActivity.getLocalHost() + "searchElement.php?&tid=" + currTid + "&uid=" + LoginActivity.getUser().uid;
        new TransTask_searchElement().execute(phpurl);
    }


    private class TransTask_searchElement extends AsyncTask<String, Void, String> {
        String mine, mountain, snow, fire, soil;
        private ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(TaskActivity.this);
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
            parseJSON(s);
            initElementView();
            pd.dismiss();
        }


        private void parseJSON(String s) {
            try {
                JSONArray array = new JSONArray(s);
                JSONObject obj = array.getJSONObject(0);
                mine = obj.getString("mine");
                mountain = obj.getString("mountain");
                snow = obj.getString("snow");
                fire = obj.getString("fire");
                soil = obj.getString("soil");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private void initElementView() {
            View dialog_view = LayoutInflater.from(TaskActivity.this).inflate(R.layout.element_dialog, null);
            ;

            TextView count_mine = ((TextView) dialog_view.findViewById(R.id.count_mine));
            TextView count_mountain = (TextView) dialog_view.findViewById(R.id.count_mountain);
            TextView count_snow = (TextView) dialog_view.findViewById(R.id.count_snow);
            TextView count_fire = (TextView) dialog_view.findViewById(R.id.count_fire);
            TextView count_soil = (TextView) dialog_view.findViewById(R.id.count_soil);
            Button Comfirm = (Button) dialog_view.findViewById(R.id.hitComfirm);

            count_mine.setText(mine);
            count_mountain.setText(mountain);
            count_snow.setText(snow);
            count_fire.setText(fire);
            count_soil.setText(soil);

            Comfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            dialog = new AlertDialog.Builder(TaskActivity.this, R.style.Translucent_NoTitle).setView(dialog_view).create();
            dialog.show();
        }

    }


    //-----標提列----//
    public void toEditProfile(View view) {
        Intent intent = new Intent();
        intent.setClass(this, EditProfile.class);
        startActivity(intent);
    }

    public void checkReview(View view) {
        Intent intent = new Intent();
        intent.setClass(this, Review.class);
        startActivity(intent);
    }

    public void toRequest(View view) {
        Intent intent = new Intent();
        intent.setClass(this, RequestActivity.class);
        startActivity(intent);
    }

}
