
package com.iotbyte.wifipidgin.ui.friendlist;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.util.Log;

import com.iotbyte.wifipidgin.dao.DaoFactory;
import com.iotbyte.wifipidgin.dao.FriendDao;
import com.iotbyte.wifipidgin.dao.event.DaoEvent;
import com.iotbyte.wifipidgin.dao.event.DaoEventSubscriber;
import com.iotbyte.wifipidgin.friend.Friend;
import java.util.List;


/**
 * Created by hao on 4/9/15.
 */
public class FriendListManager {
    FragmentActivity mFragmentActivity;
    ListFragment mListFragment;
    Context mContext;
    boolean isFavourite = true;

    FriendDao fd;
    private FriendListAdapter adapter = null;

    List<Friend> updatetmpfriendlistItems = null;
    List<Friend> tmpfriendlistItems = null;

    public FriendListManager(FragmentActivity inActivity , ListFragment inFragment) {
        mFragmentActivity = inActivity;
        mListFragment = inFragment;
        //
        fd = DaoFactory.getInstance().getFriendDao(mFragmentActivity, DaoFactory.DaoType.SQLITE_DAO, null);

        //updatetmpfriendlistItems = fd.findByIsFavourite(isFavourite);

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
                        updatetmpfriendlistItems = fd.findByIsFavourite(isFavourite);
                        //Log.d("TAG", "New friend list has % members"+tmpfriendlistItems.size());
                        //pass new friend list to adapter
                        adapter.addAll(updatetmpfriendlistItems);
                        //this will let adapter update UI.
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });


    }

    public void InflateSettingView(){
        int err = 0;
        tmpfriendlistItems = fd.findByIsFavourite(isFavourite);
        adapter = new FriendListAdapter(mFragmentActivity, tmpfriendlistItems);
        mListFragment.setListAdapter(adapter);
        return;
    }
}



