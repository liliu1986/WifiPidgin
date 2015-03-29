package com.iotbyte.wifipidgin.ui;

import android.util.Log;

import com.iotbyte.wifipidgin.friend.Friend;

import java.util.ArrayList;

/**
 * Created by fire on 22/03/15.
 */

public class tempDb {
    private ArrayList<Friend> nearbyFriendList;

    private static final String TAG = "tempDb";

    public tempDb(){
        nearbyFriendList = new ArrayList<Friend>();
    }

    public void addFriendToList(Friend inFriend){
        int i;
        int isIn = 0;

        //Searching through the list to see if the friend is already in the online list.
        for(i = 0; i < nearbyFriendList.size(); i++){
            if(nearbyFriendList.get(i).getUserIP().equals(inFriend.getUserIP())){
                isIn = 1;
            }
        }
        //If not, add him/her to the list.
        if (isIn == 0){
            Log.d(TAG, "Adding friend to the list " + inFriend.getUserIP());
            nearbyFriendList.add(inFriend);
        }else{
            Log.d(TAG, "The friend is already in the list " + inFriend.getUserIP());
        }
    }
    public void removeFriendFromList(Friend inFriend){
        int i;
        //iterate through the list to find the existing friend;
        for(i = 0; i < nearbyFriendList.size(); i++){
            if(nearbyFriendList.get(i).getUserIP().equals(inFriend.getUserIP())){
                //Found the user, remove him/her from the list since offline.
                nearbyFriendList.remove(i);
            }
        }

        if(i == nearbyFriendList.size()){
            Log.w(TAG, "The friend %s is not in the online list: "+ inFriend.getUserIP());
        }

    }
    public ArrayList<Friend> getFriendList(){
        return nearbyFriendList;
    }
}
