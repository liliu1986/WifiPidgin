package com.iotbyte.wifipidgin.ui;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;

import com.iotbyte.wifipidgin.user.User;

import java.util.ArrayList;


/**
 * Created by hao on 4/9/15.
 */
public class DiscoverManager {
    FragmentActivity mFragmentActivity;
    ListFragment mListFragment;

    ArrayList<User> tmpfriendlistItems = new ArrayList<User>(){{
        add(new User("Sue"));
        add(new User("Fifi"));
        add(new User("QD"));
        add(new User("Liang"));
    }};

    public DiscoverManager(FragmentActivity inActivity , ListFragment inFragment) {
        mFragmentActivity = inActivity;
        mListFragment = inFragment;
    }

    public int InflateSettingView(){
        int err = 0;
        DiscoverAdapter adapter = new DiscoverAdapter(mFragmentActivity, tmpfriendlistItems);
        mListFragment.setListAdapter(adapter);
        return err;
    }
}




