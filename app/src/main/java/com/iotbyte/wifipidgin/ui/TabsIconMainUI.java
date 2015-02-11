package com.iotbyte.wifipidgin.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.viewpagerindicator.TabPageIndicator;
import com.iotbyte.wifipidgin.nsdmodule.NsdWrapper;
import com.iotbyte.wifipidgin.R;

public class TabsIconMainUI extends FragmentActivity {
    private static final String TAG = "TabsIconMainUI";
    NsdWrapper mNsdWrapper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Start NSD here
        mNsdWrapper = new NsdWrapper(this);
        //Start DSN broadcasting
        mNsdWrapper.Broadcast();
        //Start DSN discovery
        mNsdWrapper.discover();

        setContentView(R.layout.simple_tabs);

        FragmentPagerAdapter adapter = new FunctionSelectTabAdapter(getSupportFragmentManager());

        ViewPager pager = (ViewPager)findViewById(R.id.pager);
        pager.setAdapter(adapter);

        TabPageIndicator indicator = (TabPageIndicator)findViewById(R.id.indicator);
        indicator.setViewPager(pager);
    }

    @Override
    protected void onDestroy() {
        if(mNsdWrapper != null)
            mNsdWrapper.tearDown();
        super.onDestroy();
    }



}
