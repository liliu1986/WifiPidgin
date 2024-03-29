package com.iotbyte.wifipidgin.chat;

import android.content.Context;

/**
 * Created by yefwen@iotbyte.com on 20/05/15.
 *
 * OutgoingMessageHandler continuously monitoring the outGoingMessageQueue and try to handle
 * the dequeue action from ChatManager
 */
public class OutgoingMessageHandler  {
    private Context context;
   // private Handler handler;


    Thread thread = null;

    public OutgoingMessageHandler(Context context){
        this.context = context;
     //   this.handler = handler;
    }


    class ServiceThread implements Runnable {

        @Override
        public void run() {

            while (!Thread.currentThread().isInterrupted()) {
                if (!ChatManager.getInstance(context).isOutGoingMessageQueueEmpty()){
                    ChatManager.getInstance(context).dequeueOutGoingMessageQueue(context); // handled by ChatManager
                }

            }

        }
    }

/*
    private void runOnUiThread(Runnable runnable) {
        handler.post(runnable);
    }*/

    public void startHandler(){
        thread = new Thread (new ServiceThread());
        thread.start();
    }

    public Context getHanderContext(){
        return this.context;
    }


}
