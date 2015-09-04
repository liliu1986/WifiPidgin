package com.iotbyte.wifipidgin.commmodule;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;

import com.iotbyte.wifipidgin.dao.DaoFactory;
import com.iotbyte.wifipidgin.dao.FriendDao;
import com.iotbyte.wifipidgin.friend.Friend;
import com.iotbyte.wifipidgin.friend.Myself;
import com.iotbyte.wifipidgin.nsdmodule.NsdServer;
import com.iotbyte.wifipidgin.utils.Utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

/**
 * Created by fire on 19/04/15.
 */
public class MessageServer   {

    private Handler mUpdateHandler;
    private NsdServer mNsdServer;
    public MessageServer() {}
    private Context mContext;
    private Handler handler;
    public static boolean serviceStarted = false;
    public MessageServer(Context inContext, Handler inHandler) {
        mContext = inContext;
        handler = inHandler;
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
                // We are using a fixed port from now on.
                mServerSocket = new ServerSocket(port);
                setLocalPort(mServerSocket.getLocalPort());

                //Noticed that after server socket initialized, need a period to allow the actual
                //connection to be able to establish.
                try {
                    TimeUnit.MILLISECONDS.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //After the message server socket is created, broadcast it.
                mNsdServer = NsdServer.getInstance(getServerContext(), mServerSocket);

                //Update the ip and port for User self
                FriendDao fd = DaoFactory.getInstance()
                        .getFriendDao(mContext, DaoFactory.DaoType.SQLITE_DAO, null);
                Friend selfFriend = fd.findById(Myself.SELF_ID);
                Myself self = new Myself(selfFriend);


                if (self.isFavourite()){
                    //If the user has set his/herself to visible, broadcast.
                    mNsdServer.broadcastService();
                }

                InetAddress myIP = null;
                WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wInfo = wifiManager.getConnectionInfo();
                String macString = wInfo.getMacAddress();
                try {
                    myIP = InetAddress.getByName(Utils.getIPAddress(true));
                    int myPort = mServerSocket.getLocalPort();
                    if (!self.getIp().equals(myIP) || self.getPort() != myPort){
                        Log.d(MSG_SERVER_TAG, "Old Server IP: " + self.getIp());
                        Log.d(MSG_SERVER_TAG, "Server IP: " + myIP);
                        self.setIp(myIP);
                        self.setPort(myPort);
                        self.setMac(Utils.macAddressHexStringToByte(macString));

                        fd.update(self);
                    }
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (Exception e){
                    e.printStackTrace();
                } finally {
                    serviceStarted = true;
                }


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
                InputStreamReader is = new InputStreamReader(inputStream);
                StringBuilder sb=new StringBuilder();
                BufferedReader br = new BufferedReader(is);
                String read = br.readLine();
                while(read != null) {
                    //System.out.println(read);
                    sb.append(read);
                    read =br.readLine();

                }
                inputStream.close();
                /*
                msgRec = new StringBuffer();


                bytesRead = inputStream.read(buffer);
                Log.d(MSG_SERVER_TAG, "bytesRead: " + bytesRead);

                byteArrayOutputStream.write(buffer, 0, bytesRead);
                msgRec.append(byteArrayOutputStream.toString("UTF-8"));

                inputStream.close();
                Log.d(MSG_SERVER_TAG, "Got msg from client " + msgRec);
                */
                //TODO - After the message is received, need to handle it/have a custom listener.
                if(mMsgRecListenerForThread != null){
                    mMsgRecListenerForThread.onMessageReceived(sb.toString());
                    Log.v(MSG_SERVER_TAG,sb.toString());
                    //mMsgRecListenerForThread.onMessageReceived(msgRec.toString());
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
            }catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
    private void runOnUiThread(Runnable runnable) {
        handler.post(runnable);
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
        if (mNsdServer != null){
            mNsdServer.tearDown();
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

    private int MAX_MSG_SIZE = 102400;
    private int port = 45871;
}

