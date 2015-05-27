package com.iotbyte.wifipidgin.dao.event;

/**
 * Dao event publisher interface.
 */
public interface DaoEventBoard {
    /**
     * Register an event subscriber. When any DaoEvent occurs, all
     * registered subscribers are notified.
     * @param subscriber subscriber to be registered.
     */
    void registerEventSubscriber(DaoEventSubscriber subscriber);

    /** post an event to event board, all subscribers will be notified about the event
     *
     * @param event event to be posted
     */
    void postEvent(DaoEvent event);
}
