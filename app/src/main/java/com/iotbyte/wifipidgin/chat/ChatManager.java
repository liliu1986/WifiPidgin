package com.iotbyte.wifipidgin.chat;

import android.util.Log;

import com.iotbyte.wifipidgin.message.ChatMessage;
import com.iotbyte.wifipidgin.message.Message;
import com.iotbyte.wifipidgin.message.MessageFactory;
import com.iotbyte.wifipidgin.message.MessageType;

import org.json.JSONException;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

/**
 * ChatManager is the singleton to manage all chat .
 * The current design is not to persist all chat information
 * All chat items are lost when close off the app.
 *
 * The ChatManager also maintains two queues to manage message sending and
 * receiving.
 * Created by yefwen@iotbyte.com on 26/03/15.
 */
public class ChatManager {
    private static ChatManager instance = null;

    private Queue<String> outGoingMessageQueue; //store the outGoingMessage in it's JSON format

    private Queue<Message> incomingMessageQueue; //Store incomingMessage in it's message object

    private HashMap<String, Chat> chatMap; // map channelIdentifier with Chat object

    private ChatManager() {
        outGoingMessageQueue = new LinkedList<>();
        incomingMessageQueue = new LinkedList<>();
        chatMap = new HashMap<>();
    }

    public static ChatManager getInstance() {
        if (instance == null) {
            //Thread Safe with synchronized block
            synchronized (ChatManager.class) {
                if (instance == null) {
                    instance = new ChatManager();
                }
            }
        }
        return instance;
    }

    /**
     * enqueueOutGoingMessageQueue()
     * <p/>
     * push the json format string into outgoing queue
     *
     * @param jsonString the json format string
     * @return true if success otherwise false
     */
    public boolean enqueueOutGoingMessageQueue(String jsonString) {
        return outGoingMessageQueue.offer(jsonString);
    }

    /**
     * dequeueOutGoingMessageQueue()
     * <p/>
     * Remove head of the queue and return it
     *
     * @return the head or null if empty
     */
    public String dequeueOutGoingMessageQueue() {
        return outGoingMessageQueue.poll();
    }

    //TODO:: require exception handling here for corrupted json string
    public boolean enqueueIncomingMessageQueue(String jsonString) {
        try {
            return incomingMessageQueue.offer(MessageFactory.buildMessageByJson(jsonString));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * dequeueIncomingMessageQueue()
     * <p/>
     * A message is removed from incomingMessageQueue and handle by it's corresponding type
     *
     * @return true if dequeue and handles successfully.
     */

    // Note the logic here, a chat has to exist under ChatManager before it can store
    // any message into.
    public boolean dequeueIncomingMessageQueue() {
        Message message = incomingMessageQueue.poll();
        if (null == message || message.getType() == MessageType.ERROR){
            return false;
        }
        Log.v("test chat", "successfully handled!!!");
        switch (message.getType()){
            case CHAT_MESSAGE:{
                if (!chatMap.containsKey(((ChatMessage)message).getChannelIdentifier())) {
                    return false;
                } else {
                    Chat chat = getChatByChannelIdentifier(((ChatMessage)message).getChannelIdentifier());
                    return chat.pushMessage((ChatMessage)message);
                }
            }
            default:
                return false;
        }

    }

    public Chat getChatByChannelIdentifier(String channelIdentifier) {
        if (chatMap.containsKey(channelIdentifier)) {
            return chatMap.get(channelIdentifier);
        } else
            return null;
    }

    //TODO:: add a custom listener to listen to incomingMessageQueue and outGoingMessageQueue change, talk to Di about who making the queue injection


    /**
     * addChat()
     * <p/>
     * add a chat to the ChatManager
     *
     * @param chat a chat to be added
     * @return true if add successfully, false if chat already exist
     */
    public boolean addChat(Chat chat) {
        if (!chatMap.containsValue(chat)) {
            chatMap.put(chat.getChannelIdentifier(), chat);
            return true;
        } else {
            return false;
        }
    }

    /**
     * deleteChat()
     * <p/>
     * delete a chat from ChatManager
     *
     * @param chat a chat to be removed from ChatManager
     * @return true if remove successfully, false if chat already exist
     */
    public boolean deleteChat(Chat chat) {
        if (!chatMap.containsKey(chat)) {
            return false;
        } else {
            chatMap.remove(chat);
            return true;
        }
    }

    /**
     * isIncomingMessageQueueEmpty()
     *
     * check if the incomingMessageQueue is empty or not
     * @return true if empty otherwise false
     */
    public boolean isIncomingMessageQueueEmpty()
    {
        return incomingMessageQueue.isEmpty();
    }

    /**
     * peekIncomingMessageQueue()
     *
     * As name implies, it peek the queue, use as safe guard
     *
     * @return Message from top of the queue without remove it.
     */

    public Message peekIncomingMessageQueue(){
        return incomingMessageQueue.peek();
    }

}
