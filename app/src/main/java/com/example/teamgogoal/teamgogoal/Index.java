package com.example.teamgogoal.teamgogoal;

import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class Index extends AppCompatActivity {
    final int FUNC_ADDTARGET = 1;
    Context context = null;
    LocalActivityManager manager = null;
    private android.support.design.widget.TabLayout mTabs;
    private CustomViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        context = Index.this;
        manager = new LocalActivityManager(this, true);
        manager.dispatchCreate(savedInstanceState);

        mViewPager = (CustomViewPager) findViewById(R.id.viewpager);
        initPagerViewer();

        mTabs = (android.support.design.widget.TabLayout) findViewById(R.id.tabs);

        addTab(getResources().getDrawable(R.drawable.item_person, null));
        addTab(getResources().getDrawable(R.drawable.item_target, null));
        addTab(getResources().getDrawable(R.drawable.item_memory, null));
        addTab(getResources().getDrawable(R.drawable.item_message, null));
        addTab(getResources().getDrawable(R.drawable.item_q, null));

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabs));
        mTabs.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        mTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void addTab(Drawable drawable) {
        TabLayout.Tab tab = mTabs.newTab();
        View view = this.getLayoutInflater().inflate(R.layout.app_custom_tab, null);
        tab.setCustomView(view);
        ImageView imv = (ImageView) view.findViewById(R.id.icon);
        imv.setImageDrawable(drawable);
        mTabs.addTab(tab);
    }

    class MyPagerAdapter extends PagerAdapter {
        List<View> list = new ArrayList<View>();

        public MyPagerAdapter(ArrayList<View> list) {
            this.list = list;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ViewPager pViewPager = ((ViewPager) container);
            pViewPager.removeView(list.get(position));
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object instantiateItem(View arg0, int arg1) {
            ViewPager pViewPager = ((ViewPager) arg0);
            pViewPager.addView(list.get(arg1));
            return list.get(arg1);
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {

        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View arg0) {
        }
    }

    private void initPagerViewer() {

        final ArrayList<View> list = new ArrayList<View>();
        Intent intent_profile = new Intent(context, EditProfile.class);
        list.add(getView("A", intent_profile));
        Intent intent_target = new Intent(context, TargetActivity.class);
        list.add(getView("B", intent_target));
        Intent intent_review = new Intent(context, Review.class);
        list.add(getView("C", intent_review));
        Intent intent_request = new Intent(context, RequestActivity.class);
        list.add(getView("D", intent_request));
        Intent intent_question = new Intent(context, Question.class);
        list.add(getView("E", intent_question));


        mViewPager.setAdapter(new MyPagerAdapter(list));
        mViewPager.setCurrentItem(1);
    }

    private View getView(String id, Intent intent) {
        return manager.startActivity(id, intent).getDecorView();
    }

}
