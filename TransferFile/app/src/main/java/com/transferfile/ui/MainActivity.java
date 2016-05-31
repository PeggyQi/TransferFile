package com.transferfile.ui;

import android.app.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.transferfile.R;
import com.transferfile.adapter.TabAdapter;
import com.transferfile.fabtoolbarlib.widget.FABToolbarLayout;
import com.transferfile.tablayout.SlidingTabLayout;
import com.transferfile.tablayout.listener.OnTabSelectListener;
import com.transferfile.utils.ViewFindUtils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,OnTabSelectListener,View.OnClickListener {
    private Context mContext = this;
    private IntentFilter filter;
    private Receiver receiver;

    /**浮动按钮**/
    private FABToolbarLayout fabToolbarLayout;
    private FloatingActionButton fab;
    private View sendview, receiveview;

    /**popupwindow**/
    private PopupWindow popuWindow;
    private View popView;
    private TextView sendtv;//发送按键
    private ImageView canceliv;//取消按键
    private TextView sendnumtv;//选中文件个数

    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private final String[] mTitles = {
            "历史", "图片","音频"
            , "视频", "文档", "应用"
    };
    private TabAdapter mTabAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();//初始化侧栏
        initTab();//初始化Tab
        initPopupwindow();//初始化Popupwindow
        filter=new IntentFilter();
        filter.addAction("ChildAdapter_CheckBoxClick");//有被选中的图片隐藏fab,弹出poupwindow
        filter.addAction("ChildAdapter_CheckBoxUnClick");//没有选中的图片弹出fab,隐藏popupwindow
        filter.addAction("ShowImageFragmentDestroyView");//该fragment销毁
        filter.addAction("ChildAdapter_CheckBoxChange");//选中文件个数发生变化
        receiver=new Receiver();
    }

    /*初始化侧栏、toolbar、fab*/
    public void initUI()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fabToolbarLayout = (FABToolbarLayout) findViewById(R.id.fabtoolbar);
        sendview = findViewById(R.id.sendtv);
        receiveview = findViewById(R.id.receivetv);

        sendview.setOnClickListener(this);
        receiveview.setOnClickListener(this);

        fab = (FloatingActionButton) findViewById(R.id.fabtoolbar_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fabToolbarLayout.show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    /*初始化Tab页面*/
    public void initTab()
    {
        for (String title : mTitles) {
            if(title.equals("历史"))
            mFragments.add(HistoryFragment.getInstance(title));
            if(title.equals("图片"))
                mFragments.add(RootPhotoFragment.getInstance(title));
            if(title.equals("音频"))
                mFragments.add(ApplicationFragment.getInstance(title));
            if(title.equals("视频"))
                mFragments.add(ApplicationFragment.getInstance(title));
            if(title.equals("文档"))
                mFragments.add(ApplicationFragment.getInstance(title));
            if(title.equals("应用"))
                mFragments.add(ApplicationFragment.getInstance(title));
        }

        View decorView = getWindow().getDecorView();
        ViewPager vp = ViewFindUtils.find(decorView, R.id.viewpager);
        mTabAdapter = new TabAdapter(getSupportFragmentManager(),mFragments,mTitles);
        vp.setAdapter(mTabAdapter);
        SlidingTabLayout tabLayout = ViewFindUtils.find(decorView, R.id.tablayout);
        tabLayout.setViewPager(vp, mTitles, this, mFragments);
        vp.setCurrentItem(1);
    }

    /**初始化popupwindow**/
    public void initPopupwindow()
    {
        if (popuWindow == null) {
            popView = LayoutInflater.from(MainActivity.this).inflate(R.layout.popuwindow, null);
            popuWindow = new PopupWindow(popView, ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            sendtv = (TextView) popView.findViewById(R.id.sendtv_popupwindow);
            canceliv= (ImageView) popView.findViewById(R.id.cancel_popupwindow);
            sendnumtv=(TextView)popView.findViewById(R.id.sendnumtv_popupwindow);

        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        registerReceiver(receiver, filter);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if(fabToolbarLayout.isOpen()==true)
            fabToolbarLayout.hide();
        else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onTabSelect(int position) {
        Toast.makeText(mContext, "onTabSelect&position--->" + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTabReselect(int position) {
        Toast.makeText(mContext, "onTabReselect&position--->" + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(this, "Element clicked", Toast.LENGTH_SHORT).show();
    }

    public class Receiver extends BroadcastReceiver{
        @Override
        public void onReceive(final Context context,Intent intent)
        {
            if(intent.getAction().equals("ChildAdapter_CheckBoxClick"))//文件被选中
            {
                if(fabToolbarLayout.isOpen()==true)
                    fabToolbarLayout.hide();
                try {
                    Thread.currentThread().sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                showPopuWindow(getWindow().findViewById(R.id.popupwindow_showimage));
                fab.setVisibility(View.INVISIBLE);
            }
            if(intent.getAction().equals("ChildAdapter_CheckBoxUnClick"))//选中0个文件
           {
               if(popuWindow!=null)
               popuWindow.dismiss();
               fab.setVisibility(View.VISIBLE);
           }
            if(intent.getAction().equals("ShowImageFragmentDestroyView"))//该ShowImagefragment被销毁
            {
                if(popuWindow!=null)
                popuWindow.dismiss();
                fab.setVisibility(View.VISIBLE);
            }
            if(intent.getAction().equals("ChildAdapter_CheckBoxChange"))//选中文件个数发生变化
            {
                int seleectnum=Integer.parseInt(intent.getExtras().get("selectimagenum").toString());
                sendnumtv.setText(String.valueOf(seleectnum));

            }
        }
    }

    /**选中文件后弹出**/
    private void showPopuWindow(View parent) {

        sendtv.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                popuWindow.dismiss();
            }
        });
        canceliv.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                popuWindow.dismiss();
            }
        });

//        ColorDrawable cd = new ColorDrawable(0x000000);
//        popuWindow.setBackgroundDrawable(cd);
//        //产生背景变暗效果
//        WindowManager.LayoutParams lp = getWindow().getAttributes();
//        lp.alpha = 0.4f;
//        getWindow().setAttributes(lp);
        popuWindow.setOutsideTouchable(false);//点击其他地方不消失
        popuWindow.setFocusable(false); // 设置PopupWindow可获得焦点点击返回键直接退出
        popuWindow.setTouchable(true); // 设置PopupWindow可触摸
        popuWindow.showAtLocation((View) parent.getParent(), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);//popupwindow从底层由下弹出
        popuWindow.update();
        popuWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            //在dismiss中恢复透明度
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;

                 getWindow().setAttributes(lp);
            }
        });

    }

}
