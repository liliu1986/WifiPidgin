package com.wifipidgin.nsdmodule;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.os.Handler;
import android.util.Log;

import com.wifipidgin.user.User;

import java.util.List;

public class NsdWrapper {
	
	Context mContext;
	NsdManager mNsdManager;
    NsdServer mNsdServer;
    NsdClient mNsdClient;
    
    private static final String TAG = "NsdWrapper";
    
    private Handler mUpdateHandler;
    
	public NsdWrapper(Context context){
    	mContext = context;
    	Log.d(TAG, "Gets Here 1" + Context.NSD_SERVICE);
    	mNsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
    	Log.d(TAG, "Gets Here 2");
    	Log.d(TAG, "IP address of this device is: " + Utils.getIPAddress(true));
    	
        mNsdServer= new NsdServer(mContext, mUpdateHandler);
        mNsdClient= new NsdClient(mContext, mNsdManager);
        mNsdServer.initializeNsdServer();
        mNsdClient.initializeNsdClient();
        
        //The user appears to be visible
        
        this.discover();

    }

    public void Broadcast(){
        //Make yourself visible to the other users within the network. 
        mNsdServer.broadcastService();
        
    }
    public void discover(){
        //Looking for other users
    	if(mNsdClient.getIsDiscovering()==false){
    		//Only start discovery when the discovery is not on
    		Log.d(TAG, "Start looking for visible user around!");
        	mNsdClient.discoverServices();
    	}
        
    }
    
    public void stopDiscover(){
        //Stop looking for other users
    	Log.d(TAG, "Stop looking for visible user around!");
        mNsdClient.stopDiscovery();
        
    }
    public List<User> getNearbyUserList(){
    	return mNsdClient.getNearbyUserList();
    }
    
    //Clearing up the registered services.
    public void tearDown(){
    	mNsdServer.tearDown();
    }
}
