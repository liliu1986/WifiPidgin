package com.iotbyte.wifipidgin.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iotbyte.wifipidgin.R;
import com.iotbyte.wifipidgin.dao.DaoFactory;
import com.iotbyte.wifipidgin.dao.FriendDao;
import com.iotbyte.wifipidgin.friend.Friend;
import com.iotbyte.wifipidgin.friend.Myself;
import com.iotbyte.wifipidgin.utils.Utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class EditUserProfileActivity extends ActionBarActivity implements View.OnClickListener {

    public final static String EXTRA_MESSAGE = "com.iotbyte.wifipidgin.NAMECARD";
    public final static String EXTRA_MESSAGE_USERMAC = "com.iotbyte.wifipidgin.NAMECARD.USERMAC";

    FriendDao fd;
    String userName = "Default User Name";
    String userDescription = "Default User Description";
    boolean userSaved = false;
    boolean userOnline = true;

    private static final String TAG = "EditUserProfile";

    private String userMac = null;
    private Friend inFriend = null;
    private Myself self = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_user_profile);

        //Display the back button.
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        fd = DaoFactory.getInstance().getFriendDao(this.getApplicationContext(), DaoFactory.DaoType.SQLITE_DAO, null);

        //Get the user Self from DB
        inFriend = fd.findById(0);


        if (inFriend != null) {
            self = new Myself(inFriend);
            if (self != null){
                userName = self.getName();
                //Display the user name card title
                actionBar.setTitle("Name Card: [" + userName +"]");

                userDescription = self.getDescription();
                //Set the user image
                File imgFile = new  File(self.getImagePath());
                ImageView myImage = (ImageView) findViewById(R.id.user_image);
                myImage.setOnClickListener(this);

                //if image file exist, present it in the convertView.
                if(imgFile.exists()){
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    myImage = (ImageView) findViewById(R.id.user_image);
                    myImage.setImageBitmap(myBitmap);

                } else {
                    //if image file doesn't exist, present the default image.
                    myImage.setImageResource(R.drawable.default_user_image);
                }


                //Set the description in textView
                TextView userDescriptionTextView = (TextView) findViewById(R.id.user_description);
                if (null == userDescription){
                    userDescriptionTextView.setText("This is my description");
                }else{
                    userDescriptionTextView.setText(userDescription);
                }



                TextView userNameTextView = (TextView) findViewById(R.id.user_name);
                if (null == userName){
                    userNameTextView.setText("User Name");
                }else{
                    userNameTextView.setText(userName);
                }


                Button saveProfileButton = (Button) findViewById(R.id.save_user_profile);
                saveProfileButton.setOnClickListener(this);

                Button cancelProfileButton = (Button) findViewById(R.id.cancel_edit);
                cancelProfileButton.setOnClickListener(this);

            }

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
            case R.id.save_user_profile :
                //Grab the content in text fields and save them to user.
                TextView userNameTextView = (TextView) findViewById(R.id.user_name);
                String newUserName = userNameTextView.getText().toString();
                self.setName(newUserName);

                TextView userDescTextView = (TextView) findViewById(R.id.user_description);
                String newUserDesc = userDescTextView.getText().toString();
                self.setDescription(newUserDesc);
                Log.d(TAG, "Mac: " + Utils.macAddressByteToHexString(self.getMac()));
                Log.d(TAG, "Mac: " + Utils.macAddressByteToHexString(inFriend.getMac()));
                fd.update(self);
                super.onBackPressed();
                break;
            case R.id.cancel_edit :
                super.onBackPressed();
                break;
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

                    String path = getExternalFilesDir(null).toString()+ File.separator+"myImage";
                    File destFile = new File(path);
                    saveFile(uri, destFile);

                    File destFile2 = new File(path);

                    if (destFile2.exists()){
                        Log.d(TAG, "GOOD");
                    } else {
                        Log.d(TAG, "BAD");
                    }

                    //path = Utils.getRealPathFromURI(this, uri);
                    Log.d(TAG, "File Path: " + uri.toString());

                    //find friend's image by the ImagePath from DB.
                    File imgFile = new  File(path);
                    //if image file exist, present it in the convertView.
                    if(imgFile.exists()){
                        self.setImagePath(path);
                        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        ImageView myImage = (ImageView) findViewById(R.id.user_image);
                        myImage.setImageBitmap(myBitmap);
                    }

                    // Get the file instance
                    // File file = new File(path);
                    // Initiate the upload
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void saveFile(Uri sourceUri, File destination){

        BufferedOutputStream bos = null;
        InputStream imgFile = null;
        try {
            imgFile = getContentResolver().openInputStream(sourceUri);
            bos = new BufferedOutputStream(new FileOutputStream(destination));

            byte[] buf = new byte[1024];
            imgFile.read(buf);
            do {
                bos.write(buf);
            } while(imgFile.read(buf) != -1);

        } catch (IOException ex) {
            ex.printStackTrace();
        }finally {
            try {
                if (imgFile != null) imgFile.close();
                if (bos != null) bos.close();
            } catch (IOException e) {

            }
        }
    }
}
