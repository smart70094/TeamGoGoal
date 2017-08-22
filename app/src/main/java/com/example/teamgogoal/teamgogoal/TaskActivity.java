package com.example.teamgogoal.teamgogoal;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TaskActivity extends AppCompatActivity {

    TaskDB db;
    LinearLayout msgll,taskll;
    TextView taskTitle;
    EditText taskNameTxt,taskContent,remindTimeTxt,cheerEt;
    Button submit,submitTaskBtn,clearTaskMessageBtn,cannelTaskBtn;
    Map<Integer,TaskUIStructure> taskMap=new HashMap<Integer,TaskUIStructure>();
    Spinner spinner;
    String currTid="",nextID="",currID="";
    LoginActivity.User user;
    AlertDialog.Builder cheerDialog;
    AlertDialog msg=null;
    AlertDialog dialog=null;
    View addTaskMsg;
    final String[] list = {"earth", "jupiter", "mars"};
    SocketTrans socketTrans=LoginActivity.socketTrans;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
       try{
           Bundle bundle = getIntent().getExtras();
           user=LoginActivity.getUser();
           currTid=bundle.getString("tid");
           String t_name=bundle.getString("t_name");



           db=new TaskDB(LoginActivity.getLocalHost()+"readmission.php");

           LayoutInflater factory = LayoutInflater.from(this);
           View addTargetMsg = factory.inflate(R.layout.activity_task_add_msg, null);
           taskll=(LinearLayout)findViewById(R.id.taskll);

           taskNameTxt=(EditText) addTargetMsg.findViewById(R.id.taskNameTxt);
           taskContent=(EditText) addTargetMsg.findViewById(R.id.taskContent);
           spinner=(Spinner) addTargetMsg.findViewById(R.id.spinner);
           ArrayAdapter<String> lunchList = new ArrayAdapter<>(TaskActivity.this,
                   android.R.layout.simple_spinner_dropdown_item,
                   list);
           spinner.setAdapter(lunchList);
           remindTimeTxt=(EditText)addTargetMsg.findViewById(R.id.remindTimeTxt);
           submitTaskBtn=(Button)addTargetMsg.findViewById(R.id.submitTargetBtn);
           cannelTaskBtn=(Button)addTargetMsg.findViewById(R.id.cannelBtn);
           clearTaskMessageBtn=(Button)addTargetMsg.findViewById(R.id.clearMessageBtn);

           new DbOperationTask().execute("read");

           LayoutInflater factoryCheerMsg = LayoutInflater.from(this);
           View cheerMsg = factoryCheerMsg.inflate(R.layout.activity_cheer_msg, null);
           submit=(Button) cheerMsg.findViewById(R.id.cheerBtn);
           cheerEt=(EditText) cheerMsg.findViewById(R.id.cheerEt);
           cheerDialog = new AlertDialog.Builder(TaskActivity.this);
           cheerDialog.setView(cheerMsg);
           taskTitle=(TextView)findViewById(R.id.taskTitle);
           taskTitle.setText(t_name);
       }catch(Exception e){
           Log.v("jim_Task_onCreate",e.toString());
       }
    }

    protected void showCheerMsg(){
        try{
            if(msg==null){
                msg=cheerDialog.show();
            }else{
                msg.show();
            }
        }catch(Exception e){
            Log.v("jim",e.toString());
        }
    }

    private class DbOperationTask  extends AsyncTask<String, Void, Void> {
        protected Void doInBackground(String... params) {
            String cmd = params[0];
            switch (cmd) {
                case "read":
                    final Map<String,TaskDB.TaskDetail> t;
                    t=db.read(currTid);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            try{
                                fresh(t);
                            }catch(Exception e) {
                                Log.v("jim11", e.toString());
                            }
                        }
                    });

                    nextID=db.taskIndex();
                    break;
                case "createTask":
                    db.create(params[1],params[2],params[3],params[4],params[5],params[6],params[7],params[8]);
                    nextID=db.taskIndex();
                    break;
                case "delete":
                    db.delete(params[1]);
                    break;
                case "targetCount":
                    // tempID=db.targetCount();
                    break;
                case "updateTask":
                    db.update(params[1],params[2],params[3],params[4],params[5],params[6]);
                    break;
            }
            return null;
        }

    }
    protected  void delete(int id){
        try{

            TaskActivity.TaskUIStructure taskUIS=taskMap.get(id);
            TaskDB.TaskDetail td=taskUIS.td;
            LinearLayout ll=taskUIS.ll;
            ((ViewGroup) ll.getParent()).removeView(ll);
            new TaskActivity.DbOperationTask().execute("delete",td.mid);
            taskMap.remove(id);
        }catch(Exception e){
            Log.v("jim1",e.toString());
        }
    }
    protected  void update(){
        String param1=taskNameTxt.getText().toString();
        String param2=taskContent.getText().toString();
        String param3=remindTimeTxt.getText().toString();
        String param4=currTid;
        String param5=spinner.getSelectedItem().toString();

        int key=Integer.parseInt(currID.trim());
        TaskActivity.TaskUIStructure taskUIS=taskMap.get(key);
        taskUIS.td.missionName=param1;
        taskUIS.td.missionContent=param2;
        taskUIS.td.remindTime=param3;
        taskUIS.td.mid=param4;
        taskUIS.td.planet=param5;
        taskUIS.txtName.setText(param1);
        taskUIS.txtName.setText(param1);

        new DbOperationTask().execute("updateTask",currID,param1,param2,param3,param4,param5);
        currID="";
        msgll.setVisibility(View.INVISIBLE);
    }
    protected  void addTask(){
        try{
            if(taskNameTxt.getText().toString().equals("")|| taskContent.getText().toString().equals("")||remindTimeTxt.getText().toString().equals("")){
                Toast.makeText(this,"請輸入完整資料",Toast.LENGTH_SHORT).show();
            }else{
                LinearLayout tll=new LinearLayout(this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(30, 20, 30, 0);
                tll.setOrientation(LinearLayout.HORIZONTAL);
                tll.setLayoutParams(layoutParams);
                tll.setOnLongClickListener(new View.OnLongClickListener(){
                    @Override
                    public boolean onLongClick(View view) {
                        final AlertDialog mutiItemDialog = getMutiItemDialog(new String[]{"read","update","delete","撰寫回顧"},view.getId());
                        mutiItemDialog.show();
                        return false;
                    }
                });

                tll.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        int key=view.getId();
                        TaskUIStructure taskUIS=taskMap.get(key);
                        socketTrans.setParams("registerCheer",user.account,taskUIS.td.auth);
                        socketTrans.send(socketTrans.getParams());
                        showCheerMsg();
                    }
                });

                ImageView img=new ImageView(this);
                img.setBackgroundResource(R.drawable.images);
                img.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                img.setAdjustViewBounds(true);
                layoutParams = new LinearLayout.LayoutParams(50, 50);
                layoutParams.setMargins(0,0,20,0);
                img.setLayoutParams(layoutParams);

                TextView txt=new TextView(this);
                EditText editTxt=(EditText) findViewById(R.id.taskNameTxt) ;
                txt.setText(editTxt.getText());

                ImageButton imgbtn = new ImageButton(this);
                imgbtn.setImageResource(R.drawable.common_google_signin_btn_icon_dark);


                tll.addView(img);
                tll.addView(txt);
                tll.addView(imgbtn);
                taskll.addView(tll);
                msgll.setVisibility(View.INVISIBLE);
                taskll.setEnabled(false);

                String param1=taskNameTxt.getText().toString();
                String param2=taskContent.getText().toString();
                String param3=remindTimeTxt.getText().toString();
                String param4=currTid;
                String param5=spinner.getSelectedItem().toString();
                String param6="no";
                String param7=user.account;

                nextID=nextID.trim();
                TaskDB.TaskDetail td=new TaskDB.TaskDetail(nextID,param1,param2,param3,param4,param5,param6,param7);

                new TaskActivity.DbOperationTask().execute("createTask",nextID,param1,param2,param3,param4,param5,param6,param7);
                int k=Integer.parseInt(nextID.trim());
                tll.setId(k);
                TaskUIStructure targetUIS=new TaskUIStructure(td,tll,img,txt);
                taskMap.put(k,targetUIS);
                Toast.makeText(this,"新增任務成功",Toast.LENGTH_SHORT).show();
            }
        }catch(Exception e){
            Log.v("jim1",e.toString());
        }
    }


    protected void fresh(Map<String,TaskDB.TaskDetail> map) {
        try{
            Iterator it=map.entrySet().iterator();

            while(it.hasNext()){

                Map.Entry<String,TaskDB.TaskDetail> set=(Map.Entry) it.next();

                LinearLayout tll=new LinearLayout(this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(30, 20, 30, 0);
                tll.setOrientation(LinearLayout.HORIZONTAL);
                tll.setLayoutParams(layoutParams);
                tll.setOnLongClickListener(new View.OnLongClickListener(){
                    @Override
                    public boolean onLongClick(View view) {
                        final AlertDialog mutiItemDialog = getMutiItemDialog(new String[]{"peek","update","delete","撰寫回顧"},view.getId());
                        mutiItemDialog.show();
                        return false;
                    }

                });
                tll.setOnClickListener(new View.OnClickListener(){
                    public void onClick(View view){

                        currID=Integer.toString(view.getId());
                        showCheerMsg();
                    }
                });

                ImageView img=new ImageView(this);
                //img.setBackgroundResource(R.drawable.images);
                img=toCircleImage(R.drawable.images,img);
                //等比例放大縮小
                img.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                //允許img可以改變大小
                img.setAdjustViewBounds(true);
                //建立layout，並且設定大小
                layoutParams = new LinearLayout.LayoutParams(50, 50);
                layoutParams.setMargins(0,0,20,0);
                //設定好的layout丟給imageview
                img.setLayoutParams(layoutParams);

                TextView txt=new TextView(this);
                txt.setGravity(Gravity.BOTTOM);
                txt.setText(set.getValue().missionName+"      執行者:"+set.getValue().auth);
                Integer key=Integer.parseInt(set.getValue().mid);
                //--------------帥哥建興的------------
                ImageButton imgbtn = new ImageButton(this);
                imgbtn.setImageResource(R.drawable.common_google_signin_btn_icon_dark);
                final Integer _key = key;
                imgbtn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        completeTask(_key);
                    }
                });
                tll.addView(img);
                tll.addView(txt);
                tll.addView(imgbtn);

                tll.setId(key);
                TaskUIStructure taskUIS=new TaskUIStructure(set.getValue(),tll,img,txt);
                taskMap.put(key,taskUIS);
                taskll.addView(tll);
            }
        }catch(Exception e){
            Log.v("jim",e.toString());
        }

    }



    public AlertDialog getMutiItemDialog(final String[] cmd,final int id) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //設定對話框內的項目
        builder.setItems(cmd, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog,int index){
                switch (cmd[index]){
                    case "peek":
                        peek(id);
                        break;
                    case "update":
                        peek(id);
                        currID=Integer.toString(id).trim();
                        break;
                    case "delete":
                        delete(id);
                        break;
                }
            }
        });
        return builder.create();
    }

    protected void peek(int id){
        msgll.setVisibility(View.VISIBLE);
        TaskUIStructure taskUIS=taskMap.get(id);


        taskNameTxt.setText(taskUIS.td.missionName);
        taskContent.setText(taskUIS.td.missionContent);
        remindTimeTxt.setText(taskUIS.td.remindTime);
        int pos=-1;
        for(int i=0;i<list.length;i++){
            if(taskUIS.td.planet.equals(list[i])){
                pos=i;
                break;
            }
        }
        pos-=1;
        spinner.setSelection(pos);
    }

    public void onClick(View view){
        int id=view.getId();
        switch (id){
            case R.id.showAddTaskBtn:
                showmsg();
                break;
            case R.id.selectRemindTime:
                alarm();
                break;
            case R.id.cannelBtn:
                cancel();
                break;
            case R.id.submitTaskBtn:
                if(currID.equals("")) addTask();
                else update();
                break;
            case R.id.cheerBtn:
                cheerSubmit();
                break;
        }
    }
    public void cheerSubmit(){
        try{
            String msgStr=cheerEt.getText().toString();
            Integer key=Integer.parseInt(currID.trim());
            TaskUIStructure taskUIS=taskMap.get(key);
            Log.v("jim_cheerSubmit",taskUIS.td.auth);
            socketTrans.setParams("register_cheer",user.account,"123");
            socketTrans.send(socketTrans.getParams());
            msg.dismiss();
        }catch(Exception e){
            Log.v("jim_cheerSubmit",e.toString());
        }
    }
    protected void showmsg(){
        View write_record;

        write_record = LayoutInflater.from(TaskActivity.this).inflate(R.layout.activity_task_add_msg, null);
        dialog = new AlertDialog.Builder(TaskActivity.this)
                .setView(write_record)
                .show();
    }
    protected void alarm(){
       final EditText remindTime=(EditText) findViewById(R.id.remindTimeTxt);
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        // Create a new instance of TimePickerDialog and return it
        new TimePickerDialog(TaskActivity.this, new TimePickerDialog.OnTimeSetListener(){
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                remindTime.setText(hourOfDay + ":" + minute);
            }
        }, hour, minute, false).show();
    }
    protected  void initial(){
        taskNameTxt.setText("");
        taskContent.setText("");
        remindTimeTxt.setText("");
        spinner.setSelection(0);
        submitTaskBtn.setEnabled(true);
        clearTaskMessageBtn.setEnabled(true);
        cannelTaskBtn.setEnabled(true);
    }
    protected  void cancel(){
        initial();
        dialog.dismiss();
    }
    public class TaskUIStructure{
        TaskDB.TaskDetail td;
        LinearLayout ll;
        ImageView img;
        TextView txtName;
        TaskUIStructure(TaskDB.TaskDetail td,LinearLayout ll,ImageView img,TextView txtNam){
            this.td=td;
            this.ll=ll;
            this.img=img;
            this.txtName=txtNam;
        }
    }

    protected ImageView  toCircleImage(int imgId,ImageView img){
        Bitmap bitmap= BitmapFactory.decodeResource(getResources(),imgId);
        RoundedBitmapDrawable roundedBitmapDrawable= RoundedBitmapDrawableFactory.create(getResources(),bitmap);
        roundedBitmapDrawable.setCircular(true);
        img.setImageDrawable(roundedBitmapDrawable);
        return img;
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
                Log.d("hhhhhhhhhhh",mid);
                String phpurl = LoginActivity.getLocalHost() + "updateTaskState.php?mid=" + mid;
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

    //--------------資料庫連接 Code---------------------//

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
        }
    }

    //--------------資料庫連接 Code---------------------//

}
