package com.iotbyte.wifipidgin.dao.event;

import java.util.ArrayList;
import java.util.List;

/**
 * Concrete implementation of DaoEventBoard. This event board is synchronous.
 */
public class DaoEventBoardImpl implements DaoEventBoard {
    public DaoEventBoardImpl() {
        this.subscribers = new ArrayList<>();
    }

    @Override
    public synchronized void registerEventSubscriber(DaoEventSubscriber subscriber) {
        assert subscriber != null;
        subscribers.add(subscriber);
    }

    @Override
    public synchronized void postEvent(DaoEvent event) {
        for (DaoEventSubscriber subscriber : subscribers) {
            subscriber.onEvent(event);
        }
    }

    /** List of subscribers to this event board */
    private List<DaoEventSubscriber> subscribers;
}
