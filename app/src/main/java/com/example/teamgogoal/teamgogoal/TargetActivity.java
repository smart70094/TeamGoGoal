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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
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
    AlertDialog msg=null;
    String localhost=LoginActivity.getLocalHost();
    LoginActivity.User user;
    TargetDB db;
    LinearLayout targetll;
    EditText targetNameEt,targeContentEt,startTimeEt,endTimeEt,participatorTxt;
    Button submitTargetBtn,clearTargetBtn,cannelBtn;
    ImageView targetProfilePicture;
    View addTargetMsg;
    Spinner spinner;
    String nextID="",currID="";
    Intent intent;

    //8/20:AutoCompleteTextView
    MultiAutoCompleteTextView mactv;

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

            // Date:8/20-監聽文字變更開始
            mactv = (MultiAutoCompleteTextView)addTargetMsg.findViewById(R.id.multiAutoCompleteTextView);
            mactv.setDropDownHeight(200); //設定高度
            mactv.setThreshold(1);
            mactv.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
            // 初始化搜尋資料
            initmactv();

            // Date:8/20-監聽文字變更結束


            //participatorTxt=(EditText) addTargetMsg.findViewById(R.id.participatorTxt);
            submitTargetBtn=(Button)addTargetMsg.findViewById(R.id.submitTargetBtn);
            clearTargetBtn=(Button)addTargetMsg.findViewById(R.id.clearMessageBtn);
            cannelBtn=(Button)addTargetMsg.findViewById(R.id.cannelBtn);
            targetProfilePicture=(ImageView)findViewById(R.id.targetProfilePicture);
            spinner=(Spinner) addTargetMsg.findViewById(R.id.spinner);
            dialog.setView(addTargetMsg);
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    initial();
                }
            });
            user=LoginActivity.getUser();



        }catch(Exception e){
            Log.v("jim",e.toString());
        }
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub

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
    protected  void loading(){
        synchronized(this) {
            new DbOperationTask().execute("readTarget");

            //帥哥峻禾部分
            String imageUrl = localhost + "profile picture/" + user.uid;
            new AsyncTask<String, Void, Bitmap>()
            {
                @Override
                protected Bitmap doInBackground(String... params)
                {
                    String url = params[0];
                    return getBitmapFromURL(url);
                }

                @Override
                protected void onPostExecute(Bitmap result)
                {
                    targetProfilePicture.setImageBitmap (result);
                    super.onPostExecute(result);
                }
            }.execute(imageUrl);
        }
    }

    //帥哥峻禾部分
    public Bitmap getBitmapFromURL(String imageUrl){
        try
        {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public void onClick(View view){
        int id=view.getId();
        switch (id){
            case R.id.showAddTargetBtn:
                showTarget();
                //participatorTxt.setText(user.account);
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
            case R.id.loadBtn:
                loading();
                break;
            //帥哥峻禾部分
            case R.id.editProfile:
                toEditProfile();
                break;
        }
    }
    //帥哥峻禾部分
    private void toEditProfile() {
        intent=new Intent();
        intent.setClass(TargetActivity.this, EditProfile.class);
        startActivity(intent);
    }
    protected  void toRequest(){
        intent=new Intent();
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
                        Integer id=view.getId();
                        String auth=targetMap.get(id).td.auth.trim();
                        if(auth.equals(user.account)){
                            final AlertDialog mutiItemDialog = getMutiItemDialog(new String[]{"read","update","delete"},view.getId());
                            mutiItemDialog.show();
                        }else{
                            submitTargetBtn.setEnabled(false);
                            clearTargetBtn.setEnabled(false);
                            read(id);
                        }
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

                //帥哥峻禾部分
                socketTrans.setParams("initial_target",nextID,param4);
                socketTrans.send(socketTrans.getParams());

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
                String s = set.getValue().tid.trim();
                Integer key = Integer.parseInt(s);
                if(!targetMap.containsKey(key)) {
                    LinearLayout tll = new LinearLayout(this);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(30, 20, 30, 0);
                    tll.setOrientation(LinearLayout.HORIZONTAL);
                    tll.setLayoutParams(layoutParams);
                    tll.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            Integer id=view.getId();
                            String auth=targetMap.get(id).td.auth.trim();
                            if(auth.equals(user.account)){
                                final AlertDialog mutiItemDialog = getMutiItemDialog(new String[]{"read","update","delete"},view.getId());
                                mutiItemDialog.show();
                            }else{
                                submitTargetBtn.setEnabled(false);
                                clearTargetBtn.setEnabled(false);
                                read(id);
                            }
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
                    img = toCircleImage(R.drawable.images, img);
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

                    tll.setId(key);

                    TargetUIStructure targetUIS = new TargetUIStructure(set.getValue(), tll, img, txt);
                    targetMap.put(key, targetUIS);
                    try {
                        targetll.addView(tll);
                    } catch (Exception e) {
                        Log.v("jim12", e.toString());
                    }
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
        String param9=targetUIS.td.participator;
        targetUIS.td.targetName=param1;
        targetUIS.td.targetContent=param2;
        targetUIS.td.startTime=param3;
        targetUIS.td.endTime=param4;
        targetUIS.td.planet=param7;
        targetUIS.td.participator=param8;
        targetUIS.txtName.setText(param1);

        //String param8=participatorTxt.getText().toString();
        new DbOperationTask().execute("updateTarget",currID,param1,param2,param3,param4,param5,param6,param7,param8,param9);
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
                    db.updateTarget(params[1],params[2],params[3],params[4],params[5],params[6],params[7],params[8],params[9],params[10]);
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

    // Date:8/20 尋找帳號開始---------------------------
    private void initmactv() {
        String phpurl = localhost + "searchID.php";
        new TransTask().execute(phpurl);
    }

    class TransTask extends AsyncTask<String, Void, String> {
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
            Log.d("FFFF",Integer.toString(list.size()));
            return list;
        }
    }

    public  void initListView(List list){
        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,list);
        mactv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }



    // Date:8/20 尋找帳號結束---------------------------
}
