package com.iotbyte.wifipidgin.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        context = this;

        ChannelId = (String) getIntent().getExtras().get("ChannelId");

        chatManager = ChatManager.getInstance();
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

    private void sendChatMessage() {
        String message = chatText.getText().toString();
        if (!message.equals("")) {
            chat.sendMessageToAll(message, context);
            chatText.setText("");
        }
    }

    private void refreshChatList() {
        chatArrayAdapter.clear();
        Log.i("TEST", "Count: " + chat.getChatMessageList().size());
        chatArrayAdapter.addAll(chat.getChatMessageList());
        chatArrayAdapter.notifyDataSetChanged();
        listView.setSelection(chatArrayAdapter.getCount() - 1);
    }
}