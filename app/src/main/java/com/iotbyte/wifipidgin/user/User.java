package com.iotbyte.wifipidgin.user;

import java.net.InetAddress;

public class User {
	
	private InetAddress host;
    private String mUserName;


    public User(String userName) {
        mUserName = userName;
    }

	public User(InetAddress inhost) {
		host = inhost;
    }
	
	public String getUserIP(){
		return host.getHostAddress();
	}

    public String getUserName(){
        return mUserName ;
    }
}
