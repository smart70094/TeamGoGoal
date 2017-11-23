package com.example.teamgogoal.teamgogoal;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hp on 2017/7/24.
 */

public class TaskDB {
    String localhost;

    public TaskDB(String localhost) {
        this.localhost = localhost + "readmission.php";
    }

    SocketTrans socketTrans = LoginActivity.socketTrans;

    protected Map<String, TaskDetail> read(String id) {
        Map<String, TaskDetail> map = new HashMap<String, TaskDetail>();
        try {
            String s = viaParams("tid=" + id, "readmission.php");
            JSONArray array = new JSONArray(s.toString());
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                String mid = obj.getString("mid").trim();
                String missionName = obj.getString("missionName");
                String missionContent = obj.getString("missionContent");
                String remindTime = obj.getString("remindTime");
                String tid = obj.getString("tid");
                String state = obj.getString("state");
                String auth = obj.getString("auth");
                String collaborator = obj.getString("collaborator");
                String dream=obj.getString("dream");
                String authID = obj.getString("authID");
                TaskDB.TaskDetail taskDetail = new TaskDB.TaskDetail(mid, missionName, missionContent, remindTime, tid, state, auth, dream ,collaborator, authID);
                map.put(mid, taskDetail);
            }
        } catch (JSONException e) {
            Log.v("jim", e.toString());
        }
        return map;
    }
    protected String readAlarmInfo(String... dataList){
        String auth=dataList[0];
        String urlParameters = "auth=" + auth;
        String php = "readAlarmMission.php";
        return viaParams(urlParameters, php);
    }

    protected String readDream(String tid,String auth){
        String params="tid="+tid+"&auth="+auth;
        String php="readDream.php";
        String result=viaParams(params,php);
        return result;
    }
    protected void updateDream(String tid,String auth,String dream){
        String params="tid="+tid+"&auth="+auth+"&dream="+dream;
        String php="updateDream.php";
        String result=viaParams(params,php);
    }

    protected void create(String... dataList) {
        String name = dataList[0];
        String context = dataList[1];
        String remindTime = dataList[2];
        String tid = dataList[3];
        String auth = dataList[4];
        socketTrans.setParams("addTask", name, context, remindTime, tid, auth);
        socketTrans.send();

    }

    protected void delete(String id) {
        id = id.trim();
        String params = "table=mission & mid=" + id.trim();
        String php = "deleteTask.php";
        viaParams(params, php);
    }

    protected void update(String param1, String param2, String param3, String param4, String param5) {
        String params = "table=mission" + " & mid=" + param1 + " & missionName=" + param2 + " & missionContent=" + param3 + " & remindTime=" + param4 + " & tid=" + param5;
        String php = "updateTask.php";
        String ans = viaParams(params, php);
    }

    public static class TaskDetail implements Serializable {
        String mid, missionName, missionContent, tid, state, auth, remindTime, collaborator, authID,dream;

        TaskDetail(String param1, String param2, String param3, String param4, String param5, String param6, String param7, String param8, String param9,String param10) {
            this.mid = param1;
            this.missionName = param2;
            this.missionContent = param3;
            this.remindTime = param4;
            this.tid = param5;
            this.state = param6;
            this.auth = param7;
            this.dream=param8;
            this.collaborator = param9;
            this.authID = param10;
        }
    }

    public String viaParams(String urlParameters, String php) {
        //Log.v("jim",urlParameters);
        byte[] postData = new byte[0];
        try {
            postData = urlParameters.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        int postDataLength = postData.length;
        String checkurl = LoginActivity.getLocalHost() + php;
        Log.v("localhost:", checkurl);
        StringBuilder sb = null;
        try {
            URL connectto = new URL(checkurl);
            HttpURLConnection conn = (HttpURLConnection) connectto.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Accept-Charset", "UTF-8");
            conn.setRequestProperty("Accept-Encoding", "UTF-8");
            conn.setUseCaches(false);
            conn.setAllowUserInteraction(false);
            conn.setInstanceFollowRedirects(false);
            conn.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.write(postData);
            wr.flush();
            wr.close();

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            sb = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                //Log.v("jim",line);
                sb.append(line + "\n");
            }
            br.close();
            conn.disconnect();
        } catch (Exception e) {
            Log.v("jim", e.toString());
        }
        return sb.toString();
    }
}
