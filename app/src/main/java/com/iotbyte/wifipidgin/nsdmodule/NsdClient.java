package com.iotbyte.wifipidgin.nsdmodule;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;

import com.iotbyte.wifipidgin.channel.Channel;
import com.iotbyte.wifipidgin.user.User;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class NsdClient {
	
    NsdManager.ResolveListener mResolveListener;
    NsdManager.DiscoveryListener mDiscoveryListener;
    
    public static final String TAG = "NsdClient";
    
    Context mContext;
    NsdManager mNsdManager;
    
    public static final String SERVICE_TYPE = "_http._tcp.";
    public String mServiceName = "WiFiPidginNsdService";
    public String mServiceName2 = "MyNsdService";
    
    NsdServiceInfo mService;

	//The list of channels that is alive 
	private List<Channel> ChannelList;
	//The list of users showing themselves as visible within the same lan network
	private List<User> NearbyUserList;
	
	
	private boolean isDiscovering=false;
    public NsdClient(Context context, NsdManager inNsdManager) {
    	mContext = context;
    	mNsdManager = inNsdManager;
        ChannelList=new ArrayList<Channel>();
        NearbyUserList=new ArrayList<User>();
    }
    public void initializeNsdClient() {
    	initializeDiscoveryListener();
    	initializeResolveListener();
    }
    public void initializeDiscoveryListener() {
        mDiscoveryListener = new NsdManager.DiscoveryListener() {

            @Override
            public void onDiscoveryStarted(String regType) {
                Log.d(TAG, "Service discovery started");
            }

            @Override
            public void onServiceFound(NsdServiceInfo service) {
            	
            	Log.d(TAG, "A service has been found: " + service.getHost());
            	//service.getHost().toString();
                if (!service.getServiceType().equals(SERVICE_TYPE)) {
                    Log.d(TAG, "Unknown Service Type: " + service.getServiceType());
                } else if (service.getServiceName().equals(mServiceName)) {
                    Log.d(TAG, "Same machine: " + mServiceName);
                } else if (service.getServiceName().contains(mServiceName2)){
                	Log.d(TAG, "Service discovery success: " + service.getServiceName());
                	Log.d(TAG, "Found services succefully, will process.");
                    mNsdManager.resolveService(service, mResolveListener);
                }
            }

            @Override
            public void onServiceLost(NsdServiceInfo service) {
                Log.e(TAG, "service lost" + service);
                if (mService == service) {
                    mService = null;
                }
            }
            
            @Override
            public void onDiscoveryStopped(String serviceType) {
                Log.i(TAG, "Discovery stopped: " + serviceType);
            }

            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
                mNsdManager.stopServiceDiscovery(this);
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
                mNsdManager.stopServiceDiscovery(this);
            }
        };
    }
    public void initializeResolveListener() {
        mResolveListener = new NsdManager.ResolveListener() {

            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                Log.e(TAG, "Resolve failed" + errorCode);
            }

            @Override
            public void onServiceResolved(NsdServiceInfo serviceInfo) {
            	
            	Log.e(TAG, "Resolve Succeeded. " + serviceInfo.getServiceName());

                if (serviceInfo.getServiceName().equals(mServiceName)) {
                    Log.d(TAG, "Same IP.");
                    return;
                }
                mService = serviceInfo;
                InetAddress host = mService.getHost();
                Log.d(TAG, "The host ip is " + host.getHostAddress());
                Log.d(TAG, "The ip for my current device is " + Utils.getIPAddress(true));
                
                User newUser = new User(host);
                NearbyUserList.add(newUser);
            }
        };
    }
    
    public void discoverServices() {
    	isDiscovering=true;
        mNsdManager.discoverServices(
                SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);
    }
    public void stopDiscovery() {
    	isDiscovering=false;
        mNsdManager.stopServiceDiscovery(mDiscoveryListener);
    }
    
    public NsdServiceInfo getChosenServiceInfo() {
        return mService;
    }
    public List<Channel> getChannelList(){
    	return ChannelList;
    }
    public List<User> getNearbyUserList(){
    	return NearbyUserList;
    }
    
    //Returns if currently the discovery is on going.
    public boolean getIsDiscovering(){
    	return isDiscovering;
    }
}
