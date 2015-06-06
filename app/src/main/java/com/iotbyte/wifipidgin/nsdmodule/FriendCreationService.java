package com.iotbyte.wifipidgin.nsdmodule;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.iotbyte.wifipidgin.chat.ChatManager;
import com.iotbyte.wifipidgin.friend.Friend;
import com.iotbyte.wifipidgin.message.FriendCreationRequest;

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
            FriendCreationQueue friendCreationQueue = FriendCreationQueue.getInstance();
            Log.d(TAG, "FriendCreationServiceThread Started");

            while (!Thread.currentThread().isInterrupted()) {
                //Processing the queue
                //Log.d(TAG, "FriendCreationServiceThread Started");
                while (!friendCreationQueue.isEmptyFriendCreationQueue()){
                    try {
                        Friend creatingFriend = friendCreationQueue.topFriendCreationQueue();
                        Log.d(TAG, "Creating friend " + creatingFriend.getMac().toString());
                        FriendCreationRequest creationRequest = new FriendCreationRequest(creatingFriend);
                        ChatManager chatManager = ChatManager.getInstance();
                        chatManager.enqueueOutGoingMessageQueue(creationRequest.convertMessageToJson());
                        TimeUnit.MILLISECONDS.sleep(delayInterval);
                        friendCreationQueue.dequeueFriendCreationQueue();

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
