package com.iotbyte.wifipidgin.ui.discoverlist;

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
import com.iotbyte.wifipidgin.dao.DaoFactory;
import com.iotbyte.wifipidgin.dao.FriendDao;
import com.iotbyte.wifipidgin.friend.Friend;
import com.iotbyte.wifipidgin.user.User;
import com.iotbyte.wifipidgin.utils.Utils;
import com.iotbyte.wifipidgin.ui.DisplayNamecardActivity;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hao on 4/9/15.
 */

public class DiscoverListAdapter extends ArrayAdapter<Friend> {

    private static final String TAG = "SettingAdapter";


    public DiscoverListAdapter(Context context, List<Friend> DicoverItems) {
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
        //
        ImageView settingImage = (ImageView) convertView.findViewById(R.id.discoverimageView);
        TextView settingTitle = (TextView) convertView.findViewById(R.id.discoverName);

        //set view's titile by friend's Name from DB.
        settingTitle.setText(friend.getName());

        //find friend's image by the ImagePath from DB.
        File imgFile = new  File(friend.getImagePath());

        //if image file exist, present it in the convertView.
        if(imgFile.exists()){

            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

            ImageView myImage = (ImageView) convertView.findViewById(R.id.discoverimageView);

            myImage.setImageBitmap(myBitmap);

        } else {
            //if image file doesn't exist, present the default image.
            settingImage.setImageResource(R.drawable.ic_launcher);
        }

        //set onclicklistener to DisplayNamecardActivity, once click on the name/image, open the correspond NameCard.
        convertView.setOnClickListener(new View.OnClickListener() {
            public final static String EXTRA_MESSAGE = "com.iotbyte.wifipidgin.NAMECARD";
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Send friendName to DisplayNamecardActivity");
                sendUserID(v);
                //for test only,
                //remove when test pass
                /*
                InetAddress xiaoMingIP = null;
                try {
                    xiaoMingIP = InetAddress.getByName("192.168.1.7");
                    byte[] xiaoMingMac = {0x5,0xa,0x0,0xa,0x5,0xb,0x5,0x8,0x3,0x5,0x4,0x4};
                    int xiaoMingPort = 51;
                    Friend testT = new Friend(xiaoMingMac,xiaoMingIP,xiaoMingPort);
                    testT.setDescription("wo shi test");
                    testT.setName("test");
                    FriendDao fd = DaoFactory.getInstance().getFriendDao(getContext(), DaoFactory.DaoType.SQLITE_DAO,null);
                    fd.add(testT);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                */
            }


            private void sendUserID(View convertView){
                //getContext() return this.converView.
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