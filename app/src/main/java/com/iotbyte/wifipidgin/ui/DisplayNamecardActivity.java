package com.iotbyte.wifipidgin.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iotbyte.wifipidgin.R;
import com.iotbyte.wifipidgin.dao.DaoFactory;
import com.iotbyte.wifipidgin.dao.FriendDao;
import com.iotbyte.wifipidgin.friend.Friend;
import com.iotbyte.wifipidgin.utils.Utils;

import java.io.File;
import java.net.URISyntaxException;

public class DisplayNamecardActivity extends ActionBarActivity implements View.OnClickListener {

    public final static String EXTRA_MESSAGE = "com.iotbyte.wifipidgin.NAMECARD";
    public final static String EXTRA_MESSAGE_USERMAC = "com.iotbyte.wifipidgin.NAMECARD.USERMAC";

    FriendDao fd;
    String userName = "Default User Name";
    String userDescription = "Default User Description";
    boolean userSaved = false;

    boolean userOnline = true;

    private static final String TAG = "DisplayNameCard";

    private String userMac = null;
    private Friend inFriend = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_display_namecard);

        Intent intent = getIntent();
        //Display the back button.
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Get the user name from the intent
        userMac = intent.getStringExtra(EXTRA_MESSAGE);
        fd = DaoFactory.getInstance().getFriendDao(this.getApplicationContext(), DaoFactory.DaoType.SQLITE_DAO, null);
        Log.d(TAG, "Use MAC: "+ userMac);
        inFriend = fd.findByMacAddress(Utils.hexStringToByteArray(userMac));

        if (inFriend != null) {
            userName = inFriend.getName();
            //Display the user name card title
            actionBar.setTitle("Name Card: [" + userName +"]");

            userDescription = inFriend.getDescription();
            //Set the user image
            File imgFile = new  File(inFriend.getImagePath());
            ImageView myImage = (ImageView) findViewById(R.id.user_image);
            myImage.setOnClickListener(this);


            if(imgFile.exists()){
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                myImage.setImageBitmap(myBitmap);
            }else{
                myImage.setImageResource(R.drawable.default_user_image);
            }

            ImageButton savedUserImageView = (ImageButton) findViewById(R.id.user_saved);
            savedUserImageView.setOnClickListener(this);

            //Indicator for if use is saved as fav already
            if(inFriend.isFavourite()){
                savedUserImageView.setImageResource(R.drawable.saved_user);
            }else{
                savedUserImageView.setImageResource(R.drawable.unsaved_user);
            }
            Button startChatButton = (Button) findViewById(R.id.startChatButton);
            startChatButton.setText("Start Chat with "+ userName);
            startChatButton.setOnClickListener(this);

            //Indicator for if use is online
            ImageView onlineUserImageView = (ImageView) findViewById(R.id.user_online);
            if(userOnline){
                onlineUserImageView.setImageResource(R.drawable.user_visible);
            }else{
                onlineUserImageView.setImageResource(R.drawable.user_invisible);
            }

            //Set the description in textView
            TextView userDescriptionTextView = (TextView) findViewById(R.id.user_description);
            userDescriptionTextView.setText(userDescription);

        }else{
            Log.e(TAG, "Couldn't find the user");
        }





    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_namecard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        boolean userSaved = false;
        switch(id){
            case android.R.id.home:{
                super.onBackPressed();
                return true;
            }
            case R.id.action_settings:{
                return true;
            }
        }


        return super.onOptionsItemSelected(item);
    }
    // Implement the OnClickListener callback
    @Override
    public void onClick(View v) {

        Log.d(TAG, "Clicked User_saved Button");
        Context context = getApplicationContext();

        switch(v.getId())
        {
            //if the user_saved button is clicked
            case R.id.user_saved :
                //@TODO should have a confirmation window to user
                ImageButton savedUserImageView = (ImageButton) findViewById(R.id.user_saved);
                if(userSaved){
                    userSaved = false;
                    savedUserImageView.setImageResource(R.drawable.unsaved_user);
                    CharSequence text = "Removed this friend from contact list";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();

                }else{
                    userSaved = true;
                    savedUserImageView.setImageResource(R.drawable.saved_user);
                    CharSequence text = "Saved this friend to contact list";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
                break;

            case R.id.startChatButton :

                //@TODO Start activity to chat

            case R.id.user_image :
                Log.d(TAG, "The path is " + getExternalFilesDir(null).toString());
                showFileChooser();

        }
    }

    private static final int FILE_SELECT_CODE = 0;

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a image to represent yourself"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    Log.d(TAG, "File Uri: " + uri.toString());
                    // Get the path
                    String path = null;

                    path = Utils.getRealPathFromURI(this, uri);
                    Log.d(TAG, "File Path: " + path);
                    //
                    File imgFile = new File(path);
                    if (imgFile.exists()) {
                        //Save the path for the current user
                        inFriend.setImagePath(path);
                        fd.update(inFriend);

                        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        ImageView myImage = (ImageView) findViewById(R.id.user_image);
                        myImage.setImageBitmap(myBitmap);
                    } else {
                        Log.d(TAG, "File Not Found!! ");
                    }


                    // Get the file instance
                    // File file = new File(path);
                    // Initiate the upload
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
