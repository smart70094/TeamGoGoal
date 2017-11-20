package com.example.teamgogoal.teamgogoal;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.VideoView;

public class videoTest extends AppCompatActivity {

    private VideoView v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_test);

        v = (VideoView) findViewById(R.id.videoview);
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.cc);
        v.setVideoURI(uri);
        v.requestFocus();
    /* 开始播放影片 */
        v.start();


    }
}
