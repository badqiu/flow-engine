package com.github.flowengine.util.loadbalance;

import java.util.HashMap;
import java.util.Map;


/**
 * 负载均衡的实例健康检查,不健康的实例将会移除
 * 
 * @author badqiu
 *
 */
public class LoadBalanceHealthCheck {
	
	private LoadBalancer loadBalancer;
	
	public LoadBalanceHealthCheck(){
	}
	
	public LoadBalanceHealthCheck(LoadBalancer loadBalancer) {
		super();
		this.loadBalancer = loadBalancer;
	}

	public LoadBalancer getLoadBalancer() {
		return loadBalancer;
	}

	public void setLoadBalancer(LoadBalancer loadBalancer) {
		this.loadBalancer = loadBalancer;
	}

	public void checkHealth() {
		checkHealth(new HashMap<String,Object>(loadBalancer.getInstanceMap()));
		checkHealth(new HashMap<String,Object>(loadBalancer.getInvalidInstanceMap()));
	}

	private void checkHealth(Map<String, Object> instanceMap) {
		for(String key : instanceMap.keySet()) {
			Object instance = instanceMap.get(key);
			boolean health = checkHealth(instance);
			if(health) {
				loadBalancer.validInstance(key);
			}else {
				loadBalancer.invalidInstance(key);
			}
		}
	}
	
	private boolean checkHealth(Object instance) {
		if(instance instanceof  HealthCheck) {
			return ((HealthCheck)instance).isHealth();
		}else {
			throw new RuntimeException("instance must be implements HealthCheck interface,instance:"+instance);
		}
	}
	
}
