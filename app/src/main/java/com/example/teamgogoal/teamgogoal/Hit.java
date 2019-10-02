package com.example.teamgogoal.teamgogoal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.teamgogoal.view.activity.R;

/**
 * Created by Gary on 2017/11/15.
 */

public class Hit {


    View dialog_view;
    Context context;

    TextView hitTitle;
    TextView hitContent;
    Button hitConfirm;
    Button hitCancel;



    public Hit(String cmd,Context context){

        this.context = context;

        switch (cmd){
            case "1":
                dialog_view = LayoutInflater.from(this.context).inflate(R.layout.hit_dialog, null);
                break;
            case "2":
                dialog_view = LayoutInflater.from(this.context).inflate(R.layout.hit_dialog_two_btn, null);
                break;
        }

        hitTitle = dialog_view.findViewById(R.id.hitTitle);
        hitContent = dialog_view.findViewById(R.id.hitContent);
        hitConfirm = dialog_view.findViewById(R.id.hitComfirm);
        if(cmd.equals("2")) {
            hitCancel = dialog_view.findViewById(R.id.hitCancel);
        }
    }



    public void set_hitTitle(String title){
        this.hitTitle.setText(title);
    }
    public void set_hitContent(String title){
        this.hitContent.setText(title);
    }

    public TextView get_hitTitle(){
        return this.hitTitle;
    }
    public TextView get_hitContent(){
        return this.hitContent;
    }
    public Button get_hitConfirm(){
        return this.hitConfirm;
    }
    public Button get_hitCancel(){
        return this.hitCancel;
    }

    public View get_view(){
        return dialog_view;
    }
}
