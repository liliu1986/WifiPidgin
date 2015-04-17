package com.iotbyte.wifipidgin.ui;

import android.content.Context;
import android.content.Intent;
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

public class DisplayNamecardActivity extends ActionBarActivity implements View.OnClickListener {

    public final static String EXTRA_MESSAGE = "com.iotbyte.wifipidgin.NAMECARD";
    boolean userSaved = false;

    boolean userOnline = true;

    String user_description = "伊利丹是玛法里奥·怒风的孪生兄弟，和他的哥哥一样，兄弟俩与女祭司泰兰德·语风在上" +
            "古之战前几千年青梅竹马一起长大。与兄长玛法里奥不同的是，尽管兄弟俩都师从于半神塞纳留斯，伊利丹对于" +
            "德鲁伊教义的学习毫无耐心。";

    private static final String TAG = "DisplayNamecard";



    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_display_namecard);

        Intent intent = getIntent();
        //Display the back button.
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Get the user name from the intent
        String userName = intent.getStringExtra(EXTRA_MESSAGE);
        //Display the user name card title
        actionBar.setTitle("Name Card: [" + userName +"]");

        //Display the user name
        TextView userNameTestView = (TextView) findViewById(R.id.user_name);
        userNameTestView.setText(userName);

        //Display the user image
        ImageView userImageView = (ImageView) findViewById(R.id.user_image);
        userImageView.setImageResource(R.drawable.default_user_image);

        ImageButton savedUserImageView = (ImageButton) findViewById(R.id.user_saved);
        savedUserImageView.setOnClickListener(this);

        Button startChatButton = (Button) findViewById(R.id.startChatButton);
        startChatButton.setText("Start Chat with "+ userName);
        startChatButton.setOnClickListener(this);


        if(userSaved){
            savedUserImageView.setImageResource(R.drawable.saved_user);
        }else{
            savedUserImageView.setImageResource(R.drawable.unsaved_user);
        }

        ImageView onlimeUserImageView = (ImageView) findViewById(R.id.user_online);

        if(userOnline){
            onlimeUserImageView.setImageResource(R.drawable.user_visible);
        }else{
            onlimeUserImageView.setImageResource(R.drawable.user_invisible);
        }

        //Set the description in testview
        TextView userDescriptionTextView = (TextView) findViewById(R.id.user_description);
        userDescriptionTextView.setText(user_description);
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

        }
    }

}
