package com.example.teamgogoal.teamgogoal;

import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Gary on 2017/10/18.
 */

public class Hit {
        TextView hitTitle;
        TextView htiContent;
        Button hitComfirm;

    public void setHitTitle(TextView hitTitle){
        this.hitTitle = hitTitle;
    }

    public void setHtiContent(TextView hitContent){
        this.htiContent = hitContent;
    }

    public void setConfirm(Button Confirm){
        this.hitComfirm = Confirm;
    }

    public TextView getHitTitle(){return this.hitTitle;}
    public TextView gethtiContent(){return this.htiContent;}
    public Button getConfirm(){return this.hitComfirm;}
}


