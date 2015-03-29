package com.iotbyte.wifipidgin.friend;

import java.net.InetAddress;

public class Friend {
	
	private InetAddress host;
	private String shost;
	public Friend(InetAddress inhost) {
		host=inhost;
    }
    public Friend(String inhost) {
        shost=inhost;
    }
	public String getUserIP(){
		return host.getHostAddress();
	}
    public String getsIP(){
        return shost;
    }

    @Override
    public String toString() {
        return host.toString();
    }
}
