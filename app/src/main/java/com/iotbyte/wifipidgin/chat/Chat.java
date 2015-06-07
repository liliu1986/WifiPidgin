package com.iotbyte.wifipidgin.chat;

import android.content.Context;

import com.iotbyte.wifipidgin.channel.Channel;
import com.iotbyte.wifipidgin.channel.ChannelManager;
import com.iotbyte.wifipidgin.dao.DaoFactory;
import com.iotbyte.wifipidgin.dao.FriendDao;
import com.iotbyte.wifipidgin.friend.Friend;
import com.iotbyte.wifipidgin.message.ChatMessage;

import java.util.ArrayList;

/**
 * Chat class is the core for chat UI backend. It links to it's corresponding channel by
 * channelIdentifier attribute. It maintains a queue of ChatMessage objects that is intend
 * for this chat.
 * <p/>
 * Created by yefwen@iotbyte.com on 26/03/15.
 */
public class Chat {

    private final String channelIdentifier;

    private ChatMessageQueueChangeListener chatMessageQueueChangeListener;

    /* this queue manage all messages with in the chat, include outgoing and incoming message */
    private ArrayList<ChatMessage> chatMessageList;
   // private Context context;

    public Chat(String channelIdentifier) {
       // this.context = context;
        this.channelIdentifier = channelIdentifier;
        chatMessageList = new ArrayList<>();


    }

    /**
     * getChannelIdentifier()
     * <p/>
     * Getter method of the corresponding channelIdentifier
     *
     * @return channelIdentifier
     */
    public String getChannelIdentifier() {
        return channelIdentifier;
    }

    /**
     * getChatMessageList()
     *
     * Getter method for ChatMessageList
     * @return ChatMessageList
     */
    public ArrayList<ChatMessage> getChatMessageList() {
        return chatMessageList;
    }

    /**
     * pushMessage()
     * <p/>
     * push a message to the queue, this action should be called by ChatManager and sendMessage
     * method
     *
     * @param message the message object to be insert into the queue
     * @return true if insert successfully, false otherwise
     */
    public boolean pushMessage(ChatMessage message) {
        if (null != chatMessageQueueChangeListener) {
            chatMessageQueueChangeListener.onChatMessageQueueNotEmpty();
        }
        return chatMessageList.add(message);
    }

    /**
     * sendMessage()
     * <p/>
     * send message by push message to the out going message queue managed by ChatManager
     * and push the same message to chatMessageList for display
     *
     * @param message message to be send out
     * @return true if send successfully, false otherwise
     */
    @Deprecated
    public boolean sendMessage(ChatMessage message) {
        return ChatManager
                .getInstance()
                .enqueueOutGoingMessageQueue(message.convertMessageToJson()) && this.pushMessage(message);
    }

    /**
     * setChatMessageQueueChangeListener()
     * <p/>
     * A setter method for ChatMessageQueueChangeListener
     *
     * @param chatMessageQueueChangeListener the listener to be set
     */
    public void setChatMessageQueueChangeListener(ChatMessageQueueChangeListener chatMessageQueueChangeListener) {
        this.chatMessageQueueChangeListener = chatMessageQueueChangeListener;
    }


    /**
     * sendMessageToAll()
     * <p/>
     * Send message to all friends of the channel, also post this message to this chat's own queue by
     * calling {@link #pushMessage(ChatMessage)}
     *
     * @param messageBody
     * @param context
     */

    public void sendMessageToAll(String messageBody, Context context) {

        Channel channel = ChannelManager.getInstance(context).getChannelByIdentifier(this.channelIdentifier);
        ChatMessage chatMessage;
        for (Friend friend : channel.getFriendsList()) {
            chatMessage = new ChatMessage(friend, this.getChannelIdentifier(), messageBody,context);
            ChatManager.getInstance().enqueueOutGoingMessageQueue(chatMessage.convertMessageToJson());
        }
        FriendDao fd = DaoFactory.getInstance().getFriendDao(context, DaoFactory.DaoType.SQLITE_DAO, null);
        Friend myself = fd.findById(0);
        chatMessage = new ChatMessage(myself, this.channelIdentifier, messageBody,context);
        pushMessage(chatMessage);
    }
}
