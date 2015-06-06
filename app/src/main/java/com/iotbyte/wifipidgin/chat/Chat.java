package com.iotbyte.wifipidgin.chat;

import com.iotbyte.wifipidgin.message.ChatMessage;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Chat class is the core for chat UI backend. It links to it's corresponding channel by
 * channelIdentifier attribute. It maintains a queue of ChatMessage objects that is intend
 * for this chat.
 * <p/>
 * Created by yefwen@iotbyte.com on 26/03/15.
 */
public class Chat {

    private final String channelIdentifier;

    /* this queue manage all messages with in the chat, include outgoing and incoming message */
    private Queue<ChatMessage> chatMessageQueue;
   // private Context context;

    public Chat(String channelIdentifier) {
       // this.context = context;
        this.channelIdentifier = channelIdentifier;
        chatMessageQueue = new LinkedList<>();
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

    public ChatMessage getMessage() {
        return chatMessageQueue.poll();
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
        return chatMessageQueue.offer(message);
    }

    /**
     * sendMessage()
     * <p/>
     * send message by push message to the out going message queue managed by ChatManager
     * and push the same message to chatMessageQueue for display
     *
     * @param message message to be send out
     * @return true if send successfully, false otherwise
     */

    public boolean sendMessage(ChatMessage message) {
        return ChatManager
                .getInstance()
                .enqueueOutGoingMessageQueue(message.convertMessageToJson()) && this.pushMessage(message);
    }


}
