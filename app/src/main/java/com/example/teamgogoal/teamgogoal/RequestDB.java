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
 * Created by hp on 2017/8/23.
 */

public class RequestDB {
    LoginActivity.User user=LoginActivity.getUser();
    protected String readRequest(){
        String s=null;
        try {
            String params="table=registerrequest & originator="+user.account;
            s=viaParams(params,"readRegisterRequest.php");
        } catch(Exception e){
            Log.v("jim_requestDB_read",e.toString());
        }
        return s;
    }

    protected  void deleteRegisterRequest(String rid){
        String params="table=registerrequest & rid="+rid;
        String php="deleteregisterrequest.php";
        String result=viaParams(params,php);
        Log.v("jim",result);
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

    public static class RequestDetail{
        String rid,subject,cmd,cmdContext,originator;
        RequestDetail(String rid,String subject,String cmd,String cmdContext,String originator){
            this.rid=rid;
            this.subject=subject;
            this.cmd=cmd;
            this.cmdContext=cmdContext;
            this.originator=originator;
        }
    }

}
