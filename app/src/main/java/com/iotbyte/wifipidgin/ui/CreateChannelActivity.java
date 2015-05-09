package com.iotbyte.wifipidgin.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.view.View.OnClickListener;

import com.iotbyte.wifipidgin.R;

import java.util.ArrayList;


public class CreateChannelActivity extends Activity {

    ArrayList<String> values = new ArrayList<String>() {{
        add("Sue");
        add("FiFi");
        add("DQ");
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_channel);

        Button buttonCancel = (Button) findViewById(R.id.buttonCancel);

        // populate list view
        ArrayAdapter<String> aa = (new ArrayAdapter<String> (this,android.R.layout.simple_list_item_multiple_choice,values));
        ListView lv = (ListView) findViewById(android.R.id.list);
        lv.setAdapter(aa);

        // listen to button click to create channel
        Button buttonCreateChannel = (Button) findViewById(R.id.buttonCreateChannel);
        buttonCreateChannel.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Log.i("CreateChannelActivity", "create channel confirm button clicked");

                // To Do: Add code to create channel

                finish();
            }
        });

        // listen to button click to cancel and go back to the main page
        buttonCancel.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Log.i("CreateChannelActivity", "cancel button clicked");
                finish();
            }
        });
    }
}