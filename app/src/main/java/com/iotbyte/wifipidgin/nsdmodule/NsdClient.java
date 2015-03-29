package com.iotbyte.wifipidgin.nsdmodule;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;

import com.iotbyte.wifipidgin.channel.Channel;
import com.iotbyte.wifipidgin.friend.Friend;
import com.iotbyte.wifipidgin.ui.tempDb;

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

    NsdServiceInfo mService;

    //The list of channels that is alive
    private List<Channel> ChannelList;
    //The list of users showing themselves as visible within the same lan network
    //private List<Friend> nearbyFriendList;
    private tempDb mdb;

    private boolean isDiscovering=false;
    public NsdClient(Context context, NsdManager inNsdManager, tempDb inDb) {
        mContext = context;
        mNsdManager = inNsdManager;
        ChannelList=new ArrayList<Channel>();
        mdb = inDb;
        //nearbyFriendList =new ArrayList<Friend>();
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

                Log.d(TAG, "Service found: "+ service);
                if (service.getServiceType().equals(SERVICE_TYPE)){
                    mNsdManager.resolveService(service, new NsdManager.ResolveListener() {
                        @Override
                        public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                            Log.e(TAG, "Resolve Failed: " + serviceInfo);
                        }
                        @Override
                        public void onServiceResolved(NsdServiceInfo serviceInfo) {
                            Log.e(TAG, "Resolve Succeeded. " + serviceInfo.getServiceName());

                            if (serviceInfo.getServiceName().equals(mServiceName)) {
                                Log.d(TAG, "Same Service Name." + mService.getHost().getHostAddress());
                                return;
                            }else if (serviceInfo.getServiceName().contains(mServiceName)){
                                mService = serviceInfo;
                                InetAddress host = mService.getHost();
                                Log.d(TAG, "The friend's ip is " + host.getHostAddress());
                                Log.d(TAG, "The ip for my current device is " + Utils.getIPAddress(true));
                                if (!host.getHostAddress().equals(Utils.getIPAddress(true))){
                                    Friend newFriend = new Friend(host);
                                    mdb.addFriendToList(newFriend);
                                }else{
                                    Log.d(TAG, "Same IP.");
                                }

                            }else{
                                Log.d(TAG, "Unknown App");
                            }

                        }
                    });
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

                Friend newFriend = new Friend(host);
                mdb.addFriendToList(newFriend);
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

    //public List<Friend> getNearbyFriendList(){
    //	return nearbyFriendList;
    //}

    //Returns if currently the discovery is on going.
    public boolean getIsDiscovering(){
        return isDiscovering;
    }
}
