package com.iotbyte.wifipidgin.chat;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.iotbyte.wifipidgin.channel.Channel;
import com.iotbyte.wifipidgin.channel.ChannelManager;
import com.iotbyte.wifipidgin.commmodule.MessageClient;
import com.iotbyte.wifipidgin.dao.DaoError;
import com.iotbyte.wifipidgin.dao.DaoFactory;
import com.iotbyte.wifipidgin.dao.FriendDao;
import com.iotbyte.wifipidgin.friend.Friend;
import com.iotbyte.wifipidgin.message.ChannelCreationRequest;
import com.iotbyte.wifipidgin.message.ChannelCreationResponse;
import com.iotbyte.wifipidgin.friend.Myself;
import com.iotbyte.wifipidgin.message.ChatMessage;
import com.iotbyte.wifipidgin.message.FriendCreationResponse;
import com.iotbyte.wifipidgin.message.FriendImageRequest;
import com.iotbyte.wifipidgin.message.FriendImageResponse;
import com.iotbyte.wifipidgin.message.FriendInfoUpdateResponse;
import com.iotbyte.wifipidgin.message.Message;
import com.iotbyte.wifipidgin.message.MessageFactory;
import com.iotbyte.wifipidgin.message.MessageType;
import com.iotbyte.wifipidgin.nsdmodule.FriendOnlineHashMap;
import com.iotbyte.wifipidgin.ui.notification.UiNotificationHelper;
import com.iotbyte.wifipidgin.utils.Utils;

