package com.transferfile.Wifi;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by suxiongye on 6/11/16.
 */
public class FileServer implements Runnable{

    @Override
    public void run() {
        startReceive();
    }

    public void startReceive() {
        ServerSocket serverSocket = null;
        ServerSocket titleServerSocket = null;
        try {
            titleServerSocket = new ServerSocket(8887);
            serverSocket = new ServerSocket(8888);
            int count = 0;

            Log.e("Receive","开始启动文件接收服务器");
            Socket titleSocket = null;
            Socket socket = null;
            while (true) {
                count++;
                titleSocket = titleServerSocket.accept();
                Log.e("Receive","接收到文件标题发送请求");
                socket = serverSocket.accept();
                Log.e("Receive","接收到文件内容发送请求");
                Thread receiveThread = new Thread(new ReceiveThread(titleSocket, socket));
                receiveThread.start();
                Log.e("Receive","接收到第"+count+"个文件");
                //断开链接则跳出
                if (WiFiAdmin.isConnected == false) break;
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }
    }
}
