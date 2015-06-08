package com.iotbyte.wifipidgin.nsdmodule;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

public class NsdServer {
	
	Context mContext;
	NsdManager mNsdManager;

    NsdManager.RegistrationListener mRegistrationListener;
    
    public static boolean RegisterredServiceFlag = false;
    
    private static final String TAG = "NsdServer";
    public static final String SERVICE_TYPE = "_http._tcp.";

    public String mServiceName = "WiFiPidginNsdService";
    public String mServiceName2 = "MyNsdService";
    
    private int mServerPort = -1;
	private ServerSocket mServerSocket = null;
	private ServerStarter mServerStarter;
    private InetAddress nsdHost;

    //private ServerSocket mServerSocket;
    public NsdServer(Context context, ServerSocket inServerSocket) {
        mContext = context;
        mServerSocket = inServerSocket;
        mNsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
        
        setServerPort(mServerSocket.getLocalPort());
        //mServerStarter = new ServerStarter();
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

                setHostIP(NsdServiceInfo.getHost());

                Log.d(TAG, "The service " + mServiceName + " has been registered successfully!");
                //Log.d(TAG, "My IP is " + NsdServiceInfo.getHost().toString());
                //Log.d(TAG, "My Port is " + NsdServiceInfo.getPort());

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
                Log.d(TAG, "Failed to register the service " + mServiceName );
            }
            
        };
    }
    
    public void broadcastService() {
    	int port=getServerPort();
    	
    	if(port==-1)
    		return;

        // Embed mac address into service name
        // Format is serviceName-macAddress
        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        String macString = wInfo.getMacAddress();
        String serviceName = mServiceName + "-" + macString;
        Log.d(TAG, "Service name is " + serviceName);

        NsdServiceInfo serviceInfo  = new NsdServiceInfo();
        serviceInfo.setPort(port);
        serviceInfo.setServiceName(serviceName);
        serviceInfo.setServiceType(SERVICE_TYPE);

        //Log.e(TAG, "The service name is about set to be " + mServiceName);
        //Log.e(TAG, "The service name is set to be " + serviceInfo.getServiceName());
        
        mNsdManager.registerService(
                serviceInfo, NsdManager.PROTOCOL_DNS_SD, mRegistrationListener);
        
    }
    public int getServerPort() {
        return mServerPort;
    }
    
    private void setServerPort(int port) {
    	mServerPort = port;
    }
    private void setHostIP(InetAddress inHost){
        nsdHost = inHost;
    }
    public class ServerStarter {
        
        Thread mThread = null;

        public ServerStarter() {
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
                setServerPort(mServerSocket.getLocalPort());
                //try {
                    //mServerSocket = new ServerSocket(0);

                //} catch (IOException e) {
                //    Log.e(TAG, "Error creating ServerSocket: ", e);
                //    e.printStackTrace();
                //}
            }
        }
    }
    
    public void tearDown() {
    	try{
            mNsdManager.unregisterService(mRegistrationListener);
    	}catch(Exception e){
    		Log.w(TAG, "Problem happened during unregisterService()!");
    	}
    }
}
