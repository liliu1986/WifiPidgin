package com.iotbyte.wifipidgin.ui;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iotbyte.wifipidgin.R;

import java.util.ArrayList;

/**
 * Created by fire on 04/04/15.
 */

public class SettingAdapter extends ArrayAdapter<String> {

    private static final String TAG = "SettingAdapter";
    private static int userVisibility = 1;
    public SettingAdapter(Context context, ArrayList<String> SettingItems) {
        super(context, 0, SettingItems);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        String user = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_settings, parent, false);
        }
        ImageView settingImage = (ImageView) convertView.findViewById(R.id.imageView1);
        TextView settingTitle = (TextView) convertView.findViewById(R.id.settingName);
        settingImage.setImageResource(R.drawable.user_visible);
        switch (position) {
            //Visibility Setting Tab
            case 0:
                settingTitle.setText("Everyone around can see you now");
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "Changing User Visibility");
                        changeUserVisibility(v);
                    }
                });
                break;
            //Start the alias editing activity.
            case 1:
                settingTitle.setText(user);
                break;
            //About Setting Tab
            case 2:
                settingTitle.setText(user);
                break;
            default:
                //settingTitle.setText(user);

        }
        // Return the completed view to render on screen
        return convertView;
    }

    /**
     * Switch the user's visibility
     * @param convertView the current view of the list
     * @return  void
     */
    private void changeUserVisibility(View convertView){

        ImageView settingImage = (ImageView) convertView.findViewById(R.id.imageView1);
        TextView settingTitle = (TextView) convertView.findViewById(R.id.settingName);

        if(userVisibility == 1){
            userVisibility = 0;
            settingTitle.setText("You are now invisible");
            settingImage.setImageResource(R.drawable.user_invisible);
        }else{
            userVisibility = 1;
            settingTitle.setText("Everyone around can see you now");
            settingImage.setImageResource(R.drawable.user_visible);
        }
    }
}