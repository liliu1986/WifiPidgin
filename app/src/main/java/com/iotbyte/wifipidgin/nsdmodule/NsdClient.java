package com.iotbyte.wifipidgin.nsdmodule;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;

import com.iotbyte.wifipidgin.dao.DaoFactory;
import com.iotbyte.wifipidgin.dao.FriendDao;
import com.iotbyte.wifipidgin.friend.Friend;
import com.iotbyte.wifipidgin.utils.Utils;

import java.net.InetAddress;

public class NsdClient {

    NsdManager.ResolveListener mResolveListener;
    NsdManager.DiscoveryListener mDiscoveryListener;

    public static final String TAG = "NsdClient";

    Context mContext;
    NsdManager mNsdManager;

    public static final String SERVICE_TYPE = "_http._tcp.";
    public String mServiceName = "WiFiPidginNsdService";

    NsdServiceInfo mService;

    private boolean isDiscovering = false;

    public NsdClient(Context context) {
        mContext = context;
        mNsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
        //nearbyFriendList =new ArrayList<Friend>();
        //Start message Server service and NSD Service
        Log.d(TAG, "Initializing NsdClient");

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
                            FriendCreationQueue friendCreationQueue  = FriendCreationQueue.getInstance();
                            if ( false == friendCreationQueue.isInFriendCreationQueue(newFriend) ){
                                //If not, check if the friend has already been created
                                if (null == fd.findByMacAddress(newFriend.getMac())){
                                    //Now, put the friend into the creation queue.
                                    friendCreationQueue.enqueueFriendCreationQueue(newFriend);
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



    //Returns if currently the discovery is on going.
    public boolean getIsDiscovering(){
        return isDiscovering;
    }
}
