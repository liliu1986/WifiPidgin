package com.iotbyte.wifipidgin.chat;

import android.content.Context;
import android.os.Handler;

/**
 * Created by yefwen@iotbyte.com on 20/05/15.
 *
 * OutgoingMessageHandler continuously monitoring the outGoingMessageQueue and try to handle
 * the dequeue action from ChatManager
 */
public class OutgoingMessageHandler  {
    private Context context;
    private Handler handler;


    Thread thread = null;

    public OutgoingMessageHandler(Context context, Handler handler){
        this.context = context;
        this.handler = handler;
    }


    class ServiceThread implements Runnable {

        @Override
        public void run() {

            while (!Thread.currentThread().isInterrupted()) {
                if (!ChatManager.getInstance().isOutGoingMessageQueueEmpty()){
                    ChatManager.getInstance().dequeueOutGoingMessageQueue(); // handled by ChatManager
                }

            }

        }
    }


    private void runOnUiThread(Runnable runnable) {
        handler.post(runnable);
    }

    public void startHandler(){
        thread = new Thread (new ServiceThread());
        thread.start();
    }

    public Context getHanderContext(){
        return this.context;
    }


}
