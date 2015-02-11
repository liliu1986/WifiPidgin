package com.iotbyte.wifipidgin.user;

import java.net.InetAddress;

public class User {
	
	private InetAddress host;
	
	public User(InetAddress inhost) {
		host=inhost;
    }
	
	public String getUserIP(){
		return host.getHostAddress();
	}
}
