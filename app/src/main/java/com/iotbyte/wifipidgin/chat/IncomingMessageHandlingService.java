package com.iotbyte.wifipidgin.chat;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by yefwen@iotbyte.com on 09/05/15.
 *
 * IncomingMessageHandlingService starts the incomingMessageHandler
 */
public class IncomingMessageHandlingService extends Service {

    private IncomingMessageHandler incomingMessageHandler;
    private final String INCOMING_MESSAGE_HANDLING_SERVICE = "inc msg serv";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        incomingMessageHandler = new IncomingMessageHandler(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        incomingMessageHandler.startHandler();

        Log.d(INCOMING_MESSAGE_HANDLING_SERVICE,"the incoming message handler Service Starts");

        return Service.START_STICKY;
    }
}
