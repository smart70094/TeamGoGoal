package com.example.teamgogoal.teamgogoal;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hp on 2017/7/23.
 */

public class TargetDB {

    LoginActivity.User user = LoginActivity.getUser();
    SocketTrans socketTrans = LoginActivity.socketTrans;


    protected Map<String, TargetDetail> readTarget() {
        Map<String, TargetDetail> map = new HashMap<String, TargetDetail>();
        try {
            String s = viaParams("user=" + LoginActivity.getUser().uid, "readTarget.php");
            JSONArray array = new JSONArray(s.toString());
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                String tid = obj.getString("tid");
                String targetName = obj.getString("targetName");
                String targetContent = obj.getString("targetContent");
                String startTime = obj.getString("targetStartTime");
                String endTime = obj.getString("targetEndTime");
                String state = obj.getString("state");
                String auth = obj.getString("auth");
                String allmission = obj.getString("allmission");
                String completemission = obj.getString("completemission");
                TargetDetail TargetDetail = new TargetDetail(tid, targetName, targetContent, startTime, endTime, state, auth, allmission, completemission);
                map.put(obj.getString("tid"), TargetDetail);
            }
        } catch (JSONException e) {
            Log.v("jim error in showTargetEvent：", e.toString());
        }
        return map;
    }

    protected String readParticipator(String tid) {
        String ans = "";
        String params = "table=participator & tid=" + tid;
        String php = "readParticipator";
        String s = viaParams(params, php);
        ans = s.substring(2);
        return ans.trim();
    }

    protected String  createTarget(String... dataList) {
        String name=dataList[0];
        String context=dataList[1];
        String startTime=dataList[2];
        String endTime=dataList[3];
        String auth=dataList[4];
        socketTrans.setParams("addTarget",name,context,startTime,endTime,auth);
        socketTrans.send();
        return null;
    }

    protected void updateTarget(String param1, String param2, String param3, String param4, String param5) {
        String params = "table=target" + " & tid=" + param1 + " & targetName=" + param2 + " & targetContent=" + param3 + " & targetStartTime=" + param4 + " & targetEndTime=" + param5;
        String php = "updateTarget.php";
        String ans = viaParams(params, php);
        socketTrans.setParams("register_update",user.account,param1,param2);
        socketTrans.send();
    }


    public void requestAddParticipator(String... dataList){
        String tid=dataList[0];
        String account=dataList[1];
        socketTrans.setParams("addParticipator",tid,account);
        socketTrans.send();
    }
    protected void deleteTargetAll(String tid,String targetName) {
        /*String params = "table=target & tid=" + tid.trim();
        String php = "deleteTargetAll.php";
        viaParams(params, php);*/
        socketTrans.setParams("register_deleteAll",user.account,tid,targetName);
        socketTrans.send();
    }

    protected void deleteParticipator(String tid, String account) {
        String params = "table=participator & tid=" + tid.trim() + " & account=" + account.trim();
        String php = "deleteParticipator";
        viaParams(params, php);
    }

    //Data
    public static class TargetDetail {
        String tid, targetName, targetContent, state, auth, startTime, endTime, allmission, completemission;

        TargetDetail(String param1, String param2, String param3, String param4, String param5, String param6, String param7, String param8, String param9) {
            this.tid = param1;
            this.targetName = param2;
            this.targetContent = param3;
            this.startTime = param4;
            this.endTime = param5;
            this.state = param6;
            this.auth = param7;
            this.allmission = param8;
            this.completemission = param9;
        }
    }

    public String viaParams(String urlParameters, String php) {
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
