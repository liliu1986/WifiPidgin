package com.iotbyte.wifipidgin.commmodule;

import android.util.Log;

import com.iotbyte.wifipidgin.channel.Channel;
import com.iotbyte.wifipidgin.chat.Message;
import com.iotbyte.wifipidgin.friend.Friend;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by fire on 19/04/15.
 */
public class MessageClient {

    private final String MSG_CLIENT_TAG = "MessageClient";

    public MessageClient(){

    }


    /**
     * Start msg sending thread
     * @param address the destination IP address
     * @param port the destication port number
     * @param msg The msg that is about to be sent.
     *
     */
    public void sendMsg(InetAddress address, int port, String msg){
        this.mAddress = address;
        this.PORT = port;
        this.MSG = msg;

        mSendThread = new Thread(new MessageSendingThread());
        mSendThread.start();
    }

    class MessageSendingThread implements Runnable {
        BlockingQueue<String> mMessageQueue;

        String response = "";

        public MessageSendingThread() {
            mMessageQueue = new ArrayBlockingQueue<String>(QUEUE_CAPACITY);
        }

        @Override
        public void run() {
            Log.d(MSG_CLIENT_TAG, "MessageSendingThread started");
            /*TODO - Need handle the scenario that if the server is busy and unable to establish connection
             */
            try {
                if (getSocket() == null) {
                    Log.d(MSG_CLIENT_TAG, "Trying to connect to " + mAddress.toString() + ":" + PORT);
                    setSocket(new Socket(mAddress, PORT));
                    Log.d(MSG_CLIENT_TAG, "Sending-side socket initialized.");
                } else {
                    Log.d(MSG_CLIENT_TAG, "Socket already initialized. skipping!");
                }

                sendMessage(MSG);

            } catch (UnknownHostException e) {
                Log.d(MSG_CLIENT_TAG, "Initializing socket failed, UHE", e);
            } catch (IOException e) {
                Log.d(MSG_CLIENT_TAG, "Initializing socket failed, IOE.", e);
            }
        }

    }


    private synchronized void setSocket(Socket socket) {

        if (mSocket != null) {
            if (mSocket.isConnected()) {
                try {
                    mSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        mSocket = socket;
    }

    public Socket getSocket() {
        return mSocket;
    }

    private void sendMessage(String msg) {
        try {
            Socket socket = getSocket();
            if (socket == null) {
                Log.d(MSG_CLIENT_TAG, "Socket is null, wtf?");
            } else if (socket.getOutputStream() == null) {
                Log.d(MSG_CLIENT_TAG, "Socket output stream is null, wtf?");
            }

            PrintWriter out = new PrintWriter(
                    new BufferedWriter(
                            new OutputStreamWriter(getSocket().getOutputStream())), true);
            out.println(msg);
            out.flush();
            //updateMessages(msg, true);
        } catch (UnknownHostException e) {
            Log.d(MSG_CLIENT_TAG, "Unknown Host", e);
        } catch (IOException e) {
            Log.d(MSG_CLIENT_TAG, "I/O Exception", e);
        } catch (Exception e) {
            Log.d(MSG_CLIENT_TAG, "Error3", e);
        }
        Log.d(MSG_CLIENT_TAG, "Client sent message: " + msg);
    }
    private Socket mSocket;

    private Thread mSendThread;

    private int QUEUE_CAPACITY = 10;

    private InetAddress mAddress;
    private int PORT;
    private String MSG;


}
