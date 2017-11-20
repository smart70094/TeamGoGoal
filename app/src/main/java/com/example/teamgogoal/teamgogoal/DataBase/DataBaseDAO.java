package com.example.teamgogoal.teamgogoal.DataBase;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by hp on 2017/11/19.
 */

public abstract class DataBaseDAO {
    String localhost;
    DataBaseDAO(String localhost){
        this.localhost=localhost;
    }
    public Object getData(String... dataList) throws Exception{
        throw new UnsupportedOperationException();
    }
    public boolean checkData(String... dataList) {
        throw new UnsupportedOperationException();
    }
    public ArrayList<String> getAllData(String... dataList){
        throw new UnsupportedOperationException();
    }
    public String addData(String... dataList) {
        throw new UnsupportedOperationException();
    }
    public void update(String... dataList) {
        throw new UnsupportedOperationException();
    }
    public boolean delete(int id) {
        throw new UnsupportedOperationException();
    }

    public String connect(String urlParameters,String php){
        byte[] postData = new byte[0];
        try {
            postData = urlParameters.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        int postDataLength = postData.length;
        String checkurl = localhost+php;

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
                sb.append(line+"\n");
            }
            br.close();
            conn.disconnect();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        return sb.toString().trim();
    }
}
