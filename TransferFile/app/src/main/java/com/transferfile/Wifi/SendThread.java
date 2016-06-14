package com.transferfile.Wifi;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.transferfile.ui.MainActivity;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by suxiongye on 6/11/16.
 */
public class SendThread implements Runnable {
    public static String SendSuccess="SendSuccess";
    static String deviceHost = null;
    String filePath = null;
    String fileName = null;
    Activity activity;
    public SendThread(String deviceHost, String filePath, String fileName,Activity activity) {
        this.deviceHost = deviceHost;
        this.filePath = filePath;
        this.fileName = fileName;
        this.activity=activity;
    }

    @Override
    public void run() {
        if (deviceHost != null && WiFiAdmin.isConnected == true)
            sendFile(deviceHost, filePath, fileName);
    }

    private void sendFile(String deviceHost, String filePath, String fileName) {
        Socket titleSocket = null;
        Socket socket = null;
        OutputStream os = null;
        OutputStream titleOs = null;
        FileInputStream fileInputStream = null;
        InputStream is = null;
        BufferedReader br = null;
        String info = null;
        try {
            titleSocket = new Socket(deviceHost, 8887);
            socket = new Socket(deviceHost, 8888);
            os = socket.getOutputStream();
            titleOs = titleSocket.getOutputStream();
            File file = new File(filePath);
            fileInputStream = new FileInputStream(file);
            int len = -1;
            byte buf[] = new byte[1024];
            //发送文件名
            Log.e("send", "开始发送文件名");
            titleOs.write(fileName.getBytes(), 0, fileName.getBytes().length);
            titleOs.close();
            Log.e("send", "开始发送文件内容");
            //发送文件内容
            while ((len = fileInputStream.read(buf)) != -1) {
                os.write(buf, 0, len);
            }

            socket.shutdownOutput();

            // get response
            is = socket.getInputStream();

            br = new BufferedReader(new InputStreamReader(is));


            while ((info = br.readLine()) != null) {
                //接收信息为success表示发送成功
                System.out.println("Response:" + info);
                if(MainActivity.firstSendBroadCast==false) {
                    MainActivity.firstSendBroadCast=true;
                    activity.getApplicationContext().sendBroadcast(new Intent(SendThread.SendSuccess));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("send",e.toString());
        } finally {
            //
//            if(!info.equals("success")){
//                //接收失败提示
//            }
            try {
                if (socket != null) {

                    socket.close();

                    if (os != null) {
                        os.close();
                    }
                    if (fileInputStream != null) {
                        fileInputStream.close();
                    }
                    if (is != null) {
                        is.close();
                    }
                    if (br != null) {
                        br.close();
                    }
                    if (titleSocket != null) {
                        titleSocket.close();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
