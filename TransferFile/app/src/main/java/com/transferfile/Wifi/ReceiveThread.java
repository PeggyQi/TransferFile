package com.transferfile.Wifi;

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by suxiongye on 6/11/16.
 */
public class ReceiveThread extends Thread {
    Socket socket = null;
    Socket titleSocket = null;
    private String fileName = "";

    private String TITLETAG = "@@@";

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
        try {
            // get filename
            fileName = getFileName();
            System.out.println(fileName);
            InputStream is = socket.getInputStream();

            File file = null;
            if (fileName != null && !fileName.equals("")) {
                file = receiveFile(is, fileName);
            }
            socket.shutdownInput();

            // response
            OutputStream os = socket.getOutputStream();
            PrintWriter pw = new PrintWriter(os);
            pw.write("recieve success");
            pw.flush();

            os.close();
            pw.close();
            is.close();
            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }

    public File receiveFile(InputStream inputStream, String fileName) {
        File file = new File(Environment.getExternalStorageDirectory()+"/WifiBase/" + fileName);
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
        return file;
    }
}
