package com.example.teamgogoal.teamgogoal;

import android.app.DatePickerDialog;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TargetActivity extends AppCompatActivity {
    AlertDialog.Builder dialog;
    AlertDialog msg=null;
    String localhost=LoginActivity.getLocalHost();
    LoginActivity.User user;
    TargetDB db;
    LinearLayout targetll;
    EditText targetNameEt,targeContentEt,startTimeEt,endTimeEt,participatorTxt;
    Button submitTargetBtn,clearTargetBtn,cannelBtn;
    View addTargetMsg;
    Spinner spinner;
    String nextID="",currID="";
    Intent intent;
    final String[] list = {"earth", "jupiter", "mars"};
    String request=null;
    SocketTrans socketTrans=LoginActivity.socketTrans;
    static Map<Integer,TargetUIStructure> targetMap =new HashMap<Integer,TargetUIStructure>();
    protected void onCreate(Bundle savedInstanceState) {
        try{
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_target);

            targetll=(LinearLayout) findViewById(R.id.taskLinearLayout);



            db= new TargetDB();
            LayoutInflater factory = LayoutInflater.from(this);
            addTargetMsg = factory.inflate(R.layout.activity_target_add_msg, null);
            dialog = new AlertDialog.Builder(TargetActivity.this);
            targetNameEt=(EditText) addTargetMsg.findViewById(R.id.targetNameTxt);
            targeContentEt=(EditText) addTargetMsg.findViewById(R.id.targetContent);
            startTimeEt=(EditText) addTargetMsg.findViewById(R.id.startTimeTxt);
            endTimeEt=(EditText) addTargetMsg.findViewById(R.id.EndTimeTxt);
            participatorTxt=(EditText) addTargetMsg.findViewById(R.id.participatorTxt);
            submitTargetBtn=(Button)addTargetMsg.findViewById(R.id.submitTargetBtn);
            clearTargetBtn=(Button)addTargetMsg.findViewById(R.id.clearMessageBtn);
            cannelBtn=(Button)addTargetMsg.findViewById(R.id.cannelBtn);
            spinner=(Spinner) addTargetMsg.findViewById(R.id.spinner);
            dialog.setView(addTargetMsg);
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    initial();
                }
            });
            user=LoginActivity.getUser();
            new DbOperationTask().execute("readTarget");


           /* Thread t=new Thread(new Runnable() {
                @Override
                public void run() {
                    String result=socketTrans.getResult();
                    Toast.makeText(TargetActivity.this,result,Toast.LENGTH_SHORT).show();
                }
            });*/
        }catch(Exception e){
            Log.v("jim",e.toString());
        }
    }
    public void onClick(View view){
        int id=view.getId();
        switch (id){
            case R.id.showAddTargetBtn:
                showTarget();
                break;
            case R.id.submitTargetBtn:
                if(currID.equals("")) addTarget();
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
            case R.id.button2:
                toRequest();
                break;
        }
    }
    protected  void toRequest(){
        if(request==null){
            Bundle bundle = getIntent().getExtras();
            request=bundle.getString("loadingRequest");
        }

        intent=new Intent();
        intent.putExtra("loadingRequest",request);
        intent.setClass(TargetActivity.this, RequestActivity.class);
        startActivity(intent);
    }

    protected void showTarget(){
        try{
            if(msg==null){
                msg=dialog.show();
            }else{
                msg.show();
            }
        }catch(Exception e){
            Log.v("jim",e.toString());
        }
    }
    protected  void addTarget(){
        try{
            if((targetNameEt.getText().toString().equals("") || targeContentEt.getText().toString().equals("") || startTimeEt.getText().toString().equals("") || endTimeEt.getText().toString().equals("") || participatorTxt.getText().toString().equals(""))){
                Toast.makeText(this,"請輸入完整資訊",Toast.LENGTH_SHORT).show();
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
                        final AlertDialog mutiItemDialog = getMutiItemDialog(new String[]{"read","update","delete"},view.getId());
                        mutiItemDialog.show();
                        return false;
                    }
                });

                tll.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        enterTaskActivity(view.getId());
                    }
                });

                ImageView img=new ImageView(this);
                img=toCircleImage(R.drawable.images,img);
                img.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                img.setAdjustViewBounds(true);
                layoutParams = new LinearLayout.LayoutParams(250, 250);
                layoutParams.setMargins(0,0,20,0);
                img.setLayoutParams(layoutParams);


                TextView txt=new TextView(this);
                String targetName=targetNameEt.getText().toString();
                txt.setText(targetName);

                tll.addView(img);
                tll.addView(txt);
                targetll.addView(tll);
                targetll.setEnabled(false);

                String param1=targetNameEt.getText().toString();
                String param2=targeContentEt.getText().toString();
                String param3=startTimeEt.getText().toString();
                String param4=endTimeEt.getText().toString();
                String param5="no";
                String param6=LoginActivity.user.account;
                String param7=spinner.getSelectedItem().toString();
                String param8=participatorTxt.getText().toString();


                nextID=nextID.trim();
                TargetDB.TargetDetail td=new TargetDB.TargetDetail(nextID,param1,param2,param3,param4,param5,param6,param7,param8);
                new DbOperationTask().execute("createTarget",nextID,param1,param2,param3,param4,param5,param6,param7,param8);

                int k=Integer.parseInt(nextID);
                tll.setId(k);
                Log.v("jim",nextID);
                TargetUIStructure targetUIS=new TargetUIStructure(td,tll,img,txt);
                targetMap.put(k,targetUIS);
                msg.dismiss();
                Toast.makeText(this,"新增成功",Toast.LENGTH_SHORT).show();
                initial();
            }

        }catch(Exception e){
            Log.v("jim1",e.toString());
        }
    }
    protected ImageView  toCircleImage(int imgId,ImageView img){
        Bitmap bitmap= BitmapFactory.decodeResource(getResources(),imgId);
        RoundedBitmapDrawable roundedBitmapDrawable= RoundedBitmapDrawableFactory.create(getResources(),bitmap);
        roundedBitmapDrawable.setCircular(true);
        img.setImageDrawable(roundedBitmapDrawable);
        return img;
    }

    protected  void initial( ){
        targetNameEt.setText("");
        targeContentEt.setText("");
        startTimeEt.setText("");
        endTimeEt.setText("");
        participatorTxt.setText("");
        submitTargetBtn.setEnabled(true);
        submitTargetBtn.setText("新增任務");
        clearTargetBtn.setEnabled(true);
        cannelBtn.setEnabled(true);
        msg.dismiss();
    }
    protected void cancel(){
        initial();
    }

    protected  void selectDate(final int txtID){
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        new DatePickerDialog(TargetActivity.this, new DatePickerDialog.OnDateSetListener() {
            int id=txtID;
            public void onDateSet(DatePicker view, int year, int month, int day) {
                String format = setDateFormat(year,month,day);
                EditText txt=(EditText) addTargetMsg.findViewById(id);
                txt.setText(format);
            }
        }, mYear,mMonth, mDay).show();

    }
    private String setDateFormat(int year,int monthOfYear,int dayOfMonth){
        return String.valueOf(year) + "-"
                + String.valueOf(monthOfYear + 1) + "-"
                + String.valueOf(dayOfMonth);
    }

    protected void fresh(Map<String,TargetDB.TargetDetail> map) {
            Iterator it = map.entrySet().iterator();

            while (it.hasNext()) {

                Map.Entry<String, TargetDB.TargetDetail> set = (Map.Entry) it.next();
                LinearLayout tll = new LinearLayout(this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(30, 20, 30, 0);
                tll.setOrientation(LinearLayout.HORIZONTAL);
                tll.setLayoutParams(layoutParams);
                tll.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        final AlertDialog mutiItemDialog = getMutiItemDialog(new String[]{"read", "update", "delete"}, view.getId());
                        mutiItemDialog.show();
                        return false;
                    }

                });

                tll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        enterTaskActivity(view.getId());
                    }
                });

                ImageView img = new ImageView(this);
                img=toCircleImage(R.drawable.images,img);
                //等比例放大縮小
                img.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                //允許img可以改變大小
                img.setAdjustViewBounds(true);
                //建立layout，並且設定大小
                layoutParams = new LinearLayout.LayoutParams(250, 250);
                layoutParams.setMargins(0, 0, 20, 0);
                //設定好的layout丟給imageview
                img.setLayoutParams(layoutParams);

                TextView txt = new TextView(this);
                txt.setGravity(Gravity.BOTTOM);

                txt.setText(set.getValue().targetName);

                tll.addView(img);
                tll.addView(txt);
                String s=set.getValue().tid.trim();
                Integer key = Integer.parseInt(s);
                tll.setId(key);

                TargetUIStructure targetUIS=new TargetUIStructure(set.getValue(),tll,img,txt);
                targetMap.put(key, targetUIS);
                try {
                    targetll.addView(tll);
                }catch(Exception e){
                    Log.v("jim12",e.toString());
                }
            }

    }
    public AlertDialog getMutiItemDialog(final String[] cmd,final int id) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //設定對話框內的項目
        builder.setItems(cmd, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog,int index){
                switch (cmd[index]){
                    case "read":
                        read(id);
                        submitTargetBtn.setEnabled(false);
                        clearTargetBtn.setEnabled(false);
                    break;
                    case "update":
                        read(id);
                        submitTargetBtn.setText("更新資料");
                        clearTargetBtn.setEnabled(false);
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
    protected  void delete(int id){
        try{
            TargetUIStructure targetUIS= targetMap.get(id);
            TargetDB.TargetDetail td=targetUIS.td;
            LinearLayout ll=targetUIS.ll;
            ((ViewGroup) ll.getParent()).removeView(ll);
            targetMap.remove(id);

            if(user.account.equals(td.auth.trim()))
                new DbOperationTask().execute("deleteParticipator_all",td.tid);
            else
                new DbOperationTask().execute("deleteParticipator",td.tid,user.account);

        }catch(Exception e){
            Log.v("jim1",e.toString());
        }


    }
    protected  void read(int id){
        TargetUIStructure targetUIS= targetMap.get(id);
        TargetDB.TargetDetail td=targetUIS.td;
        targetNameEt.setText(td.targetName);
        targeContentEt.setText(td.targetContent);
        startTimeEt.setText(td.startTime);
        endTimeEt.setText(td.endTime);
        participatorTxt.setText(td.participator);
        int pos=-1;
        for(int i=0;i<list.length;i++){
            if(list[i].equals(td.planet)) {
                pos=i;

                break;
            }
        }
        pos-=1;
        spinner.setSelection(pos);
        currID=Integer.toString(id).trim();

        showTarget();
    }

    protected  void update(){
        String param1=targetNameEt.getText().toString();
        String param2=targeContentEt.getText().toString();
        String param3=startTimeEt.getText().toString();
        String param4=endTimeEt.getText().toString();
        String param5="no";
        String param6=LoginActivity.user.account;
        String param7=spinner.getSelectedItem().toString();
        String param8=participatorTxt.getText().toString();

        int key=Integer.parseInt(currID.trim());
        TargetUIStructure targetUIS= targetMap.get(key);
        targetUIS.td.targetName=param1;
        targetUIS.td.targetContent=param2;
        targetUIS.td.startTime=param3;
        targetUIS.td.endTime=param4;
        targetUIS.td.planet=param7;
        targetUIS.td.participator=param8;
        targetUIS.txtName.setText(param1);

        //String param8=participatorTxt.getText().toString();
        new DbOperationTask().execute("updateTarget",currID,param1,param2,param3,param4,param5,param6,param7,param8);
        currID="";
        msg.dismiss();
    }



    //background run
    private class DbOperationTask  extends AsyncTask<String, Void, Void> {
        protected Void doInBackground(String... params) {
            String cmd = params[0];
            switch (cmd) {
                case "readTarget":
                    final Map<String,TargetDB.TargetDetail> t;
                     t=db.readTarget();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            try{
                                fresh(t);
                            }catch(Exception e) {
                                Log.v("jim11", e.toString());
                            }
                        }
                    });

                    nextID=db.targetIndex();
                    break;
                case "createTarget":
                    db.createTarget(params[1],params[2],params[3],params[4],params[5],params[6],params[7],params[8]);
                    db.createParticipator(params[1],params[9]);
                    nextID=db.targetIndex();
                    break;
                case "updateTarget":
                    db.updateTarget(params[1],params[2],params[3],params[4],params[5],params[6],params[7],params[8],params[9]);
                    break;
                case "deleteParticipator_all":
                    db.deleteTargetAll(params[1]);
                    break;
                case "deleteParticipator":
                    db.deleteParticipator(params[1],params[2]);
                    break;


            }
            return null;
        }
    }
    public void enterTaskActivity(int id){
        try{
            String tid=Integer.toString(id).trim();
            TargetUIStructure targetUIS=targetMap.get(id);

            Intent intent = new Intent();
            intent.putExtra("tid",targetUIS.td.tid.trim());
            intent.putExtra("t_name",targetUIS.td.targetName.trim());

            //intent.putExtras(bundle);
            intent.setClass(TargetActivity.this, TaskActivity.class);
            startActivity(intent);
        }catch(Exception e){
            Log.v("jim_enterTaskActivity",e.toString());
        }
    }
    public class TargetUIStructure implements Serializable {
        TargetDB.TargetDetail td;
        LinearLayout ll;
        ImageView img;
        TextView txtName;
        TargetUIStructure(TargetDB.TargetDetail td,LinearLayout ll,ImageView img,TextView txtNam){
            this.td=td;
            this.ll=ll;
            this.img=img;
            this.txtName=txtNam;
        }
    }

    //-----------------帥哥建興開始-----------------
    public void checkReview(View view) {
        Intent intent = new Intent();
        intent.setClass(this,Review.class);
        startActivity(intent);
    }
}
