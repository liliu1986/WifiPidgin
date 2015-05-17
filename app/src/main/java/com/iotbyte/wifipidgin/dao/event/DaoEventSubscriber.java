package com.iotbyte.wifipidgin.dao.event;

/**
 * Subscriber to Dao events. Dao objects generates events when data is changed.
 * All subscriber will be notified on the change.
 */
public interface DaoEventSubscriber {

    /** Notify this subscriber about event
     *
     * @param event event to be notified
     */
    public void onEvent(DaoEvent event);
}
