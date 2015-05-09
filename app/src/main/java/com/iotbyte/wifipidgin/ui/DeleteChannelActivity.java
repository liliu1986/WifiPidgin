package com.iotbyte.wifipidgin.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.iotbyte.wifipidgin.R;

import java.util.ArrayList;


public class DeleteChannelActivity extends Activity {

    final Context context = this;

    ArrayList<String> values = new ArrayList<String>() {{
        add("Sue Channel");
        add("FiFi Channel");
        add("DQ Channel");
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_channel);

        Button buttonDeleteChannel = (Button) findViewById(R.id.buttonDeleteChannel);
        Button buttonCancel = (Button) findViewById(R.id.buttonCancel);

        // populate list view
        ArrayAdapter<String> aa = (new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, values));
        ListView lv = (ListView) findViewById(android.R.id.list);
        lv.setAdapter(aa);

        // listen to button click to delete channel
        buttonDeleteChannel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i("DeleteChannelActivity", "delete channel confirm button clicked");

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are You Sure?");
                // Add the buttons
                builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        // To Do: Add code to delete the selected channels

                        finish();
                    }
                });
                builder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                // Create the AlertDialog
                AlertDialog dialog = builder.create();
                // show it
                dialog.show();
            }
        });

        // listen to button click to cancel and go back to main page
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i("DeleteChannelActivity", "cancel button clicked");
                finish();
            }
        });
    }
}
