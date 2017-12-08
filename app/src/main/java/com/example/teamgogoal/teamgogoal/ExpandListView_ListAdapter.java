package com.example.teamgogoal.teamgogoal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Gary on 2017/12/8.
 */

public class ExpandListView_ListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> group_list;
    private List<List<String>> item_list;
    private List<String> item_group2_common_question_content;

    public ExpandListView_ListAdapter(Context context, List<String> group_list, List<List<String>> item_list, List<String> item_group2_common_question_content) {
        this.context = context;
        this.group_list = group_list;
        this.item_list = item_list;
        this.item_group2_common_question_content = item_group2_common_question_content;
    }

    @Override
    public int getGroupCount() {
        return group_list.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return item_list.get(i).size();
    }

    @Override
    public Object getGroup(int i) {
        return null;
    }

    @Override
    public Object getChild(int i, int i1) {
        return null;
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupHolder groupHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.expendlist_level1, null);
            groupHolder = new GroupHolder();
            groupHolder.txt = (TextView) convertView.findViewById(R.id.level1_text);
            convertView.setTag(groupHolder);
        } else {
            groupHolder = (GroupHolder) convertView.getTag();
        }

        /*if (!isExpanded)
        {
            groupHolder.img.setBackgroundResource(R.drawable.group_img);
        }
        else
        {
            groupHolder.img.setBackgroundResource(R.drawable.group_open_two);
        }*/

        groupHolder.txt.setText(group_list.get(groupPosition));
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ItemHolder itemHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.expendlist_level2, null);
            itemHolder = new ItemHolder();
            itemHolder.txt = (TextView) convertView.findViewById(R.id.level2_text);
            itemHolder.content = (TextView)convertView.findViewById(R.id.level2_content);
            convertView.setTag(itemHolder);
        } else {
            itemHolder = (ItemHolder) convertView.getTag();
        }
        itemHolder.txt.setText(item_list.get(groupPosition).get(childPosition));
        itemHolder.content.setText(item_group2_common_question_content.get(childPosition));
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }

    class GroupHolder {
        public TextView txt;
    }

    class ItemHolder {
        public TextView txt;
        public TextView content;
    }
}
