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

    public TaskDB(String localhost){
        this.localhost=localhost+"readmission.php";
    }

    protected Map<String,TaskDetail> read(String id){
        Map<String, TaskDetail> map=new HashMap<String,TaskDetail>();
        try {
            String s=viaParams("tid="+id,"readmission.php");
            JSONArray array = new JSONArray(s.toString());
            for (int i=0; i<array.length(); i++){
                JSONObject obj = array.getJSONObject(i);
                String mid=obj.getString("mid").trim();
                String missionName=obj.getString("missionName");
                String missionContent=obj.getString("missionContent");
                String remindTime=obj.getString("remindTime");
                String tid=obj.getString("tid");
                String planet=obj.getString("planet");
                String state=obj.getString("state");
                String auth=obj.getString("auth");
                TaskDB.TaskDetail taskDetail=new TaskDB.TaskDetail(mid,missionName,missionContent,remindTime,tid,planet,state,auth);
                map.put(mid,taskDetail);
            }
        } catch(JSONException e){
            Log.v("jim",e.toString());
        }
        return map;
    }
    protected String taskIndex(){
        String params="table=mission";
        String php="taskIndex.php";
        String ans=viaParams(params,php);
        return  ans.trim();
    }
    protected void create(String param1,String param2,String param3,String param4,String param5,String param6,String param7,String param8){
        String params="table=mission"+" & mid="+param1 +" & missionName="+param2+" & missionContent="+param3+" & remindTime="+param4+" & tid="+param5+" & planet="+param6+" & state="+param7+" & auth="+param8;
        String php="createTask.php";
        String ans=viaParams(params,php);
    }
    protected void delete(String id){
        id=id.trim();
        String params="table=mission & mid="+id.trim();
        String php="deleteTask.php";
        viaParams(params,php);
    }
    protected  void update(String param1,String param2,String param3,String param4,String param5,String param6){
        String params="table=mission"+" & mid="+param1 +" & missionName="+param2+" & missionContent="+param3+" & remindTime="+param4+" & tid="+param5+" & planet="+param6;
        String php="updateTask.php";
        String ans=viaParams(params,php);
    }
    public static class TaskDetail  implements Serializable {
        String mid,missionName,missionContent,tid,state,auth,remindTime,planet;
        TaskDetail(String param1,String param2,String param3,String param4,String param5,String param6,String param7,String param8){
            this.mid=param1;
            this.missionName=param2;
            this.missionContent=param3;
            this.remindTime=param4;
            this.tid=param5;
            this.planet=param6;
            this.state=param7;
            this.auth=param8;
        }
    }
    public String viaParams(String urlParameters,String php){
        //Log.v("jim",urlParameters);
        byte[] postData = new byte[0];
        try {
            postData = urlParameters.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        int postDataLength = postData.length;
        String checkurl = LoginActivity.getLocalHost()+php;
        Log.v("localhost:",checkurl);
        StringBuilder sb=null;
        try {
            URL connectto = new URL(checkurl);
            HttpURLConnection conn = (HttpURLConnection) connectto.openConnection();
            conn.setRequestMethod( "POST" );
            conn.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
            conn.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty( "Accept-Charset", "UTF-8");
            conn.setRequestProperty( "Accept-Encoding", "UTF-8");
            conn.setUseCaches(false);
            conn.setAllowUserInteraction(false);
            conn.setInstanceFollowRedirects( false );
            conn.setDoOutput( true );


            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.write(postData);
            wr.flush();
            wr.close();

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            sb = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                //Log.v("jim",line);
                sb.append(line+"\n");
            }
            br.close();
            conn.disconnect();
        }
        catch (Exception e) {
            Log.v("jim",e.toString());
        }
        return sb.toString();
    }
}
