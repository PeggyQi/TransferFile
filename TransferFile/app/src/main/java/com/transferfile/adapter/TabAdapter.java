package com.transferfile.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Tab管理类
 */
public class TabAdapter  extends FragmentPagerAdapter {
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private String[] mTitles;
        public TabAdapter(FragmentManager fm,ArrayList<Fragment> mFragments,String[] mTitles) {

            super(fm);
            this.mFragments=mFragments;
            this.mTitles=mTitles;
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }
}

