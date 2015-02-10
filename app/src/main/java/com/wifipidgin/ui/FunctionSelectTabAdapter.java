package com.wifipidgin.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.viewpagerindicator.IconPagerAdapter;
import com.wifipidgin.R;

/**
 * Created by fire on 2/2/15.
 */

class FunctionSelectTabAdapter extends FragmentPagerAdapter implements IconPagerAdapter {
    public FunctionSelectTabAdapter(FragmentManager fm) {
        super(fm);
    }

    private static final String TAG = "FunctionSelectTabAdapter";

    private static final String[] CONTENT = new String[] { "Chats", "Contacts", "Discover"};

    private static final int[] ICONS = new int[] {
            R.drawable.perm_group_calendar,
            R.drawable.perm_group_camera,
            R.drawable.perm_group_device_alarms,
    };

    @Override
    public Fragment getItem(int position) {

        Log.d(TAG, "position: " + position);

        return TabIconFragment.newInstance(CONTENT[position % CONTENT.length],
                (position % CONTENT.length),
                FunctionSelectResources.getOnClickTab(position % CONTENT.length));
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return CONTENT[position % CONTENT.length].toUpperCase();
    }

    @Override public int getIconResId(int index) {
        return ICONS[index];
    }

    @Override
    public int getCount() {
        return CONTENT.length;
    }

}