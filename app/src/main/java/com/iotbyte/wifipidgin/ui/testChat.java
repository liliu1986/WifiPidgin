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
import com.iotbyte.wifipidgin.commmodule.CommModuleBroadcastReceiver;
import com.iotbyte.wifipidgin.commmodule.MessageClient;
import com.iotbyte.wifipidgin.commmodule.MessageServer;
import com.iotbyte.wifipidgin.commmodule.MessageServerService;
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

        //Start message Server service and NSD Service
        Intent i= new Intent(getApplicationContext(), MessageServerService.class);
        i.putExtra("KEY1", "Value to be used by the service");
        context.startService(i);

        //Start the service discovery
        NsdClient mNsdClient = new NsdClient(this);
        mNsdClient.initializeNsdClient();
        mNsdClient.discoverServices();

        /*
        mUpdateHandler = new Handler() {
            @Override
            public void handleMessage(ChatMessage msg) {
                String chatLine = msg.getData().getString("msg");
                Log.d(TAG, "Got message !!! : " + chatLine);
            }
        };

        mMessageServer = new MessageServer(mUpdateHandler);
        */
        /*
        mMessageServer.setMessageReceivingListener(new MessageReceivingListener(){
            @Override
            public void onMessageReceived(String msg){
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(getBaseContext(), msg, duration);
                toast.show();
            }
        });
        */
        //mMessageServer.startServer();


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
                msg = msgTextView.getText().toString();
                try {
                    destIP = InetAddress.getByName(ipString);
                    Log.d(TAG, "Trying to connect to server at : " + destIP.toString() + ": " + port);

                    mMessageClient = new MessageClient();


                } catch (UnknownHostException e) {
                    e.printStackTrace();
                };

                if (mMessageClient != null){
                    Log.d(TAG, "Sending the msg Now!!!");
                    mMessageClient.sendMsg(destIP, port, msg);
                } else {
                    Log.e(TAG, "The message client has not been initialized yet!!!");
                }
        }
    }
    @Override
    protected void onStop()
    {
        unregisterReceiver(myReceiver);
        super.onStop();
    }
}
