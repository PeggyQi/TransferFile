package com.transferfile.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;

import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.transferfile.R;
import com.transferfile.Wifi.WiFiAdmin;
import com.transferfile.adapter.TabAdapter;
import com.transferfile.fabtoolbarlib.widget.FABToolbarLayout;
import com.transferfile.tablayout.SlidingTabLayout;
import com.transferfile.tablayout.listener.OnTabSelectListener;
import com.transferfile.utils.CustomDialog;
import com.transferfile.utils.GuardLoadingRenderer;
import com.transferfile.utils.LoadingDrawable;
import com.transferfile.utils.ViewFindUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,OnTabSelectListener,View.OnClickListener {
    private Context mContext = this;
    private IntentFilter filter;
    private Receiver receiver;

    /**浮动按钮**/
    private FABToolbarLayout fabToolbarLayout;
    private FloatingActionButton fab;
    private View createiv, scaniv,createtv,scantv,cancel_popupwindow,sendtv_popupwindow;
    private TextView sendnumtv_popupwindow;
    private boolean selectfolderflag=false;//标记选中文件

    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private final String[] mTitles = {
            "历史", "图片","音频"
            , "视频", "文档", "应用"
    };
    private TabAdapter mTabAdapter;
    private ViewPager vp;
    private String currentTitle;//当前ViewPager

    //增加wifi相关
    BroadcastReceiver wifiReceiver;
    WiFiAdmin wiFiAdmin;
    List<WifiP2pDevice> devices = null;
    CustomDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();//初始化侧栏
        initTab();//初始化Tab
        filter=new IntentFilter();
        filter.addAction("ChildAdapter_CheckBoxClick");//有被选中的图片隐藏fab,弹出poupwindow
        filter.addAction("ChildAdapter_CheckBoxUnClick");//没有选中的图片弹出fab,隐藏popupwindow
        filter.addAction("ShowImageFragmentDestroyView");//该fragment销毁
        filter.addAction("ChildAdapter_CheckBoxChange");//选中文件个数发生变化
        filter.addAction("ViewPageChange");//ViewPage发生变化

        receiver=new Receiver();

        //wifi管理部分初始化
        wiFiAdmin = new WiFiAdmin((WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE), this);
        wifiReceiver = wiFiAdmin.getWiFiBroadcastReceiver();
        filter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        filter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        filter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        filter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        filter.addAction("DrawTickDone");
    }

    /*初始化侧栏、toolbar、fab*/
    public void initUI()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fabToolbarLayout = (FABToolbarLayout) findViewById(R.id.fabtoolbar);
        createiv = findViewById(R.id.createiv);//创建直连按钮
        scaniv = findViewById(R.id.scaniv);//扫描直连按钮
        createtv=findViewById(R.id.createtv);
        scantv=findViewById(R.id.scantv);
        cancel_popupwindow=findViewById(R.id.cancel_popupwindow);//取消选项
        sendtv_popupwindow=findViewById(R.id.sendtv_popupwindow);//发送文件
        sendnumtv_popupwindow=(TextView)findViewById(R.id.sendnumtv_popupwindow);//选中数目
        createiv.setOnClickListener(this);
        scaniv.setOnClickListener(this);
        cancel_popupwindow.setOnClickListener(this);
        sendtv_popupwindow.setOnClickListener(this);

        fab = (FloatingActionButton) findViewById(R.id.fabtoolbar_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changefablayout();
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
                mFragments.add(MusicFragment.getInstance(title));
            if(title.equals("视频"))
                mFragments.add(VideoFragment.getInstance(title));
            if(title.equals("文档"))
                mFragments.add(FolderFragment.getInstance(title));
            if(title.equals("应用"))
                mFragments.add(ApplicationFragment.getInstance(title));
        }

        View decorView = getWindow().getDecorView();
        vp = ViewFindUtils.find(decorView, R.id.viewpager);
        mTabAdapter = new TabAdapter(getSupportFragmentManager(),mFragments,mTitles);
        vp.setAdapter(mTabAdapter);
        SlidingTabLayout tabLayout = ViewFindUtils.find(decorView, R.id.tablayout);
        tabLayout.setMainContext(mContext);//将主界面context出入Tab类中可在页面切换时发广播
        tabLayout.setViewPager(vp, mTitles, this, mFragments);
        vp.setCurrentItem(1);
    }

    /**判断fabtoolbar布局**/
    public void changefablayout()
    {
        if(selectfolderflag==true)//选中文件
        {
            createiv.setVisibility(View.INVISIBLE);
            scaniv.setVisibility(View.INVISIBLE);
            createtv.setVisibility(View.INVISIBLE);
            scantv.setVisibility(View.INVISIBLE);
            cancel_popupwindow.setVisibility(View.VISIBLE);
            sendtv_popupwindow.setVisibility(View.VISIBLE);
            sendnumtv_popupwindow.setVisibility(View.VISIBLE);
        }
        else
        {
            createiv.setVisibility(View.VISIBLE);
            scaniv.setVisibility(View.VISIBLE);
            createtv.setVisibility(View.VISIBLE);
            scantv.setVisibility(View.VISIBLE);
            cancel_popupwindow.setVisibility(View.INVISIBLE);
            sendtv_popupwindow.setVisibility(View.INVISIBLE);
            sendnumtv_popupwindow.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        registerReceiver(receiver, filter);
        registerReceiver(wifiReceiver, filter);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        unregisterReceiver(receiver);
        unregisterReceiver(wifiReceiver);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if(fabToolbarLayout.isOpen()==true)
            fabToolbarLayout.hide();
        else if(vp.getCurrentItem()==4)//当前页面为文档时，按返回键可回到上一目录
        {
            FolderFragment.getFolderFragment().backMenu();
        }
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

    //FabToolBar中的组件点击事件
    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.createiv:                 //打开WiFi直连
                wiFiAdmin.openWifi();
                dialog = new CustomDialog(MainActivity.this,R.style.DefinDialog);
                dialog.show();
//                devices = wiFiAdmin.getDeviceList();
//                if (devices != null && devices.size() > 0){
//                    //默认链接第一个
//                    wiFiAdmin.connectDevice(devices.get(0));
//                }
//                else {
//                    Toast.makeText(MainActivity.this, "未查找到设备，请稍等",Toast.LENGTH_SHORT).show();
//                }
                break;
            case R.id.scaniv:                  //扫描WiFi直连
                wiFiAdmin.scanWifiDevice();
                break;
            case R.id.cancel_popupwindow:       //取消选中的所有文件
                 int currentitem=vp.getCurrentItem();
//                Toast.makeText(this, "当前view"+currentitem, Toast.LENGTH_SHORT).show();
                if(currentitem==1)
                    ShowImageFragment.getSif().clearSelectData();
                break;
            default:
                break;

        }

    }


    public class Receiver extends BroadcastReceiver{
        @Override
        public void onReceive(final Context context,Intent intent)
        {
            if(intent.getAction().equals("ChildAdapter_CheckBoxClick"))//文件被选中
            {
                selectfolderflag=true;
                changefablayout();
                if(fabToolbarLayout.isOpen()==false)
                    fabToolbarLayout.show();
            }
            if(intent.getAction().equals("ChildAdapter_CheckBoxUnClick"))//选中0个文件
           {
               selectfolderflag=false;
              if(fabToolbarLayout.isOpen()==true)
                  fabToolbarLayout.hide();
           }
            if(intent.getAction().equals("ShowImageFragmentDestroyView"))//该ShowImagefragment被销毁
            {
                selectfolderflag=false;
                if(fabToolbarLayout.isOpen()==true)
                    fabToolbarLayout.hide();
            }
            if(intent.getAction().equals("ChildAdapter_CheckBoxChange"))//选中文件个数发生变化
            {
                int seleectnum=Integer.parseInt(intent.getExtras().get("selectimagenum").toString());
                sendnumtv_popupwindow.setText(String.valueOf(seleectnum));
            }
            if(intent.getAction().equals("ViewPageChange"))//ViewPage发生变化
            {
                selectfolderflag=false;
                if(fabToolbarLayout.isOpen()==true)
                {
                    fabToolbarLayout.hide();
                }
                if(vp.getCurrentItem()==1&&ShowImageFragment.getSif()!=null)
                    ShowImageFragment.getSif().clearSelectData();
            }
            if(intent.getAction().equals("DrawTickDone"))//绘制完成
            {
                dialog.cancel();
            }
        }
    }

}
