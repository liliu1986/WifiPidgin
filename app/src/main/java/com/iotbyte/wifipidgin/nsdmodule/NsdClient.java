package com.iotbyte.wifipidgin.nsdmodule;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;

import com.iotbyte.wifipidgin.channel.Channel;
import com.iotbyte.wifipidgin.dao.DaoError;
import com.iotbyte.wifipidgin.dao.DaoFactory;
import com.iotbyte.wifipidgin.dao.FriendDao;
import com.iotbyte.wifipidgin.friend.Friend;
import com.iotbyte.wifipidgin.utils.Utils;
import com.iotbyte.wifipidgin.ui.tempDb;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class NsdClient {

    NsdManager.ResolveListener mResolveListener;
    NsdManager.DiscoveryListener mDiscoveryListener;

    public static final String TAG = "NsdClient";

    Context mContext;
    NsdManager mNsdManager;

    public static final String SERVICE_TYPE = "_http._tcp.";
    public String mServiceName = "WiFiPidginNsdService";

    NsdServiceInfo mService;


    private Queue<Friend> friendCreationQueue = new LinkedList<Friend>();

    private static NsdClient instance = null;

    private boolean isDiscovering = false;

    private NsdClient(Context context) {
        mContext = context;
        mNsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
        //nearbyFriendList =new ArrayList<Friend>();
        //Start message Server service and NSD Service
        Log.d(TAG, "Initializing NsdClient");

    }

    /**
     ** NsdClient is a singleton
     **/
    public static NsdClient getInstance(Context context) {
        if (instance == null){
            synchronized (NsdClient.class) {
                if (instance == null) {
                    instance = new NsdClient(context);
                }
            }
        }
        return instance;
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
            public void onServiceFound(final NsdServiceInfo service) {

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
                            mService = serviceInfo;
                            String serviceName = serviceInfo.getServiceName();
                            if (!serviceName.startsWith(mServiceName + "-")) {
                                Log.d(TAG, "Unknown App");
                                return;
                            }

                            InetAddress host = mService.getHost();
                            Log.d(TAG, "The friend's ip is " + host.getHostAddress());
                            Log.d(TAG, "The ip for my current device is " + Utils.getIPAddress(true));
                            if (host.getHostAddress().equals(Utils.getIPAddress(true))) {
                                Log.d(TAG, "Same IP.");
                                return;
                            }

                            // Extract mac address from service name
                            // Format is serviceName-macAddress
                            Log.d(TAG, "Service name is " + serviceName);
                            int macStart = mServiceName.length() + 1;
                            int macEnd = macStart + 17;
                            String macString = serviceName.substring(macStart, macEnd);
                            Log.d(TAG, "The friend's mac address is " + macString);

                            Friend newFriend = new Friend(Utils.hexStringToByteArray(macString.replaceAll(":", "")), host, 55);
                            newFriend.setIp(host);
                            FriendDao fd = DaoFactory.getInstance()
                                    .getFriendDao(mContext, DaoFactory.DaoType.SQLITE_DAO, null);

                            //Try to see if the friend is already being created.
                            if ( false == isInFriendCreationQueue(newFriend) ){
                                //If not, check if the friend has already been created
                                if (null == fd.findByMacAddress(newFriend.getMac())){
                                    //Now, put the friend into the creation queue.
                                    enqueueFriendCreationQueue(newFriend);
                                }
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

                //Friend newFriend = new Friend(host, Utils.hexStringToByteArray("ABCD"));
                //mdb.addFriendToList(newFriend);
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

    //TODO -- change public to private after testing
    public boolean enqueueFriendCreationQueue(Friend inFriend) {
        Log.d(TAG, "Adding Friend " + inFriend.getMac().toString());

        return friendCreationQueue.offer(inFriend);
    }

    protected Friend dequeueFriendCreationQueue() {
        return friendCreationQueue.poll();
    }

    protected Friend topFriendCreationQueue() {
        return friendCreationQueue.peek();
    }
    /**
     *
     * @param inFriend
     * @return a boolean value that indicates if the friend is in
     * the Friend Creation Queue
     */
    protected boolean isInFriendCreationQueue(Friend inFriend){

        for (Friend friend : friendCreationQueue) {
            if (friend.getMac().equals(inFriend.getMac())){
                return true;
            }
        }
        return false;
    }

    protected boolean isEmptyFriendCreationQueue(){
        if (friendCreationQueue.size() == 0){
            return true;
        }
        return false;
    }

    //Returns if currently the discovery is on going.
    public boolean getIsDiscovering(){
        return isDiscovering;
    }
}
