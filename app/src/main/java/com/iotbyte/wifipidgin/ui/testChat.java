package com.iotbyte.wifipidgin.ui;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.iotbyte.wifipidgin.R;
import com.iotbyte.wifipidgin.chat.ChatManager;
import com.iotbyte.wifipidgin.chat.IncomingMessageHandlingService;
import com.iotbyte.wifipidgin.chat.OutgoingMessageHandlingService;
import com.iotbyte.wifipidgin.commmodule.CommModuleBroadcastReceiver;
import com.iotbyte.wifipidgin.commmodule.MessageClient;
import com.iotbyte.wifipidgin.commmodule.MessageServer;
import com.iotbyte.wifipidgin.commmodule.MessageServerService;
import com.iotbyte.wifipidgin.friend.Friend;
import com.iotbyte.wifipidgin.message.ChatMessage;
import com.iotbyte.wifipidgin.nsdmodule.FriendStatusTrackingService;
import com.iotbyte.wifipidgin.nsdmodule.NsdClient;
import com.iotbyte.wifipidgin.nsdmodule.NsdWrapper;

import java.net.InetAddress;
import java.net.UnknownHostException;


/**
 ** This is mocked activity for testing
 **/
public class testChat extends ActionBarActivity implements View.OnClickListener{

    private static final String TAG = "testChat";
    private MessageServer mMessageServer;
    private MessageClient mMessageClient = null;
    NsdWrapper mNsdWrapper;
    private Handler mUpdateHandler;
    CommModuleBroadcastReceiver myReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_chat);
        Context context = getApplicationContext();


        Button ConnectToServerButton = (Button) findViewById(R.id.ConnectToServer);
        ConnectToServerButton.setOnClickListener(this);

        Button StartServerButton = (Button) findViewById(R.id.sendMsgButton);
        StartServerButton.setOnClickListener(this);

        myReceiver = new CommModuleBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MessageServerService.MY_ACTION);
        registerReceiver(myReceiver, intentFilter);

        startServices();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_test_chat, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        Context context = getApplicationContext();
        InetAddress destIP = null;
        int port = 0;
        String msg = "";

        switch(v.getId())
        {
            //if the user_saved button is clicked
            case R.id.ConnectToServer :
                //mMessageClient = new MessageClient();

                break;
            case R.id.sendMsgButton :
                TextView userNameTestView = (TextView) findViewById(R.id.ipField);
                String ipString = userNameTestView.getText().toString();

                TextView portTextView = (TextView) findViewById(R.id.portField);
                port = Integer.parseInt(portTextView.getText().toString());

                TextView msgTextView = (TextView) findViewById(R.id.msgField);


                // msg = msgTextView.getText().toString();
                try {
                    destIP = InetAddress.getByName(ipString);
                    byte[] aMac = {0xf,0xc,0xa,0xa,0x1,0x4,0x7,0x9,0xa,0xe,0xb,0xd}; //fc:aa:14:79:ae:bd
                    //byte[] aMac = {0xf,0x8,0xa,0x9,0xd,0x0,0x6,0x7,0xe,0xd,0xc,0x5}; //fc:aa:14:79:ae:bd
                    Friend aFriend = new Friend(aMac,destIP,port);
                    ChatMessage chatmessage = new ChatMessage(aFriend,"e2qjseahfwo3i",msgTextView.getText().toString(),getApplicationContext());

                    ChatManager chatManager = ChatManager.getInstance(getApplicationContext());
                    chatManager.enqueueOutGoingMessageQueue(chatmessage);



                    Log.d(TAG, "Trying to connect to server at : " + destIP.toString() + ": " + port);

                    mMessageClient = new MessageClient(getApplicationContext());

                    if (mMessageClient != null){
                        Log.d(TAG, "Sending the msg Now!!!");
                        // mMessageClient.sendMsg(destIP, port, chatManager.dequeueOutGoingMessageQueue());
                    } else {
                        Log.e(TAG, "The message client has not been initialized yet!!!");
                    }

                } catch (UnknownHostException e) {
                    e.printStackTrace();
                };


        }
    }
    //TODO need to destroy the NSD
    @Override
    protected void onDestroy() {
        Context context = getApplicationContext();
        context.stopService(messageServerServiceIntent);
        //mNsdClient.stopDiscovery();
        context.stopService(friendStatusTrackingServiceIntent);
        context.stopService(incomingMessageHandlingServicesIntent);
        context.stopService(outGoingMessageHandlingServicesIntent);
        unregisterReceiver(myReceiver);
        super.onDestroy();
    }


    private void startServices(){
        Context context = getApplicationContext();
        //Start message Server service and NSD Service
        messageServerServiceIntent= new Intent(context, MessageServerService.class);
        context.startService(messageServerServiceIntent);

        //Start NSD Client here
        //Start the service discovery
        //mNsdClient = new NsdClient(this);
        //mNsdClient.initializeNsdClient();
        //mNsdClient.discoverServices();

        //Start FriendCreationService
        friendStatusTrackingServiceIntent = new Intent(context, FriendStatusTrackingService.class);
        context.startService(friendStatusTrackingServiceIntent);

        //Start IncomingMessageHandlingService
        incomingMessageHandlingServicesIntent = new Intent(context, IncomingMessageHandlingService.class);
        context.startService(incomingMessageHandlingServicesIntent);

        //Start OutGoingMessageHandlingService
        outGoingMessageHandlingServicesIntent = new Intent(context, OutgoingMessageHandlingService.class);
        context.startService(outGoingMessageHandlingServicesIntent);
    }
    NsdClient mNsdClient;
    private Intent messageServerServiceIntent;
    private Intent friendStatusTrackingServiceIntent;
    private Intent incomingMessageHandlingServicesIntent;
    private Intent outGoingMessageHandlingServicesIntent;
    @Override
    protected void onStop()
    {

        super.onStop();
    }
}
