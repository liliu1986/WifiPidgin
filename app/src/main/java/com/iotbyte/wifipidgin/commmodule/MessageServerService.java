package com.iotbyte.wifipidgin.commmodule;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.iotbyte.wifipidgin.chat.ChatManager;

import java.net.ServerSocket;

/**
 * Created by fire on 29/04/15.
 */
public class MessageServerService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(MSG_SERVICE_TAG, "Service Started !!!");
        mMessageServer.startServer();

        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        mMessageServer = new MessageServer(getApplicationContext());
        mMessageServer.setMessageReceivingListener(new MessageReceivingListener(){
            @Override
            public void onMessageReceived(String msg){
                Log.d(MSG_SERVICE_TAG, "A message has been received: " + msg);

                ChatManager.getInstance(getApplicationContext()).enqueueIncomingMessageQueue(msg);
                Intent intent = new Intent();
                intent.setAction(MY_ACTION);
                intent.putExtra("MSGREC", msg);
                sendBroadcast(intent);
            }
        });
    }

    private final String MSG_SERVICE_TAG = "MessageService";

    private MessageServer mMessageServer;
    private Handler mUpdateHandler;

    private ServerSocket mServerSocket = null;
    private int mPort = -1;

    public final static String MY_ACTION = "MY_ACTION";
}
