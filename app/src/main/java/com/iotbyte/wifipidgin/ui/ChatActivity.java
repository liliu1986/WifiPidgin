package com.iotbyte.wifipidgin.ui;

import android.app.Activity;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.iotbyte.wifipidgin.R;
import com.iotbyte.wifipidgin.message.ChatMessage;

import java.util.ArrayList;

public class ChatActivity extends Activity {
    private static final String TAG = "ChatActivity";

    private ChatArrayAdapter chatArrayAdapter;
    private ListView listView;
    private EditText chatText;
    private Button buttonSend;
    String ChannelId;

    ArrayList<String> data = new ArrayList<String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        ChannelId = (String) getIntent().getExtras().get("ChannelId");

        data.add("Message 1");
        data.add("Message 2");
        data.add("Message 3");

        buttonSend = (Button) findViewById(R.id.buttonSendMessage);

        listView = (ListView) findViewById(R.id.chatList);
        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.activity_chat_single_message, data);
        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setAdapter(chatArrayAdapter);

        chatText = (EditText) findViewById(R.id.enterMessage);

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                sendChatMessage();
            }
        });
    }

    private void sendChatMessage() {
        String message = chatText.getText().toString();
        if (!message.equals("")) {
            data.add(message);
            listView.setSelection(chatArrayAdapter.getCount() - 1);
            chatText.setText("");
        }
    }
}