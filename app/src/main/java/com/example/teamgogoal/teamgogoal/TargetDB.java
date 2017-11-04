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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
            String s = viaParams("user=" + user.uid, "readTarget.php");
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
                String participator = readParticipator(tid);
                TargetDetail TargetDetail = new TargetDetail(tid, targetName, targetContent, startTime, endTime, state, auth, participator, allmission, completemission);
                map.put(obj.getString("tid"), TargetDetail);
                /*String tid=obj.getString("tid");
                String targetName=obj.getString("targetName");
                String targetContent=obj.getString("targetContent");
                String startTime=obj.getString("targetStartTime");
                String endTime=obj.getString("targetEndTime");
                String state=obj.getString("state");
                String auth=obj.getString("auth");
                String participator=readParticipator(tid);
                TargetDetail TargetDetail=new TargetDetail(tid,targetName,targetContent,startTime,endTime,state,auth,participator);
                //Log.v("jim",TargetDetail.tid +","+TargetDetail.targetName+","+TargetDetail.targetContent+","+TargetDetail.startTime+","+TargetDetail.endTime+","+TargetDetail.state+","+TargetDetail.auth+","+TargetDetail.dream);
                map.put(obj.getString("tid"),TargetDetail);*/
            }
        } catch (JSONException e) {
            Log.v("jim error in read：", e.toString());
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

    protected String createTarget(String param1, String param2, String param3, String param4, String param5, String param6, String param7) {
        String params = "table=target" + " & tid=" + param1 + " & targetName=" + param2 + " & targetContent=" + param3 + " & targetStartTime=" + param4 + " & targetEndTime=" + param5 + " & state=" + param6 + " & auth=" + param7;
        String php = "createTarget.php";
        String ans = viaParams(params, php);
        return ans;
    }

    protected void updateTarget(String param1, String param2, String param3, String param4, String param5, String param6, String param7, String param8, String param9) {
        String params = "table=target" + " & tid=" + param1 + " & targetName=" + param2 + " & targetContent=" + param3 + " & targetStartTime=" + param4 + " & targetEndTime=" + param5 + " & state=" + param6 + " & auth=" + param7;
        String php = "updateTarget.php";
        String ans = viaParams(params, php);

        String str[] = param8.split(",");
        String old_str[] = param9.split(",");
        List new_list = new ArrayList(Arrays.asList(str));
        List old_list = new ArrayList(Arrays.asList(old_str));
        List list = new ArrayList(Arrays.asList(new Object[old_list.size()]));
        list = diff(new_list, old_list);


        for (int i = 0; i < list.size(); i++) {
            if (!list.get(i).equals("")) {
                String params3 = "table=registerrequest & originator=" + user.account + " & cmd=request_ask & cmdContext=" + param1.trim() + " & subject=" + list.get(i).toString().trim();
                String php3 = "createRegisterRequest.php";
                String s = viaParams(params3, php3);
                socketTrans.setParams("register_request", user.account.trim(), list.get(i).toString().trim(), param2.trim());
                socketTrans.send();
            }
        }
        list.clear();
        list = diff(old_list, new_list);
        for (int i = 0; i < list.size(); i++) {
            if (!list.get(i).equals("")) {
                String params2 = "table=participator & tid=" + param1 + " & account=" + list.get(i).toString().trim();
                String php2 = "deleteParticipator.php";
                String ss = viaParams(params2, php2);

                //將退出訊息記錄到request與傳送socket訊息
                params2 = "table=registerrequest & originator=" + user.account + " & cmd=request_delete & cmdContext=" + param1.trim() + " & subject=" + list.get(i).toString().trim();
                php2 = "createRegisterRequest.php";
                String s = viaParams(params2, php2);

                socketTrans.setParams("register_delete", user.account.trim(), list.get(i).toString().trim(), param2.trim());
                socketTrans.send();
            }
        }

        list = intersect(old_list, new_list);
        list.remove(user.account);
        for (int i = 0; i < list.size(); i++) {
            if (!list.get(i).equals("")) {
                String params2 = "table=registerrequest & originator=" + user.account + " & cmd=request_update & cmdContext=" + param2.trim() + " & subject=" + list.get(i).toString().trim();
                String php2 = "createRegisterRequest.php";
                String ss = viaParams(params2, php2);

                socketTrans.setParams("register_update", user.account.trim(), list.get(i).toString().trim(), param2.trim());
                socketTrans.send();
            }
        }
    }

    public List diff(List ls, List ls2) {
        List list = new ArrayList(Arrays.asList(new Object[ls.size()]));
        Collections.copy(list, ls);
        list.removeAll(ls2);
        return list;
    }

    public List intersect(List ls, List ls2) {
        List list = new ArrayList(Arrays.asList(new Object[ls.size()]));
        Collections.copy(list, ls);
        list.retainAll(ls2);
        return list;
    }

    protected void createParticipator(String id, String param1) {
        String str[] = param1.split(",");
        String php, params;

        for (int i = 0; i < str.length; i++) {
            if (!str[i].equals("")) {
                if (str[i].equals(user.account)) {

                    php = "createParticipator.php";
                    params = "table=participator & tid=" + id.trim() + " & account=" + str[i].trim();
                    String result = viaParams(params, php);
                    Log.v("jim", result);
                } else {
                    php = "createRegisterRequest.php";
                    params = "table=registerrequest & originator=" + user.account + " & cmd=request_ask & cmdContext=" + id.trim() + " & subject=" + str[i].trim();
                    String result = viaParams(params, php);
                    Log.v("jim_TargetDB_createParticipator", result);
                    socketTrans.setParams("register_request", user.account.trim(), str[i].trim(), id.trim());
                    socketTrans.send(socketTrans.getParams());
                    Log.v("jim_createParticipator", result);
                }
            }
        }
    }

    protected void createParticipator(String tid, String originator, String subject) {
        socketTrans.setParams("register_request", originator, subject, tid);
        socketTrans.send(socketTrans.getParams());
        String result = socketTrans.getResult();
        Log.v("jim_createParticipator", result);
    }


    protected String targetIndex() {
        String params = "table=target";
        String php = "targetIndex.php";
        String ans = viaParams(params, php);
        return ans.trim();
    }

    protected void deleteTargetAll(String tid) {
        String params = "table=target & tid=" + tid.trim();
        String php = "deleteTargetAll.php";
        viaParams(params, php);
    }

    protected void deleteParticipator(String tid, String account) {
        String params = "table=participator & tid=" + tid.trim() + " & account=" + account.trim();
        String php = "deleteParticipator";
        viaParams(params, php);
    }

    //Data
    public static class TargetDetail {
        String tid, targetName, targetContent, state, auth, startTime, endTime, participator, allmission, completemission;

        TargetDetail(String param1, String param2, String param3, String param4, String param5, String param6, String param7, String param8, String param9, String param10) {
            this.tid = param1;
            this.targetName = param2;
            this.targetContent = param3;
            this.startTime = param4;
            this.endTime = param5;
            this.state = param6;
            this.auth = param7;
            this.participator = param8;
            this.allmission = param9;
            this.completemission = param10;
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
