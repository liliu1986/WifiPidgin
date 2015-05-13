package com.iotbyte.wifipidgin.ui;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.util.Log;

import com.iotbyte.wifipidgin.dao.DaoFactory;
import com.iotbyte.wifipidgin.dao.FriendDao;
import com.iotbyte.wifipidgin.dao.DiscoverListChangedListener;
import com.iotbyte.wifipidgin.friend.Friend;

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

    private static final String TAG = "DiscoverManager";

    public DiscoverManager(FragmentActivity inActivity , ListFragment inFragment) {
        mFragmentActivity = inActivity;
        mListFragment = inFragment;
        DaoFactory.setDiscoverListChangedListener(new DiscoverListChangedListener() {
            @Override
            public void onDiscoverListChanged() {
                Log.d(TAG, "The discover list has been changed.");
            }
        });
        fd = DaoFactory.getInstance().getFriendDao(mFragmentActivity, DaoFactory.DaoType.SQLITE_DAO, null);

    }

    public int InflateSettingView(){
        int err = 0;
        if(tmpfriendlistItems == null) {
            //fd = DaoFactory.getInstance().getFriendDao(mFragmentActivity, DaoFactory.DaoType.SQLITE_DAO, null);
            tmpfriendlistItems = fd.findAll();
        }

        DiscoverAdapter adapter = new DiscoverAdapter(mFragmentActivity, tmpfriendlistItems);
        mListFragment.setListAdapter(adapter);
        return err;
    }
}




