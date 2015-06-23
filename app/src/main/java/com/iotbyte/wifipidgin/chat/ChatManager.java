package com.iotbyte.wifipidgin.chat;

import android.content.Context;
import android.util.Log;

import com.iotbyte.wifipidgin.commmodule.MessageClient;
import com.iotbyte.wifipidgin.dao.DaoError;
import com.iotbyte.wifipidgin.dao.DaoFactory;
import com.iotbyte.wifipidgin.dao.FriendDao;
import com.iotbyte.wifipidgin.friend.Friend;
import com.iotbyte.wifipidgin.message.ChatMessage;
import com.iotbyte.wifipidgin.message.FriendCreationResponse;
import com.iotbyte.wifipidgin.message.Message;
import com.iotbyte.wifipidgin.message.MessageFactory;
import com.iotbyte.wifipidgin.message.MessageType;
import com.iotbyte.wifipidgin.nsdmodule.FriendOnlineHashMap;
import com.iotbyte.wifipidgin.utils.Utils;

import org.json.JSONException;

import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * ChatManager is the singleton to manage all chat .
 * The current design is not to persist all chat information
 * All chat items are lost when close off the app.
 * <p/>
 * The ChatManager also maintains two queues to manage message sending and
 * receiving.
 * Created by yefwen@iotbyte.com on 26/03/15.
 */
public class ChatManager {
    private static ChatManager instance = null;

    private Queue<String> outGoingMessageQueue; //store the outGoingMessage in it's JSON format

    private Queue<Message> incomingMessageQueue; //Store incomingMessage in it's message object

    private HashMap<String, Chat> chatMap; // map channelIdentifier with Chat object

    private MessageClient messageClient;

   // private Context context;

    private ChatManager() {
        outGoingMessageQueue = new ConcurrentLinkedQueue<>();
        incomingMessageQueue = new ConcurrentLinkedQueue<>();
        chatMap = new HashMap<>();
        messageClient = new MessageClient();
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
     * It remove the message from the outgoingMessageQueue and send it via messageClient.
     *
     * @return true if the message is dequeue properly from outgoingMessageQueue and send via messageClient,
     * return false otherwise
     */
    public boolean dequeueOutGoingMessageQueue() {
        String message = outGoingMessageQueue.poll();

        if (null == message) {
            return false;
        }
        try {
            Message outMsg = MessageFactory.buildMessageByJson(message);
            Friend receiver = outMsg.getReceiver();
            if (outMsg.getType() == MessageType.FRIEND_CREATION_REQUEST){
                //When the friend creation request is sent out, remove it from the map
                FriendOnlineHashMap friendOnlineHashMap = FriendOnlineHashMap.getInstance();
                String friendMacString = Utils.macAddressByteToHexString(receiver.getMac());
                friendOnlineHashMap.removeFriendbyMac(friendMacString);
            }
            messageClient.sendMsg(receiver.getIp(), receiver.getPort(), message);
            return true;
        } catch (JSONException ex) {  //FIXME:: require better exception handle!!!
            ex.printStackTrace();
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        }

        return false;
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
    public boolean dequeueIncomingMessageQueue(Context context) {
        Message message = incomingMessageQueue.poll();
        if (null == message || message.getType() == MessageType.ERROR) {
            return false;
        }
        Log.v("test chat", "Get: " + message.convertMessageToJson());
        switch (message.getType()) {
            case CHAT_MESSAGE: {
                if (!chatMap.containsKey(((ChatMessage) message).getChannelIdentifier())) {
                    return false;
                } else {
                    Chat chat = getChatByChannelIdentifier(((ChatMessage) message).getChannelIdentifier());
                    return chat.pushMessage((ChatMessage) message);
                }
            }
            case FRIEND_CREATION_REQUEST: {
                /*
                When a friend creation request is processed, it should create a friend creation response and push it to
                the outgoingQueue.
                 */

                //TODO:: try to use MessageFactory here later
                FriendCreationResponse friendCreationResponse = new FriendCreationResponse(message.getSender(),context);
                return this.enqueueOutGoingMessageQueue(friendCreationResponse.convertMessageToJson());
            }
            case FRIEND_CREATION_RESPONSE: {
                /*
                In response to a friendCreationResponse, the sender of the response is saved into the database
                 */
                FriendDao fd = DaoFactory.getInstance().getFriendDao(context, DaoFactory.DaoType.SQLITE_DAO, null);
                if (null == fd){
                    return false;
                }
                Friend sender = message.getSender();
                String friendMacString = Utils.macAddressByteToHexString(sender.getMac());

                FriendOnlineHashMap friendOnlineHashMap = FriendOnlineHashMap.getInstance();
                sender.setLastOnlineTimeStamp(new Timestamp(System.currentTimeMillis()));
                friendOnlineHashMap.put(friendMacString, sender);

                if (DaoError.NO_ERROR == fd.add(sender)){
                    return true;
                } else
                {
                    return false;
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
     * <p/>
     * check if the incomingMessageQueue is empty or not
     *
     * @return true if empty otherwise false
     */
    public boolean isIncomingMessageQueueEmpty() {
        return incomingMessageQueue.isEmpty();
    }

    /**
     * peekIncomingMessageQueue()
     * <p/>
     * As name implies, it peek the queue, use as safe guard
     *
     * @return Message from top of the queue without remove it.
     */

    public Message peekIncomingMessageQueue() {
        return incomingMessageQueue.peek();
    }

    /**
     * isOutGoingMessageQueueEmpty()
     * <p/>
     * check if the outGoingMessageQueue is empty or not
     *
     * @return true if empty otherwise false
     */
    public boolean isOutGoingMessageQueueEmpty() {
        return outGoingMessageQueue.isEmpty();
    }

}
