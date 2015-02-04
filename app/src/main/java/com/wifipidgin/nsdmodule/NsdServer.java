package com.wifipidgin.nsdmodule;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;

public class NsdServer {
	
	Context mContext;
	NsdManager mNsdManager;
	
    NsdManager.ResolveListener mResolveListener;
    NsdManager.DiscoveryListener mDiscoveryListener;
    NsdManager.RegistrationListener mRegistrationListener;
    
    public static boolean RegisterredServiceFlag = false;
    
    private static final String TAG = "NsdServer";
    public static final String SERVICE_TYPE = "_http._tcp.";
    
    public String mServiceName = "MyNsdService";
    public String mServiceName2 = "MyNsdService";
    
    private int mServerPort = -1;
	private ServerSocket mServerSocket = null;
	private ServerStarter mServerStarter;
	
    public NsdServer(Context context, Handler handler) {
        mContext = context;
        mNsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
        mServerStarter = new ServerStarter(handler);
    }
    
    public void initializeNsdServer() {
        initializeServiceBroadcastListener();
    }
    
    private void initializeServiceBroadcastListener() {
        mRegistrationListener = new NsdManager.RegistrationListener() {

            @Override
            public void onServiceRegistered(NsdServiceInfo NsdServiceInfo) {
            	RegisterredServiceFlag = true;
                mServiceName = NsdServiceInfo.getServiceName();
                Log.d(TAG, "The service " + mServiceName + " has been registered successfully!");
                Log.d(TAG, "Actual Service Name is " + mServiceName);
            }
            
            @Override
            public void onRegistrationFailed(NsdServiceInfo arg0, int arg1) {
            	Log.d(TAG, "The service " + mServiceName + " cannot be registered!");
            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo arg0) {
            	Log.d(TAG, "Unregisterring the service!");
            }
            
            @Override
            public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
            }
            
        };
    }
    
    public void broadcastService() {
    	int port=getServerPort();
    	
    	if(port==-1)
    		return;
    	
        NsdServiceInfo serviceInfo  = new NsdServiceInfo();
        serviceInfo.setPort(port);
        serviceInfo.setServiceName(mServiceName);
        serviceInfo.setServiceType(SERVICE_TYPE);
        
        Log.e(TAG, "The service name is about set to be " + mServiceName);
        Log.e(TAG, "The service name is set to be " + serviceInfo.getServiceName());
        
        mNsdManager.registerService(
                serviceInfo, NsdManager.PROTOCOL_DNS_SD, mRegistrationListener);
        
    }
    public int getServerPort() {
        return mServerPort;
    }
    
    private void setServerPort(int port) {
    	mServerPort = port;
    }
    
    public class ServerStarter {
        
        Thread mThread = null;

        public ServerStarter(Handler handler) {
            mThread = new Thread(new ServerThread());
            mThread.start();
        }

        public void tearDown() {
            mThread.interrupt();
            try {
                mServerSocket.close();
            } catch (IOException ioe) {
                Log.e(TAG, "Error when closing server socket.");
            }
        }

        class ServerThread implements Runnable {

            @Override
            public void run() {

                try {
                    mServerSocket = new ServerSocket(0);
                    setServerPort(mServerSocket.getLocalPort());

                } catch (IOException e) {
                    Log.e(TAG, "Error creating ServerSocket: ", e);
                    e.printStackTrace();
                }
            }
        }
    }
    
    public void tearDown() {
    	try{
    		if(RegisterredServiceFlag)
    			mNsdManager.unregisterService(mRegistrationListener);
    	}catch(Exception e){
    		Log.w(TAG, "Problem happened during unregisterService()!");
    	}
    	
    }
}
