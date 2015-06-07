package com.iotbyte.wifipidgin.ui;

import android.content.Context;
import android.util.Log;
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
import java.util.List;

public class ChatArrayAdapter extends ArrayAdapter<String> {

    private TextView chatText;
    private List chatMessageList = new ArrayList();
    private LinearLayout singleMessageContainer;

    Context context;
    int layoutResourceId;
    ArrayList<String> data = null;

    public ChatArrayAdapter(Context context, int layoutResourceId, ArrayList<String> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.activity_chat_single_message, parent, false);
        }
        singleMessageContainer = (LinearLayout) row.findViewById(R.id.singleMessageContainer);
        String chatMessageObj = data.get(position);
        chatText = (TextView) row.findViewById(R.id.singleMessage);
        chatText.setText(chatMessageObj);
        chatText.setBackgroundResource(chatMessageObj.equals("1") ? R.drawable.bubble_b : R.drawable.bubble_a);
        singleMessageContainer.setGravity(chatMessageObj.equals("1") ? Gravity.RIGHT : Gravity.LEFT);
        return row;
    }
}
