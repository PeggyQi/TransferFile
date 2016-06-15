package com.transferfile.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.transferfile.R;
import com.transferfile.utils.CircleIndicator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/6/14.
 */
public class WelcomeActivity extends Activity {
    private List<View> viewList;
    private ViewPager viewPager;
    private CircleIndicator circleIndicator;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        initData();
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(pagerAdapter);

        circleIndicator = (CircleIndicator) findViewById(R.id.indicator);
        circleIndicator.setViewPager(viewPager);

    }

    private void initData() {
        LayoutInflater lif=LayoutInflater.from(this);
        viewList = new ArrayList<View>();
        View view1=lif.inflate(R.layout.activity_page1,null);
        view1.getBackground().setAlpha(230);
        View view2=lif.inflate(R.layout.activity_page2,null);
        view2.getBackground().setAlpha(230);
        viewList.add(view1);
        viewList.add(view2);
    }

    public void StartActivity(View v)
    {
        Intent intent=new Intent();
        intent.setClass(WelcomeActivity.this,MainActivity.class);
        startActivity(intent);
        this.finish();
    }

    PagerAdapter pagerAdapter = new PagerAdapter() {

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {

            return arg0 == arg1;
        }

        @Override
        public int getCount() {

            return viewList.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position,
                                Object object) {
            container.removeView(viewList.get(position));

        }

        @Override
        public int getItemPosition(Object object) {

            return super.getItemPosition(object);
        }

        @Override
        public CharSequence getPageTitle(int position) {

            return "title";
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(viewList.get(position));

            return viewList.get(position);
        }

    };
}
