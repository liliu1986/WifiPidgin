package com.iotbyte.wifipidgin.nsdmodule;

import android.util.Log;

import com.iotbyte.wifipidgin.friend.Friend;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by fire on 03/06/15.
 */
public class FriendOnlineList {
    private static FriendOnlineList instance = null;
    private Queue<Friend> friendCreationQueue;
    public static final String TAG = "FriendCreationQueue";

    private FriendOnlineList(){
        friendCreationQueue = new ConcurrentLinkedQueue<>();
    }
    public static FriendOnlineList getInstance() {
        if (instance == null){
            synchronized (FriendOnlineList.class) {
                if (instance == null) {
                    instance = new FriendOnlineList();
                }
            }
        }
        return instance;
    }

    //TODO -- change public to private after testing
    public boolean enqueueFriendCreationQueue(Friend inFriend) {
        Log.d(TAG, "Adding Friend " + inFriend.getIp().toString());

        return friendCreationQueue.offer(inFriend);
    }

    protected Friend dequeueFriendCreationQueue() {
        return friendCreationQueue.poll();
    }

    protected Friend topFriendCreationQueue() {
        return friendCreationQueue.peek();
    }
    /**
     *
     * @param inFriend
     * @return a boolean value that indicates if the friend is in
     * the Friend Creation Queue
     */
    protected boolean isInFriendCreationQueue(Friend inFriend){

        for (Friend friend : friendCreationQueue) {
            if (friend.getMac().equals(inFriend.getMac())){
                return true;
            }
        }
        return false;
    }

    protected boolean isEmptyFriendCreationQueue(){
        if (friendCreationQueue.size() == 0){
            return true;
        }
        return false;
    }

}
