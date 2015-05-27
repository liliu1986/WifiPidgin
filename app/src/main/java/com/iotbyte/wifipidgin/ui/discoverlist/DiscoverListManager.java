package com.iotbyte.wifipidgin.ui.discoverlist;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.util.Log;

import com.iotbyte.wifipidgin.dao.DaoFactory;
import com.iotbyte.wifipidgin.dao.FriendDao;
import com.iotbyte.wifipidgin.dao.event.DaoEvent;
import com.iotbyte.wifipidgin.dao.event.DaoEventSubscriber;
import com.iotbyte.wifipidgin.friend.Friend;
import com.iotbyte.wifipidgin.ui.friendlist.FriendListAdapter;
import com.iotbyte.wifipidgin.user.User;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by hao on 4/9/15.
 */
public class DiscoverListManager {
    FragmentActivity mFragmentActivity;
    ListFragment mListFragment;
    Context mContext;

    FriendDao fd;
    private DiscoverListAdapter adapter = null;

    List<Friend> updatetmpfriendlistItems = null;
    List<Friend> tmpfriendlistItems = null;

    public DiscoverListManager(FragmentActivity inActivity , ListFragment inFragment) {
        mFragmentActivity = inActivity;
        mListFragment = inFragment;

        //
        fd = DaoFactory.getInstance().getFriendDao(mFragmentActivity, DaoFactory.DaoType.SQLITE_DAO, null);

        //register subscriber to data base event.
        fd.getDaoEventBoard().registerEventSubscriber(new DaoEventSubscriber() {
            @Override
            public void onEvent(DaoEvent event) {
                //filter event from data base by check event type FIEND_LIST_CHANGED.
                if (event == DaoEvent.FRIEND_LIST_CHANGED){
                    //check if adapter and adapter's list has been initialized.
                    if (adapter != null && tmpfriendlistItems != null){
                        //clear adapter's list.
                        adapter.clear();
                        //get updated friend list item from data base.
                        updatetmpfriendlistItems = fd.findAll();
                        //Log.d("TAG", "New friend list has % members"+tmpfriendlistItems.size());
                        //pass new friend list to adapter
                        adapter.addAll(updatetmpfriendlistItems);
                        //this will let adapter update UI.
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });

        //
    }


    public void InflateSettingView(){
        int err = 0;
        tmpfriendlistItems = fd.findAll();
        adapter = new DiscoverListAdapter(mFragmentActivity, tmpfriendlistItems);
        mListFragment.setListAdapter(adapter);
        //for each tmpfriendlistItems(friends in the data base), create DiscoverListAdapter for it.
        return;
    }
}




