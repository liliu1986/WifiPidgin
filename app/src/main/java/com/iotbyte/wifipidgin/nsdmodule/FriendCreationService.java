package com.iotbyte.wifipidgin.nsdmodule;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.iotbyte.wifipidgin.friend.Friend;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

/**
 * Created by fire on 20/05/15.
 */
public class FriendCreationService extends Service  {

    @Override
    public IBinder onBind(Intent intent) {return null;}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "FriendCreationService Started !!!");
        mThread = new Thread(new FriendCreationServiceThread());
        mThread.start();

        return Service.START_STICKY;
    }
    @Override
    public void onCreate() {
    }
    class FriendCreationServiceThread implements Runnable {

        @Override
        public void run() {
            NsdClient nsdClientInstance = NsdClient.getInstance(getApplicationContext());
            Log.d(TAG, "FriendCreationServiceThread Started");

            while (!Thread.currentThread().isInterrupted()) {
                //Processing the queue
                //Log.d(TAG, "FriendCreationServiceThread Started");
                while (!nsdClientInstance.isEmptyFriendCreationQueue()){
                    try {
                        TimeUnit.MILLISECONDS.sleep(delayInterval);
                        Friend creatingFriend = nsdClientInstance.dequeueFriendCreationQueue();
                        Log.d(TAG, "Creating friend " + creatingFriend.getMac().toString());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private final String TAG = "FriendCreationService";
    private Thread mThread;
    private final int delayInterval = 100;
}
