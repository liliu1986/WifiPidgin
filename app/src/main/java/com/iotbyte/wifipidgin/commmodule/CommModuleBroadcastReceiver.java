package com.iotbyte.wifipidgin.commmodule;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by fire on 04/05/15.
 */
public class CommModuleBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String datapassed = intent.getStringExtra("MSGREC");

        Toast.makeText(context,
                "Triggered by Service!\n"
                        + "Data passed: " + datapassed,
                Toast.LENGTH_LONG).show();
    }
}
