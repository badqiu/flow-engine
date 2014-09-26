package com.duowan.flowengine.util.loadbalance;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.security.auth.Destroyable;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.SystemUtils;
import org.springframework.beans.factory.DisposableBean;



/**
 * 负载均衡工具类
 * 
 * 支持负载均衡算法:
 *  host_hash: 根据当前主机地址,持续的绑定在某个实例上
 *  round_robin: 轮循算法,按顺序调用每个实例
 *  
 * 其它功能: 
 *  实现实现FailFast快速失败
 *  实现FailOver
 * @author badqiu
 *
 */
public class LoadBalancer {

	private static long MAX_INDEX = Long.MAX_VALUE - Integer.MAX_VALUE;
	
	static private String host = null;
	private String lbType = null; //host_hash,加权轮询（weighted round robin）
	private long index = 0;
	
	private Map<String,Object> instanceMap = new LinkedHashMap<String,Object>();
	private Map<String, Object> invalidInstanceMap = new LinkedHashMap<String,Object>();
	
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
	
	public Map<String, Object> getInvalidInstanceMap() {
		return Collections.unmodifiableMap(invalidInstanceMap);
	}

	public void addInstance(String key,Object instance) {
		instanceMap.put(key, instance);
		updateInstanceList();
	}
	
	public void removeInstance(String key) {
		Object obj = instanceMap.remove(key);
		if(obj != null && obj instanceof DisposableBean ) {
			try {
				((DisposableBean)obj).destroy();
			} catch (Exception e) {
				throw new RuntimeException("destory obj fail,obj:"+obj,e);
			}
		}
		updateInstanceList();
	}
	
	public void invalidInstance(String key) {
		Object obj = instanceMap.remove(key);
		invalidInstanceMap.put(key, obj);
		updateInstanceList();
	}

	private void updateInstanceList() {
		instances = new ArrayList(instanceMap.values());
	}
	
	public Object getInstance() {
		if(CollectionUtils.isEmpty(instances)) {
			throw new RuntimeException("not found any instance");
		}
		
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

	public void validInstance(String key) {
		if(instanceMap.containsKey(key)) {
			return;
		}
		Object instance = invalidInstanceMap.remove(key);
		addInstance(key, instance);
	}
	
}
