package com.example.teamgogoal.teamgogoal;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lukedeighton.wheelview.WheelView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Review2 extends Fragment {
    View v;
    //private Gallery gallery;
    private List<GalleryModel> list;
    private ArrayList<CompleteTarget> completetarget;
    private LoginActivity.User user = LoginActivity.getUser();
    private String userID;
    String localhost = LoginActivity.getLocalHost();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_review, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        v = getView();
        userID = user.uid;
        completetarget = new ArrayList<CompleteTarget>();
        intiDataBase();
    }

    private void intiDataBase() {
        //step1:找出回顧資料String phpurl = "http://106.107.161.179/android/searchCompleteTarget.php?userID=" + userID;
        //String phpurl = "http://106.107.161.179/android/searchCompleteTarget.php?userID=" + userID;

        String phpurl = localhost + "searchCompleteTarget.php?userID=" + userID;
        new TransTask().execute(phpurl);

    }

    private class TransTask extends AsyncTask<String, Void, String> {

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

            initData();
            initView();
        }

        private void parseJSON(String s) {
            try {
                JSONArray array = new JSONArray(s);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    String tid = obj.getString("tid");
                    String target = obj.getString("targetName");
                    String planet = obj.getString("planet");
                    completetarget.add(new CompleteTarget(tid, target, planet));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private void initData() {
        list = new ArrayList<GalleryModel>();
        Log.d("DDDD", Integer.toString(completetarget.size()));
        for (CompleteTarget cTarget : completetarget) {
            switch (cTarget.getPlanet()) {
                case "gold":
                    list.add(new GalleryModel(R.drawable.planet_gold, cTarget.getTarget(), ResourcesCompat.getDrawable(getResources(), R.drawable.planet_gold, null)));
                    break;
                case "tree":
                    list.add(new GalleryModel(R.drawable.planet_tree, cTarget.getTarget(), ResourcesCompat.getDrawable(getResources(), R.drawable.planet_tree, null)));
                    break;
                case "water":
                    list.add(new GalleryModel(R.drawable.planet_water, cTarget.getTarget(), ResourcesCompat.getDrawable(getResources(), R.drawable.planet_water, null)));
                    break;
                case "fire":
                    list.add(new GalleryModel(R.drawable.planet_fire, cTarget.getTarget(), ResourcesCompat.getDrawable(getResources(), R.drawable.planet_fire, null)));
                    break;
                case "earth":
                    list.add(new GalleryModel(R.drawable.planet_earth, cTarget.getTarget(), ResourcesCompat.getDrawable(getResources(), R.drawable.planet_earth, null)));
                    break;
            }
        }

    }

    private void initView() {
        final WheelView wheelView = (WheelView) v.findViewById(R.id.wheelview);
        wheelView.setAdapter(new Review_ListAdapter(list, v.getContext()));


        wheelView.setOnWheelItemClickListener(new WheelView.OnWheelItemClickListener() {

            @Override
            public void onWheelItemClick(WheelView parent, int position, boolean isSelected) {

                if (isSelected) {
                    Intent intent = new Intent();
                    intent.setClass(v.getContext(), Record.class);
                    intent.putExtra("tid", completetarget.get(position).getTid());
                    intent.putExtra("userID", userID);
                    intent.putExtra("target", completetarget.get(position).getTarget());
                    intent.putExtra("planet_imv", list.get(position).getImageView());
                    startActivity(intent);
                }
            }


        });
    }

}