import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
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

    /**
     * store the outGoingMessage in it's JSON format
     */
    private Queue<Message> outGoingMessageQueue;
    /**
     * Store incomingMessage in it's message object
     */
    private Queue<Message> incomingMessageQueue;
    /**
     * map channelIdentifier with Chat object
     */
    private HashMap<String, Chat> chatMap;

    private MessageClient messageClient;

    // private Context context;
    private static final String TAG = "ChannelManager";

    private ChatManager() {
        outGoingMessageQueue = new ConcurrentLinkedQueue();
        incomingMessageQueue = new ConcurrentLinkedQueue();
        chatMap = new HashMap();
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
     * @param message the message object to be send out
     * @return true if success otherwise false
     */
    public boolean enqueueOutGoingMessageQueue(Message message) {
        return outGoingMessageQueue.offer(message);
    }

    /**
     * dequeueOutGoingMessageQueue()
     * <p/>
     * It remove the message from the outgoingMessageQueue and send it via messageClient.
     *
     * @param context
     * @return true if the message is dequeue properly from outgoingMessageQueue and send via messageClient,
     * return false otherwise
     */
    public boolean dequeueOutGoingMessageQueue(Context context) {
        Message message = outGoingMessageQueue.poll();

        if (null == message) {
            return false;
        }
        Friend receiver = message.getReceiver();
        //In case of Friend creation request/response, the database does not have these friends' record, so
        //don't need to lookup in database
        if (MessageType.FRIEND_CREATION_REQUEST != message.getType() &&
                MessageType.FRIEND_CREATION_RESPONSE != message.getType()) {
            //TODO: there might be performance impact when sending message to lookout DB to update the ip and port
            FriendDao fd = DaoFactory.getInstance().getFriendDao(context, DaoFactory.DaoType.SQLITE_DAO, null);
            // Don't need to update sender information, it has been taken cared in receiver end
            Friend receiverLookUp = fd.findByMacAddress(receiver.getMac());
            if (null != receiverLookUp) {
                if (null != receiverLookUp.getIp()) {
                    receiver.setIp(receiverLookUp.getIp());
                }
                if (0 != receiverLookUp.getPort()) {
                    receiver.setPort(receiverLookUp.getPort());
                }
            }

        }

        messageClient.sendMsg(receiver.getIp(), receiver.getPort(), message.convertMessageToJson());
        return true;
    }

    //TODO:: require exception handling here for corrupted json string
    public boolean enqueueIncomingMessageQueue(String jsonString, Context context) {
        try {
            return incomingMessageQueue.offer(MessageFactory.buildMessageByJson(jsonString, context));
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
                    FriendDao fd = DaoFactory.getInstance().getFriendDao(context, DaoFactory.DaoType.SQLITE_DAO, null);
                    Friend sender = message.getSender();
                    Friend dbSender = fd.findByMacAddress(sender.getMac());
                    if (dbSender != null) {
                        if (!(dbSender.getImageHash().equals(((ChatMessage) message).getImageHashCode()))) {
                            sendFriendImageRequest(sender, context);
                        }
                    }
                    Chat chat = getChatByChannelIdentifier(((ChatMessage) message).getChannelIdentifier());
                    boolean ret = chat.pushMessage((ChatMessage) message);
                    if (ret) {
                        UiNotificationHelper.notifyChatMessage((ChatMessage) message, context);
                    }
                }
            }
            case FRIEND_CREATION_REQUEST: {
                /*
                When a friend creation request is processed, it should create a friend creation response and push it to
                the outgoingQueue.
                 */

                //TODO:: try to use MessageFactory here later
                FriendCreationResponse friendCreationResponse = new FriendCreationResponse(message.getSender(), context);
                return this.enqueueOutGoingMessageQueue(friendCreationResponse);
            }
            case FRIEND_CREATION_RESPONSE: {
                /*
                In response to a friendCreationResponse, the sender of the response is saved into the database
                 */
                FriendDao fd = DaoFactory.getInstance().getFriendDao(context, DaoFactory.DaoType.SQLITE_DAO, null);
                if (null == fd) {
                    return false;
                }
                Friend sender = message.getSender();
                String friendMacString = Utils.macAddressByteToHexString(sender.getMac());

                FriendOnlineHashMap friendOnlineHashMap = FriendOnlineHashMap.getInstance();
                sender.setLastOnlineTimeStamp(new Timestamp(System.currentTimeMillis()));
                friendOnlineHashMap.put(friendMacString, sender);

                Log.d(TAG, "Adding " + friendMacString + " to hashmap");
                //friendOnlineHashMap.printAll();

                if (DaoError.NO_ERROR == fd.add(sender)) {
                    return true;
                } else {
                    return false;
                }
            }
            case CHANNEL_CREATION_REQUEST: {
                /*
                In response to a channel creation request a notification will be push.
                user will prompted to select to join the channel or not join

                As ChannelCreationRequest is a dual functional message, if the channel currently is
                not in local channelManager,it is to request a creation of channel.
                if the channel already exist, it is a notification to update the channel information
                */
                ChannelCreationRequest channelCreationRequest = (ChannelCreationRequest) message;
                Channel channel = channelCreationRequest.getChannel();
                ChannelManager channelManager = ChannelManager.getInstance(context);
                // check if the user is already in the channel, if yes, this is a update message,
                // update the local channel information
                if (null != channelManager.getChannelByIdentifier(channel.getChannelIdentifier())) {
                    channelManager.updateChannel(channel);
                    return true;
                } else {
                    // the channel does not exist, create the channel
                    UiNotificationHelper.notifyChannelCreationRequest(channelCreationRequest, context);
                    return true;
                }
            }
            case CHANNEL_CREATION_RESPONSE: {
                /*
                when receive a channel creation response, it means a friend is accept the channel creation actions.
                user will update his channel friend list to include the sender of this response.
                and send out a channel creation request(remember it is dual functional message) to update the channel
                information to all friends in the list
                 */

                ChannelCreationResponse channelCreationResponse = (ChannelCreationResponse) message;
                Channel channel = ChannelManager.getInstance(context).
                        getChannelByIdentifier(channelCreationResponse.getChannelIdentifier());
                channel.addFriend(channelCreationResponse.getSender());
                ChannelManager.getInstance(context).updateChannel(channel);//notify the channel is updated, and this will kick in UI update
                ChannelManager.getInstance(context).sendChannelCreationMessageToAll(channel);//send the message out to notify all members of the channel
                return true;
            }
            case FRIEND_INFO_UPDATE_REQUEST: {
                FriendDao fd = DaoFactory.getInstance().getFriendDao(context,
                        DaoFactory.DaoType.SQLITE_DAO,
                        null);
                if (null == fd) {
                    return false;
                }
                Friend myself = fd.findById(Myself.SELF_ID);
                assert null != myself;

                FriendInfoUpdateResponse response =
                        new FriendInfoUpdateResponse(message.getSender(), context,
                                myself.getDescription());
                return this.enqueueOutGoingMessageQueue(response);
            }
            case FRIEND_INFO_UPDATE_RESPONSE: {
                FriendDao fd = DaoFactory.getInstance().getFriendDao(context,
                        DaoFactory.DaoType.SQLITE_DAO,
                        null);
                if (null == fd) {
                    return false;
                }

                Friend sender = fd.findByMacAddress(message.getSender().getMac());
                if (null != sender) {
                    String description = ((FriendInfoUpdateResponse) message).getDescription();
                    sender.setDescription(description);
                    fd.update(sender);
                    return true;
                } else {
                    Log.d(TAG, "Received a FRIEND_INFO_UPDATE_RESPONSE from unknown sender " +
                            "with mac:" + message.getSender().getMac());
                    return false;
                }
            }
            case FRIEND_IMAGE_REQUEST: {
                Log.d("test chat", "Got a FRIEND_IMAGE_REQUEST");
                FriendDao fd = DaoFactory.getInstance().getFriendDao(context, DaoFactory.DaoType.SQLITE_DAO, null);
                Friend myself = fd.findById(Myself.SELF_ID);
                String imageBase64 = Utils.convertImgToBase64(myself.getImagePath());
                FriendImageResponse friendImageResponse = new FriendImageResponse(message.getSender(), context, imageBase64);
                return this.enqueueOutGoingMessageQueue(friendImageResponse);
            }
            case FRIEND_IMAGE_RESPONSE: {
                FriendDao fd = DaoFactory.getInstance().getFriendDao(context, DaoFactory.DaoType.SQLITE_DAO, null);
                if (null == fd) {
                    return false;
                }

                Friend sender = fd.findByMacAddress(message.getSender().getMac());
                if (sender != null) {
                    Log.d("test chat", "Got the sender from db");
                    String imageBase64Encode = ((FriendImageResponse) message).getImageBase64Encode();
                    Bitmap imageBitmap = Utils.convertBase64toBitmap(imageBase64Encode);

                    //The absolute path that the friend image will be stored at.
                    String imagePath = context.getExternalFilesDir(null).toString() +
                            File.separator + "userImage" + Utils.macAddressByteToHexString(sender.getMac());
                    File file = new File(imagePath);
                    if (file.exists()) {
                        file.delete();
                        file = new File(imagePath);
                    }
                    try {
                        OutputStream outStream = new FileOutputStream(file);
                        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                        outStream.flush();
                        outStream.close();
                        sender.setImagePath(imagePath);
                        fd.update(sender);
                        return true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    Log.d("CCC", "NO SENDER INFORMATION");
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

    private void sendFriendImageRequest(Friend receiver, Context context) {
        FriendImageRequest friendImageRequest = new FriendImageRequest(receiver, context);
        ChatManager chatManager = ChatManager.getInstance();
        chatManager.enqueueOutGoingMessageQueue(friendImageRequest);
    }


}
