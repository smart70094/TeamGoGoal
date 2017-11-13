package com.example.teamgogoal.teamgogoal;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;

public class TargetEventAcivity extends AppCompatActivity {
    Bundle bundle;
    String cmd;
    Button TargetEventBtn;
    EditText targetNameEt, targeContentEt, startTimeEt, endTimeEt;
    TargetDB db;
    ProgressDialog progressdialog;
    SocketTrans socketTrans = LoginActivity.socketTrans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_target_event_acivity);
        bundle = getIntent().getExtras();

        cmd = bundle.getString("cmd");

        db=new TargetDB();
        
        targetNameEt = (EditText) findViewById(R.id.targetNameEt);
        targeContentEt = (EditText) findViewById(R.id.targeContentEt);
        startTimeEt = (EditText) findViewById(R.id.startTimeEt);
        endTimeEt = (EditText) findViewById(R.id.endTimeEt);

        switch (cmd){
            case "modifyTarget":
            case "readTarget" :
                readTarget();
                initView();
                break;
        }




        TargetEventBtn = (Button) findViewById(R.id.TargetEventBtn);
        TargetEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                switch (cmd) {
                    case "readTarget":
                        finish();
                        break;

                    case "addTarget":
                        addTarget();
                        break;

                    case "modifyTarget":
                        modifyTarget();
                        break;
                }
            }
        });


    }



    private void readTarget() {
        targetNameEt.setText(bundle.getString("targetName").trim());
        targeContentEt.setText(bundle.getString("targetContent").trim());
        startTimeEt.setText(bundle.getString("startTime").trim());
        endTimeEt.setText(bundle.getString("endTime").trim());
    }

    private void initView() {
        switch (cmd){
            case "readTarget":
                targetNameEt.setEnabled(false);
                targeContentEt.setEnabled(false);
                startTimeEt.setEnabled(false);
                endTimeEt.setEnabled(false);
                break;
        }
    }

    private void addTarget() {
        try {
            if ((targetNameEt.getText().toString().equals("") || targeContentEt.getText().toString().equals("") || startTimeEt.getText().toString().equals("") || endTimeEt.getText().toString().equals(""))) {
                Toast.makeText(this, "請輸入完整資訊", Toast.LENGTH_SHORT).show();
            } else {
                String param1 = targetNameEt.getText().toString();
                String param2 = targeContentEt.getText().toString();
                String param3 = startTimeEt.getText().toString();
                String param4 = endTimeEt.getText().toString();
                String param5 = "N";
                String param6 = LoginActivity.user.account;

                TargetDB.TargetDetail td = new TargetDB.TargetDetail("", param1, param2, param3, param4, param5, param6, "0", "0");
                new DbOperationTask().execute("createTarget", param1, param2, param3, param4, param6);

                //帥哥峻禾部分
                /*socketTrans.setParams("initial_target", nextID, param4);
                socketTrans.send();*/

            }

        } catch (Exception e) {
            Log.v("jim1", e.toString());
        }
    }

    private void modifyTarget() {

        String tid = bundle.getString("tid").trim();
        String param1 = targetNameEt.getText().toString();
        String param2 = targeContentEt.getText().toString();
        String param3 = startTimeEt.getText().toString();
        String param4 = endTimeEt.getText().toString();


        new DbOperationTask().execute("updateTarget", tid, param1, param2, param3, param4);
    }

    public void selectStartTime(View view) {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int month, int day) {
                String format = setDateFormat(year, month, day);
                startTimeEt.setText(format);
            }
        }, mYear, mMonth, mDay).show();
    }

    public void selectEndTime(View view) {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int month, int day) {
                String format = setDateFormat(year, month, day);
                endTimeEt.setText(format);
            }
        }, mYear, mMonth, mDay).show();
    }

    private String setDateFormat(int year, int monthOfYear, int dayOfMonth) {
        return String.valueOf(year) + "-"
                + String.valueOf(monthOfYear + 1) + "-"
                + String.valueOf(dayOfMonth);
    }


    private class DbOperationTask extends AsyncTask<String, Void, Void> {


        protected Void doInBackground(String... params) {
            String cmd = params[0];
            String result = cmd + ",";
            switch (cmd) {
                case "createTarget":
                    try {
                        db.createTarget(params[1], params[2], params[3], params[4], params[5]);
                    } catch (Exception e) {
                        Log.v("jim_createTarget:", e.toString());
                    }
                    break;
                case "updateTarget":
                    db.updateTarget(params[1], params[2], params[3], params[4], params[5]);
                    break;
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            finish();
        }
    }
}
