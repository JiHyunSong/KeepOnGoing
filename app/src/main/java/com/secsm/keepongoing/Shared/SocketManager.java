package com.secsm.keepongoing.Shared;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SocketManager {

    public final static String HOST = KogPreference.CHAT_IP;
    public final static int PORT = KogPreference.CHAT_PORT;

    private static Socket socket;

    public static Socket getSocket() throws IOException
    {
        if( socket == null)
            socket = new Socket();

        if( !socket.isConnected() )
            socket.connect(new InetSocketAddress(HOST, PORT));

        return socket;
    }

    public static void closeSocket() throws IOException
    {
        if ( socket != null )
            socket.close();
    }

    public static void sendMsg(String msg) throws IOException
    {
        getSocket().getOutputStream().write((msg + '\n').getBytes());
    }
}