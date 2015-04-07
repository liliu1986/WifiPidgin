package com.iotbyte.wifipidgin.ui;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.widget.ArrayAdapter;

import com.iotbyte.wifipidgin.R;

import java.util.ArrayList;

/**
 * Created by fire on 20/03/15.
 */
public class SettingsManager {

    FragmentActivity mFragmentActivity;
    ListFragment mListFragment;

    ArrayList<String> settingItems = new ArrayList<String>() {{
        add("My Visibility");
        add("Alias and Description");
        add("About");
    }};


    public SettingsManager(FragmentActivity inActivity , ListFragment inFragment) {
        mFragmentActivity = inActivity;
        mListFragment = inFragment;
    }

    public int InflateSettingView(){
        int err = 0;
        SettingAdapter adapter = new SettingAdapter(mFragmentActivity, settingItems);
        mListFragment.setListAdapter(adapter);
        return err;
    }
}
