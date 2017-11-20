package com.example.teamgogoal.teamgogoal.DataBase;

import java.util.ArrayList;

/**
 * Created by hp on 2017/11/19.
 */

public class ElementDAOImpl extends DataBaseDAO{

    ElementDAOImpl(String localhost) {
        super(localhost);
    }
    public String addData(String... dataList) {
        String tid=dataList[0];
        String uid=dataList[1];
        String urlParams="tid="+tid+"&uid="+uid+"&a=0&b=0&c=0&d=0&e=0&f=0&g=0&incomplete=0";
        String php="createElement.php";
        String result=connect(urlParams,php);
        return result;
    }
    public ArrayList<String> getAllData(String... dataList){
        String tid=dataList[0];
        String uid=dataList[1];
        String urlParams="tid="+tid+"&uid="+uid;
        String php="readElement.php";

        return null;
    }

}
