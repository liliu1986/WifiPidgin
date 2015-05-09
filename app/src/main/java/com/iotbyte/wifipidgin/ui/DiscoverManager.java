package com.iotbyte.wifipidgin.ui;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;

import com.iotbyte.wifipidgin.dao.DaoFactory;
import com.iotbyte.wifipidgin.dao.FriendDao;
import com.iotbyte.wifipidgin.friend.Friend;
import com.iotbyte.wifipidgin.user.User;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by hao on 4/9/15.
 */
public class DiscoverManager {
    FragmentActivity mFragmentActivity;
    ListFragment mListFragment;
    Context mContext;

    FriendDao fd;

    List<Friend> tmpfriendlistItems = null;

    public DiscoverManager(FragmentActivity inActivity , ListFragment inFragment) {
        mFragmentActivity = inActivity;
        mListFragment = inFragment;
    }

    public int InflateSettingView(){
        int err = 0;
        if(tmpfriendlistItems == null) {
            fd = DaoFactory.getInstance().getFriendDao(mFragmentActivity, DaoFactory.DaoType.SQLITE_DAO, null);
            tmpfriendlistItems = fd.findAll();
        }

        DiscoverAdapter adapter = new DiscoverAdapter(mFragmentActivity, tmpfriendlistItems);
        mListFragment.setListAdapter(adapter);
        return err;
    }
}




