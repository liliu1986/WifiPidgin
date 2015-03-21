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
    ArrayList<String> values3 = new ArrayList<String>() {{
        add("Sue");
        add("FiFi");
        add("DQ");
        add("Hao");
    }};

    public SettingsManager(FragmentActivity inActivity , ListFragment inFragment) {
        mFragmentActivity = inActivity;
        mListFragment = inFragment;
    }

    public int InflateSettingView(){
        int err = 0;
        mListFragment.setListAdapter(new ArrayAdapter<String>(mFragmentActivity, R.layout.list_settings, R.id.text1, values3));
        return err;
    }
}
