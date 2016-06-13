package com.transferfile.Wifi;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by suxiongye on 6/11/16.
 */
public class SendThread extends Thread {

    String deviceHost = null;
    String filePath = null;
    String fileName = null;

    public SendThread(String deviceHost, String filePath, String fileName){
        this.deviceHost = deviceHost;
        this.filePath = filePath;
        this.fileName = fileName;
    }

    @Override
    public void run() {
        if(deviceHost != null && WiFiAdmin.isConnected == true)
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
        Log.e("send ex", "sendFile");
        try {
            Log.e("send ex", "sendFile1");
            titleSocket = new Socket(deviceHost, 8887);
            Log.e("send ex", "sendFile2");
            socket = new Socket(deviceHost, 8888);
            Log.e("send ex", "sendFile3");
            os = socket.getOutputStream();
            Log.e("send ex", "sendFile4");
            titleOs = titleSocket.getOutputStream();

            File file = new File(filePath);
            fileInputStream = new FileInputStream(file);
            int len = -1;
            byte buf[] = new byte[1024];

            //发送文件名
            Log.e("send ex", "发送文件名前");
            titleOs.write(fileName.getBytes(), 0, fileName.getBytes().length);
            titleOs.close();
            Log.e("send ex", "发送文件名后");
            //发送文件内容
            while ((len = fileInputStream.read(buf)) != -1) {
                os.write(buf, 0, len);
            }

            socket.shutdownOutput();

            // get response
            is = socket.getInputStream();

            br = new BufferedReader(new InputStreamReader(is));

            String info = null;
            while ((info = br.readLine()) != null) {
                System.out.println("Response:" + info);
            }

        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            Log.e("send ex1", e.toString());
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e("send ex", e.toString());
        } finally {
            try {
                if (socket != null) {
                    socket.close();
                }
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
                if(titleSocket != null){
                    titleSocket.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
