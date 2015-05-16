package com.iotbyte.wifipidgin.dao.event;

/**
 * Dao event publisher interface.
 */
public interface DaoEventPublisher {
    /**
     * Register an event subscriber. When any DaoEvent occurs, all
     * registered subscribers are notified.
     * @param subscriber subscriber to be registered.
     */
    void registerEventSubscriber(DaoEventSubscriber subscriber);

    /** notify all subscribers about an event
     *
     * @param event event to be notified
    */
    void notifySubscribers(DaoEvent event);
}
