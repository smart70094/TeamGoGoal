package com.example.teamgogoal.teamgogoal;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.List;

public class Question2 extends Fragment {
    private ExpandableListView expandableListView;

    private List<String> group_list;

    private List<String> item_group1_use_detail;
    private List<String> item_group2_common_question;
    private List<String> item_group2_common_question_content;

    private List<List<String>> item_list;

    private ExpandListView_ListAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_question, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View v = getView();


        expandableListView = (ExpandableListView) v.findViewById(R.id.question_listview);

        group_list = new ArrayList<String>();
        group_list.add("使用說明");
        group_list.add("常見問題");

        item_group1_use_detail = new ArrayList<String>();
        item_group1_use_detail.add("沒");

        item_group2_common_question = new ArrayList<String>();
        item_group2_common_question.add("Q 怎麼傳送訊息給別人？");
        item_group2_common_question.add("Q 在新增目標成員時，要輸入帳號還是暱稱呢？");
        item_group2_common_question.add("Q 我要在哪裡接收我的訊息及通知呢？");
        item_group2_common_question.add("Q 如果想要一個人鼓勵我，但不敢直接跟她講怎麼辦？");
        item_group2_common_question.add("Q 元素是拿來幹嘛的？");
        item_group2_common_question.add("Q 每天我都會有一個新的夥伴，夥伴有什麼特別的嗎？");
        item_group2_common_question.add("Q 如果沒有完成任務會怎麼樣？");
        item_group2_common_question.add("Q 藍圖是甚麼？");

        item_group2_common_question_content = new ArrayList<String>();
        item_group2_common_question_content.add("點擊任務頭像即可編寫訊息傳送");
        item_group2_common_question_content.add("帳號");
        item_group2_common_question_content.add("所有的訊息及通知都會在信箱裡面唷");
        item_group2_common_question_content.add("可以使用匿名要求鼓勵的功能喔");
        item_group2_common_question_content.add("每次完成任務時都可以獲得元素，最後系統會依據你得到的元素，建立專屬於你的星球唷");
        item_group2_common_question_content.add("夥伴如果完成任務，你們兩個人就可以都獲得一個元素");
        item_group2_common_question_content.add("除了拿不到自己的元素外，你的夥伴也會因此少拿一個元素唷");
        item_group2_common_question_content.add("藍圖代表你在創立或加入目標時，給予未來的自己完成這項目標後的期許");

        item_list = new ArrayList<List<String>>();
        item_list.add(item_group1_use_detail);
        item_list.add(item_group2_common_question);

        expandableListView.setGroupIndicator(null);
        adapter = new ExpandListView_ListAdapter(this.getContext(),group_list,item_list,item_group2_common_question_content);
        expandableListView.setAdapter(adapter);
    }

}
