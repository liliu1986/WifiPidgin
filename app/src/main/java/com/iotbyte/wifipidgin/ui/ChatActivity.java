package com.iotbyte.wifipidgin.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.iotbyte.wifipidgin.R;
import com.iotbyte.wifipidgin.chat.Chat;
import com.iotbyte.wifipidgin.chat.ChatManager;
import com.iotbyte.wifipidgin.chat.ChatMessageQueueChangeListener;

public class ChatActivity extends Activity {
    private static final String TAG = "ChatActivity";

    private ChatArrayAdapter chatArrayAdapter;
    private ListView listView;
    private EditText chatText;
    private Button buttonSend;
    String ChannelId;

    ChatManager chatManager;
    Chat chat;
    Context context;
    Intent i;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        context = this;

        ChannelId = (String) getIntent().getExtras().get("ChannelId");

        chatManager = ChatManager.getInstance(getApplicationContext());
        chat = chatManager.getChatByChannelIdentifier(ChannelId);
        if (null == chat) {
            chat = new Chat(ChannelId);
            chatManager.addChat(chat);
        }

        buttonSend = (Button) findViewById(R.id.buttonSendMessage);

        listView = (ListView) findViewById(R.id.chatList);
        chatArrayAdapter = new ChatArrayAdapter(context, R.layout.activity_chat_single_message, chat.getChatMessageList());
        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setAdapter(chatArrayAdapter);

        chatText = (EditText) findViewById(R.id.enterMessage);

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                sendChatMessage();
            }
        });

        chat.setChatMessageQueueChangeListener(new ChatMessageQueueChangeListener() {
            @Override
            public void onChatMessageQueueNotEmpty() {
                refreshChatList();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                return true;
            case R.id.chat_participants:
                i = new Intent(this, ChannelPropertyActivity.class);
                i.putExtra("ChannelId", ChannelId);
                startActivity(i);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendChatMessage() {
        String message = chatText.getText().toString();
        if (!message.equals("")) {
            chat.sendMessageToAll(message, context);
            chatText.setText("");
        }
    }

    private void refreshChatList() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                chatArrayAdapter.clear();
                chatArrayAdapter.addAll(chat.getChatMessageList());
                chatArrayAdapter.notifyDataSetChanged();
                listView.setSelection(chatArrayAdapter.getCount() - 1);
            }
        });
    }
}