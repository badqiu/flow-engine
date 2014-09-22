package com.duowan.flowengine.util;

import java.util.Map;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class LoadBalancerInterceptor implements MethodInterceptor{

	private LoadBalancer loadBalancer = new LoadBalancer();;
	
	public LoadBalancerInterceptor() {
	}
	
	public LoadBalancerInterceptor(LoadBalancer loadBalancer) {
		super();
		this.loadBalancer = loadBalancer;
	}

	public void setLoadBalancer(LoadBalancer loadBalancer) {
		this.loadBalancer = loadBalancer;
	}

	public String getLbType() {
		return loadBalancer.getLbType();
	}

	public void setLbType(String lbType) {
		loadBalancer.setLbType(lbType);
	}

	public void setInstanceMap(Map<String, Object> instanceMap) {
		loadBalancer.setInstanceMap(instanceMap);
	}

	public void addInstance(String key, Object instance) {
		loadBalancer.addInstance(key, instance);
	}

	public Object getInstance() {
		return loadBalancer.getInstance();
	}

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		Object target = loadBalancer.getInstance();
		Object result = invocation.getMethod().invoke(target, invocation.getArguments());
		return result;
	}

}
