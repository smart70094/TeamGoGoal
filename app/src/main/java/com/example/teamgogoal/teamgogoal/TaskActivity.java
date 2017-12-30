package com.example.teamgogoal.teamgogoal;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class TaskActivity extends AppCompatActivity {

    TargetDB targetdb;
    TaskDB db;
    TextView TargetTitle, dreamContext;
    EditText cheerEt;
    Button submit;
    Map<Integer, TaskDB.TaskDetail> taskMap = new HashMap<Integer, TaskDB.TaskDetail>();
    String currTid = "", nextID = "", currID = "", targetName = "", dream = "";
    LoginActivity.User user;
    AlertDialog.Builder cheerDialog;
    AlertDialog msg = null, dialog = null;
    SocketTrans socketTrans = LoginActivity.socketTrans;
    boolean hasTask = false;
    final int FUNC_MEMBER = 1;
    //進度條
    CircularProgressBar circularProgressBar;
    int percentage;
    //團隊成員大頭貼
    public static HashMap<String, String> member;
    public static HashMap<String, String> inviteMember;

    List<HashMap<String, String>> taskDate = new ArrayList<>();
    private Task_ListAdapter task_listAdapter;
    ListView task_listview;
    int map_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);


        try {
            Bundle bundle = getIntent().getExtras();
            user = LoginActivity.getUser();
            currTid = bundle.getString("tid");
            targetName = bundle.getString("targetName");
            db = new TaskDB(LoginActivity.getLocalHost() + "readmission.php");

            targetdb = new TargetDB();
            initMemberPhoto();
            initInviteMemberPhoto();


            TargetTitle = (TextView) findViewById(R.id.TargetTitle);
            TargetTitle.setText(targetName);


            dreamContext = (TextView) findViewById(R.id.dreamContext);
            dream = dreamContext.getText().toString();
            // need to set dreamContext


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


    //------團隊成員大頭貼載入------//

    private void initMemberPhoto() throws JSONException, ExecutionException, InterruptedException {
        member = new HashMap<>();
        member = new MemberTransTask().execute().get();
    }

    private class MemberTransTask extends AsyncTask<String, Void, HashMap<String, String>> {
        @Override
        protected HashMap<String, String> doInBackground(String... params) {

            HashMap<String, String> member = new HashMap<>();

            try {
                member = targetdb.readParticipator(currTid);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return member;
        }

    }

    private void initInviteMemberPhoto() throws JSONException, ExecutionException, InterruptedException {
        String phpurl = LoginActivity.getLocalHost() + "searchInviteMember.php?tid=" + currTid;
        String invite_result = new InviteMemberTransTask().execute(phpurl).get();
        inviteMember = new HashMap<>();
        inviteMember = InviteMember_parseJSON(invite_result);
    }

    private class InviteMemberTransTask extends AsyncTask<String, Void, String> {
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
    }

    private HashMap<String, String> InviteMember_parseJSON(String s) {
        HashMap<String, String> hashmap = new HashMap<>();
        try {
            JSONArray array = new JSONArray(s);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                hashmap.put(obj.getString("subject"),obj.getString("uid"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return hashmap;
    }


    private class PhotoTransTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);
                return bitmap;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    protected Drawable toCircleImage(Bitmap bitmap) {

        //原图宽度
        int bitmapWidth = bitmap.getWidth();
        //原图高度
        int bitmapHeight = bitmap.getHeight();

        //转换为正方形后的宽高
        int bitmapSquareWidth = Math.min(bitmapWidth, bitmapHeight);

        //最终图像的宽高
        int newBitmapSquareWidth = bitmapSquareWidth;

        Bitmap roundedBitmap = Bitmap.createBitmap(newBitmapSquareWidth, newBitmapSquareWidth, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(roundedBitmap);
        int x = bitmapSquareWidth - bitmapWidth;
        int y = bitmapSquareWidth - bitmapHeight;

        //裁剪后图像,注意X,Y要除以2 来进行一个中心裁剪
        canvas.drawBitmap(bitmap, x / 2, y / 2, null);

        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), roundedBitmap);
        roundedBitmapDrawable.setCircular(true);
        return roundedBitmapDrawable;

    }


    @Override
    protected void onResume() {
        super.onResume();
        loading();
    }

    protected void loading() {
        synchronized (this) {
            taskMap.clear();
            taskDate.clear();
            new DbOperationTask().execute("read");
            new DreamOpeartionThread().execute("loading", currTid, user.account);
        }
    }


    protected void fresh(Map<String, TaskDB.TaskDetail> map) {
        Iterator it = map.entrySet().iterator();
        boolean hasDream = false;
        while (it.hasNext()) {

            Map.Entry<String, TaskDB.TaskDetail> set = (Map.Entry) it.next();

            String s = set.getValue().mid.trim();
            Integer key = Integer.parseInt(s);
            if (!taskMap.containsKey(key)) {
                HashMap<String, String> tk_hashmap = new HashMap<>();
                tk_hashmap.put("mid", s);
                tk_hashmap.put("personal_photo", "null");
                tk_hashmap.put("missionName", set.getValue().missionName);
                tk_hashmap.put("state", set.getValue().state);
                tk_hashmap.put("auth", set.getValue().auth);
                tk_hashmap.put("authID", set.getValue().authID);

                taskDate.add(tk_hashmap);
                taskMap.put(key, set.getValue());

                if (!hasDream)
                    if (set.getValue().auth.equals(user.account) && !set.getValue().dream.equals("null"))
                        hasDream = true;

                if (!hasTask)
                    if (set.getValue().auth.equals(user.account))
                        hasTask = true;

            }
        }

        task_listAdapter = new Task_ListAdapter(this);
        task_listAdapter.setData(taskDate);

        task_listview.setAdapter(task_listAdapter);

        // 任務項目-長按事件:任務詳細事項(完成、詳細、修改、刪除)
        task_listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                map_id = i;
                final int id = Integer.valueOf(taskDate.get(i).get("mid"));
                String auth = taskMap.get(id).auth.trim();
                if (auth.equals(user.account.trim())) {

                    View dialog_view = LayoutInflater.from(TaskActivity.this).inflate(R.layout.task_selector, null);


                    Button completeTask = (Button) dialog_view.findViewById(R.id.completeTask);
                    Button readTask = (Button) dialog_view.findViewById(R.id.readTask);
                    Button modifyTask = (Button) dialog_view.findViewById(R.id.modifyTask);
                    Button deleteTask = (Button) dialog_view.findViewById(R.id.deleteTask);


                    completeTask.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                            completeTask(id);
                        }
                    });


                    readTask.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                            showTaskEvent(id, "readTask");
                        }
                    });

                    modifyTask.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                            showTaskEvent(id, "modifyTask");
                        }
                    });

                    deleteTask.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                            delete(id);
                        }
                    });
                    dialog = new AlertDialog.Builder(TaskActivity.this, R.style.hitStyle).setView(dialog_view).create();
                    dialog.show();

                } else {
                    showTaskEvent(id, "readTask");
                }
                return true;
            }
        });
        if (!hasDream) {
            Toast.makeText(this, "快去輸入你的夢想藍圖吧！", Toast.LENGTH_LONG).show();
            //process

            //
        }
    }


    //------左上返回鍵------//
    public void cancel(View view) {
        finish();
    }

    //------跳轉詳細任務資訊------//
    protected void showTaskEvent(int id, String cmd) {
        TaskDB.TaskDetail td = taskMap.get(id);

        Intent intent = new Intent();
        intent.setClass(this, TaskEventActivity.class);

        intent.putExtra("cmd", cmd);
        intent.putExtra("targetName", targetName);
        intent.putExtra("tid", currTid);
        intent.putExtra("mid", Integer.toString(id).trim());
        intent.putExtra("taskName", td.missionName);
        intent.putExtra("taskContent", td.missionContent);
        intent.putExtra("remindTime", td.remindTime);

        startActivity(intent);
    }

    // ------刪除任務------//
    protected void delete(int id) {
        try {
            TaskDB.TaskDetail td = taskMap.get(id);
            new TaskActivity.DbOperationTask().execute("delete", td.mid);
            taskMap.remove(id);
            taskDate.remove(map_id);
            task_listAdapter.notifyDataSetChanged();

            Intent intent = new Intent(getApplicationContext(), RegisterAlarmService.class);
            intent.putExtra("cmd", "cancel");
            intent.putExtra("mid", currID.trim());
            startService(intent);
        } catch (Exception e) {
            Log.v("jim1", e.toString());
        }
    }

    public void requestMessage(View view) {
        /*String participator="";
        */
        String participator = "456-789";
        socketTrans.setParams("requestMessage", participator, currID);
        socketTrans.send();
    }


    //------編輯藍圖------//
    public void writeDream(View view) {

        View dialog_view;

        dialog_view = LayoutInflater.from(this).inflate(R.layout.dream_dialog, null);


        final EditText dream_context = dialog_view.findViewById(R.id.dream_context);
        Button confirmDream = dialog_view.findViewById(R.id.confirmDream);

        if (dream.equals("")) {
            dream_context.setText("");
        } else {
            dream_context.setText(dreamContext.getText().toString());
        }

        confirmDream.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dream = dream_context.getText().toString();
                dreamContext.setText(dream);
                new DreamOpeartionThread().execute("update", currTid, user.account, dreamContext.getText().toString());
                dialog.dismiss();
                showHit("1","藍圖修改成功！");
            }
        });

        dialog = new AlertDialog.Builder(this, R.style.hitStyle).setView(dialog_view).create();
        dialog.show();
        Window dialogWindow = dialog.getWindow();
        WindowManager m = this.getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高度
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        p.height = (int) (d.getHeight() * 0.7); // 高度设置为屏幕的0.6，根据实际情况调整
        p.width = (int) (d.getWidth() * 0.8); // 宽度设置为屏幕的0.65，根据实际情况调整
        dialogWindow.setAttributes(p);
    }

    //------顯示提示窗------//
    private void showHit(String numBtn,String context) {
        Hit hit = new Hit(numBtn, this);

        hit.set_hitTitle("提示");
        hit.set_hitContent(context);


        hit.get_hitConfirm().setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog = new AlertDialog.Builder(this, R.style.hitStyle).setView(hit.get_view()).show();
    }

    private class DreamOpeartionThread extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... params) {
            String cmd = params[0];
            String result = "";
            String tid, auth, dream;
            switch (cmd) {
                case "loading":
                    tid = params[1];
                    auth = params[2];
                    result = db.readDream(tid, auth);
                    if (result.equals("")) result = "快來輸入你的夢想藍圖吧";
                    else dream = result;
                    break;
                case "update":
                    tid = params[1];
                    auth = params[2];
                    dream = params[3];
                    db.updateDream(tid, auth, dream);
                    result = dream;
                    break;
            }
            return result.trim();
        }

        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dreamContext.setText(s);
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

                    break;
                case "delete":
                    db.delete(params[1]);
                    break;
            }
            return null;
        }
    }

    //------頁面下方-新增任務------
    public void addTask(View view) {
        if (hasTask) {
            Toast.makeText(this, "您已經新增過任務了！", Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent();
            intent.setClass(this, TaskEventActivity.class);
            intent.putExtra("cmd", "addTask");
            intent.putExtra("tid", currTid);
            intent.putExtra("targetName", targetName);
            startActivity(intent);
        }
    }

    //------完成任務------

    private void completeTask(final Integer key) {

        Hit hit = new Hit("2", this);

        hit.set_hitTitle("提示");
        hit.set_hitContent("確認完成個人任務 ?");


        hit.get_hitConfirm().setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String mid = Integer.toString(key);
                String phpurl = LoginActivity.getLocalHost() + "updateTaskState.php?mid=" + mid + "&tid=" + currTid + "&uid=" + LoginActivity.getUser().uid + "&partnerid=" + taskMap.get(key).collaborator;
                new TransTask().execute(phpurl);

            }
        });

        hit.get_hitCancel().setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog = new AlertDialog.Builder(this, R.style.hitStyle).setView(hit.get_view()).show();
    }

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
            writeRecord();
        }
    }

    private void writeRecord() {
        View dialog_view;

        dialog_view = LayoutInflater.from(this).inflate(R.layout.write_record, null);


        final EditText recordContext = dialog_view.findViewById(R.id.recordContext);
        Button save = dialog_view.findViewById(R.id.save);
        Button cancel = dialog_view.findViewById(R.id.cancel);


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                socketTrans.setParams("addRecord",user.uid,currTid,recordContext.getText().toString());
                socketTrans.send();
                dialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog = new AlertDialog.Builder(this, R.style.hitStyle).setView(dialog_view).create();
        dialog.show();
        Window dialogWindow = dialog.getWindow();
        WindowManager m = this.getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高度
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        p.height = (int) (d.getHeight() * 0.7); // 高度设置为屏幕的0.6，根据实际情况调整
        p.width = (int) (d.getWidth() * 0.8); // 宽度设置为屏幕的0.65，根据实际情况调整
        dialogWindow.setAttributes(p);

    }


    //------資料庫連接 Code   進度條搜尋百分比------//

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


    //------元素搜尋------//

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

            dialog = new AlertDialog.Builder(TaskActivity.this, R.style.hitStyle).setView(dialog_view).create();
            dialog.show();
            Window dialogWindow = dialog.getWindow();
            WindowManager m = TaskActivity.this.getWindowManager();
            Display d = m.getDefaultDisplay(); // 获取屏幕宽、高度
            WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
            p.height = (int) (d.getHeight() * 0.7); // 高度设置为屏幕的0.6，根据实际情况调整
            p.width = (int) (d.getWidth() * 0.8); // 宽度设置为屏幕的0.65，根据实际情况调整
            dialogWindow.setAttributes(p);
        }

    }


    //------管理訊息------//

    public void checkMessage(View view) {
        Intent intent = new Intent(this, Message.class);
        intent.putExtra("tid", currTid);
        startActivity(intent);
    }


    //------管理成員------//

    public void checkMember(View view) {
        Intent intent = new Intent(this, Member.class);
        intent.putExtra("tid", currTid);
        intent.putExtra("targetName", targetName);
        startActivityForResult(intent,FUNC_MEMBER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==FUNC_MEMBER){
            if(resultCode==RESULT_OK){
                finish();
            }
        }
    }
}
