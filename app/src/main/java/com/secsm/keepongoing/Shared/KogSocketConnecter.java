package com.secsm.keepongoing.Shared;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.Handler;
import android.util.Log;


public class KogSocketConnecter {

    private final static String TAG = "[KogSocketConnecter]";

    private static Socket socket = null;
    private static KogSocketConnecter instance = null;

    private KogSocketConnecter() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    socket = new Socket(KogPreference.CHAT_IP, KogPreference.CHAT_PORT);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public Socket getSocket(){
        return socket;
    }

    public boolean close() {
        try {
            socket.close();
            return true;
        }catch (IOException e)
        {
            return false;
        }
    }

    public synchronized static KogSocketConnecter getInstance() {

        if (instance == null)
            instance = new KogSocketConnecter();

        return instance;
    }

    public void sendMsg(final String msg, final Handler handler) {
        try {
            background(msg, handler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void background(final String msg, final Handler handler)
            throws IOException {
        new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                if(socket == null){
//                  socket = new Socket(Conf.SERVER_IP, Conf.SERVER_PORT);
                }

                PrintWriter out = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream())),
                        true);
                out.println(msg);

                if (handler != null)
                    handler.sendEmptyMessage(0);

                //socket.close();

            } catch (Exception e) {
                Log.e(TAG, "err: " + e.toString());
            }
        }
    }).start();
}
}