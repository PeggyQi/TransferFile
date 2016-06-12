package com.transferfile.Wifi;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by suxiongye on 6/11/16.
 */
public class FileServer extends AsyncTask{

    @Override
    protected Object doInBackground(Object[] params) {
        this.startReceive();
        return null;
    }

    public void startReceive() {
        ServerSocket serverSocket = null;
        ServerSocket titleServerSocket = null;
        try {
            titleServerSocket = new ServerSocket(8887);
            serverSocket = new ServerSocket(8888);
            int count = 0;
            System.out.println("Ready to start server");

            Socket titleSocket = null;
            Socket socket = null;
            while (true) {
                count++;
                titleSocket = titleServerSocket.accept();
                socket = serverSocket.accept();
                ReceiveThread receiveThread = new ReceiveThread(titleSocket, socket);
                receiveThread.start();
                System.out.println("Receive count:" + count);
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
                ;
            }
        }
    }
}
