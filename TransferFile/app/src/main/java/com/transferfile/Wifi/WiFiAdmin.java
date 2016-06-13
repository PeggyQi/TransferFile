package com.transferfile.Wifi;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.transferfile.ui.MainActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * Created by suxiongye on 6/10/16.
 */
public class WiFiAdmin {

    //调试标签
    public String SCAN_TAG = "SCAN";

    //状态标签
    private static boolean hasDevice = false;
    public static boolean isConnected = false;

    private WifiManager wifiManager;
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private WiFiDirectBroadcastReceiver receiver;
    private static FileServer fileServer;
    private MainActivity activity;

    public WiFiAdmin(WifiP2pManager manager, MainActivity activity) {
        this.manager = manager;
        this.activity = activity;
        this.channel = manager.initialize(activity, Looper.getMainLooper(), null);
        wifiManager = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
        //初始化接收器
        receiver = new WiFiDirectBroadcastReceiver(manager, channel, activity);
    }
    /**
     * open the wifi if the wifi off
     */
    public void openWifi(){
        if(!wifiManager.isWifiEnabled()){
            wifiManager.setWifiEnabled(true);
        }
    }
    /**
     * 获取接收器
     *
     * @return
     */
    public WiFiDirectBroadcastReceiver getWiFiBroadcastReceiver() {
        return this.receiver;
    }

    /**
     * 扫描可用设备，若有可用设备返回设备列表，否则返回空
     */
    public List<WifiP2pDevice> scanWifiDevice() {
        Log.e(SCAN_TAG, "scanWifiDevice");
        //开启wifi
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        //寻找设备
        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                WiFiAdmin.hasDevice = true;
            }

            @Override
            public void onFailure(int reason) {
                WiFiAdmin.hasDevice = false;
                Log.e(SCAN_TAG, "not find");
            }
        });
        if (WiFiAdmin.hasDevice) {
            showDeviceInLog();
            return getDeviceList();
        } else return null;
    }

    /**
     * 返回找到的设备列表
     *
     * @return WifiP2pDevice列表
     */
    public List<WifiP2pDevice> getDeviceList() {
        return receiver.getDevices();
    }

    /**
     * 在Log中显示可用设备
     */
    public void showDeviceInLog() {
        List<WifiP2pDevice> devices = receiver.getDevices();
        for (WifiP2pDevice device : devices) {
            Log.e(SCAN_TAG, "mac:" + device.deviceAddress);
            Log.e(SCAN_TAG, "name" + device.deviceName);
        }
    }

    /**
     * 建立p2p链接
     *
     * @param device
     */
      public void connectDevice(WifiP2pDevice device) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        manager.connect(channel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int reason) {

            }
        });
        if (isConnected) {
            //开启后台文件接收
            if (fileServer == null){
                fileServer = new FileServer();
                fileServer.execute();
            }
            activity.getApplicationContext().sendBroadcast(new Intent("WiFiConnectSuccess"));
            Toast.makeText(this.activity, "已连接上", Toast.LENGTH_SHORT).show();
        } else {
            fileServer = null;
            Toast.makeText(this.activity, "正在连接....", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 获取正在链接中的p2p设备ip
     *
     * @return
     */
    public String getP2pDeviceIP() {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;
            while ((line = br.readLine()) != null) {

                String[] splitted = line.split(" +");
                if (splitted != null && splitted.length >= 4) {
                    // Basic sanity check
                    String device = splitted[5];

                    if (device.matches(".*p2p.*")) {
                        return splitted[0];
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 根据路径发送文件
     */
    public void sendFileByPath(String filePath) {
        File file = new File(filePath);
        Log.e("filePath", filePath);
        Log.e("fileName", file.getName());
        Log.e("ip", getP2pDeviceIP());
        SendThread sendThread = new SendThread(getP2pDeviceIP(), filePath, file.getName());
        sendThread.run();
    }

    /**
     * 查看设备是否在已链接状态
     * @return
     */
    public boolean checkIfConnect(){
        return this.isConnected;
    }

    public void createFile() {
        File file = new File(Environment.getExternalStorageDirectory() + "/WifiBase/123.txt");

    }

}
