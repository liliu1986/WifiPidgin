package com.iotbyte.wifipidgin.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.viewpagerindicator.IconPagerAdapter;
import com.iotbyte.wifipidgin.R;

/**
 * Created by fire on 2/2/15.
 */

class FunctionSelectTabAdapter extends FragmentPagerAdapter {
    public FunctionSelectTabAdapter(FragmentManager fm) {
        super(fm);
    }

    private static final String TAG = "FunctionSelectTab";

    private static final String[] CONTENT = new String[]{"Chats", "Contacts", "Discover"};

    @Override
    public Fragment getItem(int position) {

        Log.d(TAG, "position: " + position);

        switch (position) {
            case 0:
                return ChannelTabFragment.newInstance();
            default:
                return TabIconFragment.newInstance(CONTENT[position % CONTENT.length],
                        (position % CONTENT.length),
                        FunctionSelectResources.getOnClickTab(position % CONTENT.length));
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return CONTENT[position % CONTENT.length].toUpperCase();
    }

    @Override
    public int getCount() {
        return CONTENT.length;
    }

}