package com.iotbyte.wifipidgin.commmodule;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.iotbyte.wifipidgin.nsdmodule.NsdServer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by fire on 19/04/15.
 */
public class MessageServer   {

    private Handler mUpdateHandler;
    private NsdServer mNsdServer;
    public MessageServer() {}
    private Context mContext;
    public MessageServer(Context inContext) {
        mContext = inContext;
    }
    /**
     * Sets the listener for receiving a msg on this server
     */
    public void setMessageReceivingListener(MessageReceivingListener MsgReccListener){
        mMessageReceivingListener = MsgReccListener;
    }
    class ServerThread implements Runnable {

        @Override
        public void run() {

            try {
                // Since discovery will happen via Nsd, we don't need to care which port is
                // used.  Just grab an available one  and advertise it via Nsd.
                mServerSocket = new ServerSocket(0);
                setLocalPort(mServerSocket.getLocalPort());

                //After the message server socket is created, broadcast it.
                mNsdServer = new NsdServer(getServerContext(), mServerSocket);
                mNsdServer.initializeNsdServer();
                mNsdServer.broadcastService();


                while (!Thread.currentThread().isInterrupted()) {
                    Log.d(MSG_SERVER_TAG, "ServerSocket Created, awaiting connection at port: "
                            + mServerSocket.getLocalPort());

                    //Waiting for a remote friend to connect
                    //Wait for the connection representing socket.
                    Socket socket = mServerSocket.accept();
                    Log.d(MSG_SERVER_TAG, "Got a connection from "+socket.getInetAddress() + ":"
                            + socket.getPort());


                    Thread.UncaughtExceptionHandler h = new Thread.UncaughtExceptionHandler() {
                        public void uncaughtException(Thread th, Throwable ex) {
                            Log.d(MSG_SERVER_TAG,"Uncaught exception: ");
                            //System.out.println("Uncaught exception: " + ex);
                        }
                    };

                    ServerReceivingThread mServerReceivingThread = new ServerReceivingThread(socket, mMessageReceivingListener);
                    mServerReceivingThread.setPriority(mServerReceivingThread.getThreadGroup().getMaxPriority());

                    mServerReceivingThread.setUncaughtExceptionHandler(h);

                    mServerReceivingThread.run();

                }
            } catch (IOException e) {
                Log.e(MSG_SERVER_TAG, "Error creating ServerSocket: ", e);
                e.printStackTrace();
            }
        }
    }
    private Context getServerContext(){
        return mContext;
    }
    /**
     * Starts a thread to wait for the message from client.
     *
     */
    private class ServerReceivingThread extends Thread {
        private Socket hostThreadSocket;
        private StringBuffer msgRec;
        private MessageReceivingListener mMsgRecListenerForThread;

        ServerReceivingThread(Socket socket, MessageReceivingListener mMessageReceivingListener) {
            hostThreadSocket = socket;
            mMsgRecListenerForThread = mMessageReceivingListener;
        }
        @Override
        public void run() {

            try {
                Log.d(MSG_SERVER_TAG, "Start receiving the msg");

                ByteArrayOutputStream byteArrayOutputStream =
                        new ByteArrayOutputStream(MAX_MSG_SIZE);
                byte[] buffer = new byte[MAX_MSG_SIZE];

                int bytesRead;
                //String messageStr = null;
                InputStream inputStream = hostThreadSocket.getInputStream();
                msgRec = new StringBuffer();


                bytesRead = inputStream.read(buffer);
                byteArrayOutputStream.write(buffer, 0, bytesRead);
                msgRec.append(byteArrayOutputStream.toString("UTF-8"));

                inputStream.close();
                Log.d(MSG_SERVER_TAG, "Got msg from client " + msgRec);

                //TODO - After the message is received, need to handle it/have a custom listener.
                if(mMsgRecListenerForThread != null){
                    mMsgRecListenerForThread.onMessageReceived(msgRec.toString());
                }else{
                    Log.e(MSG_SERVER_TAG, "ChatMessage Receiving Listener is not set");
                }
                /*
                Bundle messageBundle = new Bundle();
                messageBundle.putString("msg", msgRec);

                ChatMessage message = new ChatMessage();
                message.setData(messageBundle);
                mUpdateHandler.sendMessage(message);
                */

            }catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    /**
     * Starts a thread to send some reply msg back to the client(in case needed)
     *
     */
    private class ServerReplyThread extends Thread {
        private Socket hostThreadSocket;
        private String msgReply;

        ServerReplyThread(Socket socket, String replyMsg) {
            hostThreadSocket = socket;
            msgReply = replyMsg;
        }
        @Override
        public void run() {
            OutputStream outputStream;
            try {
                Log.d(MSG_SERVER_TAG, "Replying to the connected client");

                outputStream = hostThreadSocket.getOutputStream();
                PrintStream printStream = new PrintStream(outputStream);
                printStream.print(msgReply);
                printStream.close();

            }catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    /**
     * set the port number the server is listening to.
     *
     */
    public void setLocalPort(int port) {
        mPort = port;
    }

    /**
     * Gets the port number the server is listening to.
     * @return the port number
     */
    public int getLocalPort(){return mPort;}


    /**
     * Terminate the server thread
     *
     */
    public void tearDown(){
        if(mThread != null){
            mThread.interrupt();
        }
    }

    /**
     * Start the server thread.
     *
     */
    public void startServer(){
        mThread = new Thread(new ServerThread());
        mThread.start();
    }
    private final String MSG_SERVER_TAG = "MessageServer";

    private ServerSocket mServerSocket = null;
    private int mPort = -1;
    Thread mThread = null;
    private MessageReceivingListener mMessageReceivingListener;

    private int MAX_MSG_SIZE = 1024;
}

