package com.transferfile.Wifi;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by suxiongye on 6/11/16.
 */
public class ReceiveThread implements Runnable {
    Socket socket = null;
    Socket titleSocket = null;
    private String fileName = "";

    public ReceiveThread(Socket titleSocket, Socket socket) {
        this.titleSocket = titleSocket;
        this.socket = socket;
    }

    // get the file name
    private String getFileName() {
        InputStream is = null;
        byte[] buf = new byte[1024];
        String localFileName = "";
        try {
            int len = 0;
            is = titleSocket.getInputStream();
            while ((len = is.read(buf)) != -1) {
                is.read(buf);
                localFileName += new String(buf);
            }
            localFileName = localFileName.trim();
            System.out.println(localFileName.length());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                is.close();
                titleSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return localFileName;
    }

    public void run() {
        // TODO Auto-generated method stub
        OutputStream os = null;
        InputStream is = null;
        PrintWriter pw = null;

        try {
            // get filename
            fileName = getFileName();
            Log.e("Receive", "接收文件标题为" + fileName);
            is = socket.getInputStream();

            if (fileName != null && !fileName.equals("")) {
                Log.e("Receive", "开始接收文件内容");
                receiveFile(is, fileName);
            }
            socket.shutdownInput();

            // response
            os = socket.getOutputStream();
            pw = new PrintWriter(os);
            pw.write("success");
            pw.flush();
            Log.e("Receive", "文件接收完成");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null)
                    os.close();
                if (pw != null)
                    pw.close();
                if (is != null)
                    is.close();
                if (socket != null)
                    socket.close();
            } catch (Exception e) {

            }
        }
    }

    public void receiveFile(InputStream inputStream, String fileName) {
        File file = new File(Environment.getExternalStorageDirectory()+"/TransferFile/" + fileName);
        File dirs = new File(file.getParent());

        // create the file if not exists
        if (!dirs.exists())
            dirs.mkdirs();
        try {
            System.out.println("create:" + file.getPath());
            file.createNewFile();
        } catch (IOException e1) {
            // TODO Auto-generated catch block

            e1.printStackTrace();
        }

        FileOutputStream os = null;
        try {
            os = new FileOutputStream(file);

            byte buf[] = new byte[1024];

            int len = -1;

            while ((len = inputStream.read(buf)) != -1) {
                os.write(buf, 0, len);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
}
