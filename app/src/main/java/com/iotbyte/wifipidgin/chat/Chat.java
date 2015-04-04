package com.iotbyte.wifipidgin.chat;

/**
 * Created by yefwen@iotbyte.com on 26/03/15.
 */
public class Chat {

    private String channelIdentifier;

    public Chat(String channelIdentifier) {
        this.channelIdentifier = channelIdentifier;
    }

    /**
     * Getter method of the corresponding channelIdentifier
     * @return
     */
    public String getChannelIdentifier() {
        return channelIdentifier;
    }
}
