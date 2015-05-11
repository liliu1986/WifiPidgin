
package com.iotbyte.wifipidgin.ui;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;

import com.iotbyte.wifipidgin.dao.DaoFactory;
import com.iotbyte.wifipidgin.dao.FriendDao;
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


    List<Friend> tmpfriendlistItems = null;
    
    public FriendListManager(FragmentActivity inActivity , ListFragment inFragment) {
        mFragmentActivity = inActivity;
        mListFragment = inFragment;
    }

    public int InflateSettingView(){
        int err = 0;
        if(tmpfriendlistItems == null) {
            fd = DaoFactory.getInstance().getFriendDao(mFragmentActivity, DaoFactory.DaoType.SQLITE_DAO, null);
            tmpfriendlistItems = fd.findByIsFavourite(isFavourite);
        }
        FriendListAdapter adapter = new FriendListAdapter(mFragmentActivity, tmpfriendlistItems);
        mListFragment.setListAdapter(adapter);
        return err;
    }
}



