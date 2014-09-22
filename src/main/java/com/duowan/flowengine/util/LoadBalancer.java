package com.duowan.flowengine.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;



/**
 * 负载均衡工具类
 * 
 * @author badqiu
 *
 */
public class LoadBalancer {

	private static long MAX_INDEX = Long.MAX_VALUE - Integer.MAX_VALUE;
	
	static private String host = null;
	private String lbType = null; //host_hash,加权轮询（weighted round robin）
	private long index = 0;
	
	private Map<String,Object> instanceMap = new LinkedHashMap<String,Object>();
	private List<Object> instances = null;
	
	static {
		try {
			host = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			host = ""+System.currentTimeMillis();
		}
	}
	
	public String getLbType() {
		return lbType;
	}

	public void setLbType(String lbType) {
		this.lbType = lbType;
	}

	public void setInstanceMap(Map<String, Object> instanceMap) {
		this.instanceMap = instanceMap;
		updateInstanceList();
	}
	
	public Map<String, Object> getInstanceMap() {
		return Collections.unmodifiableMap(instanceMap);
	}

	public void addInstance(String key,Object instance) {
		instanceMap.put(key, instance);
		updateInstanceList();
	}

	private void updateInstanceList() {
		instances = new ArrayList(instanceMap.values());
	}
	
	public Object getInstance() {
		if("host_hash".equals(lbType)) {
			int instanceIndex = Math.abs(host.hashCode() % instances.size());
			return instances.get(instanceIndex);
		}else if("round_robin".equals(lbType)) {
			if(index > MAX_INDEX) {
				index = 0;
			}
			int instanceIndex = (int)(index++ % instances.size());
			return instances.get(instanceIndex);
		}else {
			throw new RuntimeException("lbType is null,must be round_robin,host_hash");
		}
	}
	
}
