package com.iotbyte.wifipidgin.chat;

import android.content.Context;

/**
 * Created by yefwen@iotbyte.com on 09/05/15.
 *
 * IncomingMessageHandler continuously monitoring the incomingMessageQueue and try to handle
 * the deque action from ChatManager
 */
public class IncomingMessageHandler {

    private Context context;

    Thread thread = null;

    public IncomingMessageHandler(Context context){
         this.context = context;
    }


    class Handler implements Runnable {

        @Override
        public void run() {

            while (!Thread.currentThread().isInterrupted()) {
               if (!ChatManager.getInstance(context).isIncomingMessageQueueEmpty()){
                   ChatManager.getInstance(context).dequeueIncomingMessageQueue(); // handled by ChatManager
                }
            }

        }
    }

    public void startHandler(){
       thread = new Thread (new Handler());
       thread.start();
    }

    public Context getHanderContext(){
        return this.context;
    }


}
