package com.iotbyte.wifipidgin.chat;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by yefwen@iotbyte.com on 20/05/15.
 *
 * OutgoingMessageHandlingService starts the OutgoingMessageHandler
 */
public class OutgoingMessageHandlingService extends Service{

    private OutgoingMessageHandler outgoingMessageHandler;
//    private Handler handler;
    private final String OUTGOING_MESSAGE_HANDLING_SERVICE = "out msg serv";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
  //      handler = new Handler();
        outgoingMessageHandler = new OutgoingMessageHandler(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        outgoingMessageHandler.startHandler();

        Log.d(OUTGOING_MESSAGE_HANDLING_SERVICE, "the outgoing message handler Service Starts");

        return Service.START_STICKY;
    }
}
