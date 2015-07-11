package com.iotbyte.wifipidgin.nsdmodule;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.iotbyte.wifipidgin.chat.ChatManager;
import com.iotbyte.wifipidgin.commmodule.MessageServer;
import com.iotbyte.wifipidgin.dao.DaoFactory;
import com.iotbyte.wifipidgin.dao.FriendDao;
import com.iotbyte.wifipidgin.friend.Friend;
import com.iotbyte.wifipidgin.message.FriendCreationRequest;
import com.iotbyte.wifipidgin.utils.Utils;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * Created by fire on 20/05/15.
 */
public class FriendStatusTrackingService extends Service  {

    @Override
    public IBinder onBind(Intent intent) {return null;}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "FriendStatusTrackingService Started !!!");
        fd = DaoFactory.getInstance()
                .getFriendDao(getApplicationContext(), DaoFactory.DaoType.SQLITE_DAO, null);

        mThread = new Thread(new FriendStatusTrackingServiceThread());
        mThread.start();

        return Service.START_STICKY;
    }
    @Override
    public void onCreate() {
    }
    class FriendStatusTrackingServiceThread implements Runnable {
        @Override
        public void run() {
            FriendOnlineHashMap friendOnlineHashMap = FriendOnlineHashMap.getInstance();
            Log.d(TAG, " Service Started");

            while (!Thread.currentThread().isInterrupted()) {
                //Processing the queue
                //Log.d(TAG, "FriendStatusTrackingService Started: " + MessageServer.serviceStarted);
                while (MessageServer.serviceStarted && !friendOnlineHashMap.isEmpty()){
                    for (Friend friend : friendOnlineHashMap.getValues()) {
                        long lastOnlineTime = friend.getLastOnlineTimeStamp().getTime();
                        long currentTime = System.currentTimeMillis();
                        if (Math.abs(currentTime - lastOnlineTime) >= onlineTimeoutPeriod){
                            //This user has been offline too long, removing from the hash map
                            Log.d(TAG, "This user has been offline too long, removing from the hash map " + Utils.macAddressByteToHexString(friend.getMac()));
                            Friend dbFriend = fd.findByMacAddress(friend.getMac());
                            if (dbFriend == null) {
                                Log.d(TAG, "Couldn't find the friend in db: " + Utils.macAddressByteToHexString(friend.getMac()));
                                continue;
                            }
                            //if (dbFriend.isFavourite()){
                            //    //If this friend is saved as favorite, just update the status
                            //    Log.d(TAG, "Setting the friend as offline in database");
                            //    dbFriend.setStatus(Friend.FriendStatus.OFFLINE);
                            //    fd.update(dbFriend);
                            else if (dbFriend.isFavourite() == false){
                                //Otherwise, remove the friend from the db
                                Log.d(TAG, "Removing friend from DB");
                                fd.delete(dbFriend.getId());
                            } else {
                                dbFriend.setStatus(Friend.FriendStatus.OFFLINE);
                                fd.update(dbFriend);
                            }
                            friendOnlineHashMap.removeFriendbyMac(Utils.macAddressByteToHexString(friend.getMac()));

                        }
                    }
                }
            }
        }
    }

    private final String TAG = "StatusTrackingService";
    private Thread mThread;
    private final int delayInterval = 100;
    private final int onlineTimeoutPeriod = 500000;
    private FriendDao fd;
}
