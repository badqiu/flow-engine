package com.duowan.flowengine.util.loadbalance;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class SocketHealthCheck implements HealthCheck{

	private String host;
	private int port;
	
	/**
	 * 心跳间隔
	 */
	private int heartbeatInterval = 3000;
	private int connectTimeout = 2000;
	private boolean keepAlive = true;
	private Socket socket;
	private boolean health = false;
	
	public SocketHealthCheck(String host, int port) {
		this.host = host;
		this.port = port;
	}
	
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getHeartbeatInterval() {
		return heartbeatInterval;
	}

	public void setHeartbeatInterval(int heartbeatInterval) {
		this.heartbeatInterval = heartbeatInterval;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public boolean isKeepAlive() {
		return keepAlive;
	}

	public void setKeepAlive(boolean keepAlive) {
		this.keepAlive = keepAlive;
	}

	private Socket newSocket() throws SocketException, IOException {
		Socket socket = new Socket(); 
        socket.setKeepAlive(keepAlive);  
        socket.setSoTimeout(100);
        socket.connect(new InetSocketAddress(host,port), connectTimeout);
		return socket;
	}

	@Override
	public boolean isHealth() {
		health = isHealth0();
		return health;
	}
	
	private boolean isHealth0() {
		try {
			if(socket == null) {
				socket = newSocket();
			}
			try {
	    		socket.sendUrgentData(0xFF); // 发送心跳包
	    	}catch(SocketException e) {
	    		socket.close();
	    		socket = newSocket();
	    		socket.sendUrgentData(0xFF); // 发送心跳包
	    	}
			return true;
		}catch(Exception e) {
			return false;
		}
	}
	
	
	public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
		SocketHealthCheck shc = new SocketHealthCheck("www.163.com",80);
		shc.isHealth();
	}
	
}
