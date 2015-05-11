package com.iotbyte.wifipidgin.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import com.iotbyte.wifipidgin.R;
import com.iotbyte.wifipidgin.friend.Friend;
import com.iotbyte.wifipidgin.user.User;
import com.iotbyte.wifipidgin.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hao on 4/9/15.
 */

public class DiscoverAdapter extends ArrayAdapter<Friend> {

    private static final String TAG = "SettingAdapter";

    public DiscoverAdapter(Context context, List<Friend> DicoverItems) {
        super(context, 0, DicoverItems);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final Friend friend = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_discover, parent, false);
        }
        ImageView settingImage = (ImageView) convertView.findViewById(R.id.discoverimageView);
        TextView settingTitle = (TextView) convertView.findViewById(R.id.discoverName);

        settingTitle.setText(friend.getName());

        File imgFile = new  File(friend.getImagePath());

        if(imgFile.exists()){

            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

            ImageView myImage = (ImageView) convertView.findViewById(R.id.discoverimageView);

            myImage.setImageBitmap(myBitmap);

        } else {
            settingImage.setImageResource(R.drawable.ic_launcher);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            public final static String EXTRA_MESSAGE = "com.iotbyte.wifipidgin.NAMECARD";
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Send friendName to DisplayNamecardActivity");
                sendUserID(v);
            }

            private void sendUserID(View convertView){

                Intent intent = new Intent(getContext(), DisplayNamecardActivity.class);
                intent.putExtra(EXTRA_MESSAGE, Utils.bytesToHex(friend.getMac()));
                getContext().startActivity(intent);
            }
        });

        // Return the completed view to render on screen
        return convertView;
    }

    /**
     * Switch the user's visibility
     * @param convertView the current view of the list
     * @return  void
     */

}