package com.example.teamgogoal.teamgogoal;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.List;
import java.util.Vector;

public class test extends AppCompatActivity {
    private android.support.design.widget.TabLayout mTabs;
    private ViewPager mViewPager;
    List<Fragment> fragments = new Vector<Fragment>();
    private PagerAdapter mPagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        this.initialisePaging();
        mTabs = (android.support.design.widget.TabLayout) findViewById(R.id.tabs);
        mTabs.addTab(mTabs.newTab().setText("Tab 1"));
        mTabs.addTab(mTabs.newTab().setText("Tab 2"));
        mTabs.addTab(mTabs.newTab().setText("Tab 3"));


        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setAdapter(mPagerAdapter);

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

    private void initialisePaging() {
        fragments.add(Fragment.instantiate(this, Question2.class.getName()));
        fragments.add(Fragment.instantiate(this, TargetActivity2.class.getName()));
        fragments.add(Fragment.instantiate(this, Review2.class.getName()));
        this.mPagerAdapter  = new SamplePagerAdapter(super.getSupportFragmentManager(), fragments);
    }

    class SamplePagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragments;

        public SamplePagerAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public int getCount() {
            return this.fragments.size();
        }

        @Override
        public Fragment getItem(int position) {
            return this.fragments.get(position);
        }
    }
}
