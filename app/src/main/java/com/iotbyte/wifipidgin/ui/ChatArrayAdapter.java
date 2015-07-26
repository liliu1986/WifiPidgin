package com.iotbyte.wifipidgin.ui;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iotbyte.wifipidgin.R;
import com.iotbyte.wifipidgin.message.ChatMessage;

import java.util.ArrayList;

public class ChatArrayAdapter extends ArrayAdapter<ChatMessage> {

    private TextView chatTextView;
    private TextView senderNameView;
    private LinearLayout singleMessageContainer;

    Context context;
    int layoutResourceId;
    ArrayList<ChatMessage> data = null;

    public ChatArrayAdapter(Context context, int layoutResourceId, ArrayList<ChatMessage> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        Boolean self;

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.activity_chat_single_message, parent, false);
        }
        singleMessageContainer = (LinearLayout) row.findViewById(R.id.singleMessageContainer);
        ChatMessage chatMessageObj = data.get(position);

        self = chatMessageObj.isFromMyself();

        chatTextView = (TextView) row.findViewById(R.id.singleMessage);
        chatTextView.setText(chatMessageObj.getMessageBody());

        senderNameView = (TextView) row.findViewById(R.id.senderName);
        senderNameView.setText(chatMessageObj.getSender().getName());

        singleMessageContainer.setGravity(self ? Gravity.RIGHT : Gravity.LEFT);
        chatTextView.setBackgroundResource(self ? R.drawable.bubble_a : R.drawable.bubble_b);
        senderNameView.setVisibility(self ? View.GONE : View.VISIBLE);

        return row;
    }
}