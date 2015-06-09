/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.iotbyte.wifipidgin.ui;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.iotbyte.wifipidgin.R;
import com.iotbyte.wifipidgin.channel.ChannelManager;
import com.iotbyte.wifipidgin.chat.IncomingMessageHandlingService;
import com.iotbyte.wifipidgin.chat.OutgoingMessageHandlingService;
import com.iotbyte.wifipidgin.commmodule.CommModuleBroadcastReceiver;
import com.iotbyte.wifipidgin.commmodule.MessageServerService;
import com.iotbyte.wifipidgin.nsdmodule.FriendCreationService;
import com.iotbyte.wifipidgin.nsdmodule.NsdClient;
import com.iotbyte.wifipidgin.nsdmodule.NsdWrapper;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

    FunctionSelectTabAdapter mFunctionSelectTabAdapter;

    ViewPager mViewPager;
    CommModuleBroadcastReceiver myReceiver;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Context context = getApplicationContext();

        // Create the adapter that will return a fragment for each of the three primary sections
        // of the app.
        mFunctionSelectTabAdapter = new FunctionSelectTabAdapter(getSupportFragmentManager());

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();

        // Specify that the Home/Up button should not be enabled, since there is no hierarchical
        // parent.
        actionBar.setHomeButtonEnabled(false);

        // Specify that we will be displaying tabs in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Set up the ViewPager, attaching the adapter and setting up a listener for when the
        // user swipes between sections.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mFunctionSelectTabAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When swiping between different app sections, select the corresponding tab.
                // We can also use ActionBar.Tab#select() to do this if we have a reference to the
                // Tab.
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mFunctionSelectTabAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by the adapter.
            // Also specify this Activity object, which implements the TabListener interface, as the
            // listener for when this tab is selected.
            actionBar.addTab(actionBar.newTab().setText(mFunctionSelectTabAdapter.getPageTitle(i))
                    .setTabListener(this));
        }

        myReceiver = new CommModuleBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MessageServerService.MY_ACTION);
        registerReceiver(myReceiver, intentFilter);

        //Start all the related services
        startServices();

        ChannelManager.getInstance(getApplicationContext());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_about, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MessageServerService.MY_ACTION);
        registerReceiver(myReceiver, intentFilter);
        super.onResume();
    }

    @Override
    public void onPause(){
        unregisterReceiver(myReceiver);
        super.onPause();
    }


    //TODO need to destroy the NSD
    @Override
    protected void onDestroy() {
        Context context = getApplicationContext();
        context.stopService(messageServerServiceIntent);
        mNsdClient.stopDiscovery();
        context.stopService(friendCreationServiceIntent);
        context.stopService(incomingMessageHandlingServicesIntent);
        context.stopService(outGoingMessageHandlingServicesIntent);

        super.onDestroy();
    }


    private void startServices(){
        Context context = getApplicationContext();
        //Start message Server service and NSD Service
        messageServerServiceIntent= new Intent(context, MessageServerService.class);
        context.startService(messageServerServiceIntent);

        //Start NSD Client here
        //Start the service discovery
        mNsdClient = new NsdClient(this);
        mNsdClient.initializeNsdClient();
        mNsdClient.discoverServices();

        //Start FriendCreationService
        friendCreationServiceIntent = new Intent(context, FriendCreationService.class);
        context.startService(friendCreationServiceIntent);

        //Start IncomingMessageHandlingService
        incomingMessageHandlingServicesIntent = new Intent(context, IncomingMessageHandlingService.class);
        context.startService(incomingMessageHandlingServicesIntent);

        //Start OutGoingMessageHandlingService
        outGoingMessageHandlingServicesIntent = new Intent(context, OutgoingMessageHandlingService.class);
        context.startService(outGoingMessageHandlingServicesIntent);
    }
    NsdClient mNsdClient;
    private Intent messageServerServiceIntent;
    private Intent friendCreationServiceIntent;
    private Intent incomingMessageHandlingServicesIntent;
    private Intent outGoingMessageHandlingServicesIntent;
}
