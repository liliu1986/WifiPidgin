package com.iotbyte.wifipidgin.chat;

import android.content.Context;
import android.os.Handler;

/**
 * Created by yefwen@iotbyte.com on 09/05/15.
 *
 * IncomingMessageHandler continuously monitoring the incomingMessageQueue and try to handle
 * the deque action from ChatManager
 */
public class IncomingMessageHandler {

    private Context context;

    Thread thread = null;

    private Handler handler;

    public IncomingMessageHandler(Context context, Handler handler){
         this.context = context;
         this.handler = handler;
    }



    class ServiceThread implements Runnable {

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!ChatManager.getInstance().isIncomingMessageQueueEmpty()) {
                            ChatManager.getInstance().dequeueIncomingMessageQueue(context); // handled by ChatManager
                        }

                    }
                });

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
