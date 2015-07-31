package com.iotbyte.wifipidgin.commmodule;

import android.content.Context;
import android.util.Log;

import com.iotbyte.wifipidgin.chat.ChatManager;
import com.iotbyte.wifipidgin.message.Message;
import com.iotbyte.wifipidgin.message.MessageFactory;

import org.json.JSONException;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by fire on 19/04/15.
 */
public class MessageClient {

    private final String MSG_CLIENT_TAG = "MessageClient";
    private MessageSendingListener messageSendingListener;

    private Context mContext;

    public MessageClient(Context context){
        mContext = context;
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

        Thread.UncaughtExceptionHandler h = new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread th, Throwable ex) {
                System.out.println("Uncaught exception: " + ex);
            }
        };


        messageSendingListener = new MessageSendingListener() {
            @Override
            public void onMessageNotSent(String msg) {
                Log.d(MSG_CLIENT_TAG, "The message is not sent, retrying");
                if (mContext != null){
                    try {
                        Message tryMsg = MessageFactory.buildMessageByJson(msg, mContext);
                        if (tryMsg.canRetry()){
                            tryMsg.incrementMsgRetyCounter();
                            ChatManager
                                    .getInstance(mContext)
                                    .enqueueOutGoingMessageQueue(tryMsg);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.d(MSG_CLIENT_TAG, "Context is not set");
                }
            }
        };

        mSendThread = new Thread(new MessageSendingThread());
        mSendThread.start();
    }

    class MessageSendingThread implements Runnable {
        //BlockingQueue<String> mMessageQueue;

        //String response = "";

        public MessageSendingThread() {
            //mMessageQueue = new ArrayBlockingQueue<String>(QUEUE_CAPACITY);
        }

        @Override
        public void run() {
            Log.d(MSG_CLIENT_TAG, "MessageSendingThread started");
            /*TODO - Need handle the scenario that if the server is busy and unable to establish connection
             */
            Log.d(MSG_CLIENT_TAG, "Trying to connect to " + mAddress.toString() + ":" + PORT);
            Socket newSocket = createSocket();
            if (newSocket != null){
                //setSocket(newSocket);
                Log.d(MSG_CLIENT_TAG, "Sending-side socket initialized.");
                if (sendMessage(MSG, newSocket)){
                    Log.d (MSG_CLIENT_TAG, "The message has been successfully sent out.");
                }else{
                    if (messageSendingListener != null){
                        messageSendingListener.onMessageNotSent(MSG);
                    }
                }

                //Close and delete the socket
                try {
                    newSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    newSocket = null;
                    System.gc();
                }

            } else {
                Log.w(MSG_CLIENT_TAG, "Sending-side socket cannot be initialized");
                if (messageSendingListener != null){
                    messageSendingListener.onMessageNotSent(MSG);
                }
            }
        }

    }

    private Socket createSocket(){
        Socket newSocket = null;
        try {
            newSocket  = new Socket(mAddress, PORT);
        } catch (UnknownHostException e) {
            Log.d(MSG_CLIENT_TAG, "Initializing socket failed, UHE", e);
        } catch (IOException e) {
            Log.d(MSG_CLIENT_TAG, "Initializing socket failed, IOE.", e);
        }
        return newSocket;
    }

    private boolean sendMessage(String msg, Socket inSocket) {

        boolean sendSuccessfully = false;

        try {
            Socket socket = inSocket;
            if (socket == null || socket.isClosed()) {
                Log.d(MSG_CLIENT_TAG, "Socket is null or closed, wtf?");
            } else if (socket.getOutputStream() == null) {
                Log.d(MSG_CLIENT_TAG, "Socket output stream is null, wtf?");
            } else {
                PrintWriter out = new PrintWriter(
                        new BufferedWriter(
                                new OutputStreamWriter(socket.getOutputStream())), true);
                out.println(msg);
                out.flush();

                socket.close();
                Log.d(MSG_CLIENT_TAG, "Client sent message: " + msg);
            }
            sendSuccessfully = true;
        } catch (UnknownHostException e) {
            sendSuccessfully = false;
            Log.d(MSG_CLIENT_TAG, "Unknown Host", e);
        } catch (IOException e) {
            sendSuccessfully = false;
            Log.d(MSG_CLIENT_TAG, "I/O Exception", e);
        } catch (Exception e) {
            sendSuccessfully = false;
            Log.d(MSG_CLIENT_TAG, "Error3", e);
        } finally {
            return sendSuccessfully;
        }

    }
    private Socket mSocket;

    private Thread mSendThread;

    private InetAddress mAddress;
    private int PORT;
    private String MSG;


}
