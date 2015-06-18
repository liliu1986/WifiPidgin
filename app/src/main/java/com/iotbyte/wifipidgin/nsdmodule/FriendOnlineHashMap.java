package com.iotbyte.wifipidgin.nsdmodule;

import com.iotbyte.wifipidgin.friend.Friend;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by fire on 10/06/15.
 */
public class FriendOnlineHashMap {
    private static FriendOnlineHashMap instance = null;

    Map<String, Friend> friendOnlineMap ;
    private FriendOnlineHashMap(){
        friendOnlineMap = new ConcurrentHashMap<>();
    }

    public static FriendOnlineHashMap getInstance() {
        if (instance == null){
            synchronized (FriendOnlineHashMap.class) {
                if (instance == null) {
                    instance = new FriendOnlineHashMap();
                }
            }
        }
        return instance;
    }

    public void clearMap(){
        friendOnlineMap.clear();
    }
    public boolean containsMac (String inMac){
        return friendOnlineMap.containsKey(inMac);
    }
    public void put(String mac, Friend inFriend){
        friendOnlineMap.put(mac, inFriend);
    }
    public Friend get(String inMac){
        return friendOnlineMap.get(inMac);
    }

    public void removeFriendbyMac (String mac){
        friendOnlineMap.remove(mac);
    }

    public boolean isEmpty(){
        return friendOnlineMap.isEmpty();
    }
    public Collection<Friend> getValues(){
        return friendOnlineMap.values();
    }
}
