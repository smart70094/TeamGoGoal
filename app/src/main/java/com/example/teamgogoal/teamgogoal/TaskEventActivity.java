package com.example.teamgogoal.teamgogoal;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.teamgogoal.view.activity.R;

import java.util.Calendar;

public class TaskEventActivity extends AppCompatActivity {

    Bundle bundle;
    String cmd,targetName;
    TextView TargetName;
    EditText taskNameEt, taskContentEt, remindTimeEt;
    TaskDB db;
    String currTid, currMid;
    ProgressDialog progressdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_event);

        bundle = getIntent().getExtras();
        db = new TaskDB(LoginActivity.getLocalHost() + "readmission.php");

        currTid = bundle.getString("tid");
        cmd = bundle.getString("cmd");



        TargetName = (TextView) findViewById(R.id.TargetTitle);
        TargetName.setText(bundle.getString("targetName"));

        taskNameEt = (EditText) findViewById(R.id.taskNameEt);
        taskContentEt = (EditText) findViewById(R.id.taskContentEt);
        remindTimeEt = (EditText) findViewById(R.id.remindTimeEt);

        switch (cmd) {
            case "modifyTask":
            case "readTask":
                currMid = bundle.getString("mid");
                readTask();
                initView();
                break;
        }
    }


    private void readTask() {
        taskNameEt.setText(bundle.getString("taskName").trim());
        taskContentEt.setText(bundle.getString("taskContent").trim());
        remindTimeEt.setText(bundle.getString("remindTime").trim());
    }

    private void initView() {
        switch (cmd) {
            case "readTask":
                taskNameEt.setEnabled(false);
                taskContentEt.setEnabled(false);
                remindTimeEt.setEnabled(false);
                break;
        }
    }


    public void TaskEvent(View view) {
        switch (cmd) {
            case "readTask":
                finish();
                break;

            case "addTask":
                addTarget();
                break;

            case "modifyTask":
                modifyTask();
                break;
        }


    }

    private void addTarget() {

        if (taskNameEt.getText().toString().equals("") || taskContentEt.getText().toString().equals("") || remindTimeEt.getText().toString().equals("")) {
            Toast.makeText(this, "請輸入完整資料", Toast.LENGTH_SHORT).show();
        } else {

            String param1 = taskNameEt.getText().toString().trim();
            String param2 = taskContentEt.getText().toString().trim();
            String param3 = remindTimeEt.getText().toString().trim();
            String param4 = currTid.trim();
            String param5 = LoginActivity.user.account;

            //TaskDB.TaskDetail td = new TaskDB.TaskDetail(nextID, param1, param2, param3, param4, param5, param6, param7);

            new DbOperationTask().execute("createTask", param1, param2, param3, param4, param5);
        }
    }

    private void modifyTask() {
        String param1 = taskNameEt.getText().toString();
        String param2 = taskContentEt.getText().toString();
        String param3 = remindTimeEt.getText().toString();
        String param4 = currTid;

        new DbOperationTask().execute("updateTask", currMid, param1, param2, param3, param4);
    }

    private class DbOperationTask extends AsyncTask<String, Void, Void> {

        protected Void doInBackground(String... params) {
            String cmd = params[0];
            switch (cmd) {
                case "createTask":
                    db.create(params[1], params[2], params[3], params[4], params[5]);
                    break;
                case "updateTask":
                    db.update(params[1], params[2], params[3], params[4], params[5]);
                    break;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            switch (cmd) {
                case "addTask":
                    /*Intent intent = new Intent(getApplicationContext(), RegisterAlarmService.class);
                    intent.putExtra("cmd", "adding");
                    intent.putExtra("mid", nextID.trim());
                    intent.putExtra("taskName", taskNameEt.getText().toString());
                    intent.putExtra("remindTime", remindTimeEt.getText().toString());
                    startService(intent);*/
            }

            super.onPostExecute(aVoid);
            finish();
        }
    }


    public void selectRemindTime(View view) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        // Create a new instance of TimePickerDialog and return it
        new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                remindTimeEt.setText(hourOfDay + ":" + minute);
                Log.v("jim_alarm", hourOfDay + ":" + minute);
            }
        }, hour, minute, false).show();
    }


    public void cancel(View view) {
        finish();
    }
}
