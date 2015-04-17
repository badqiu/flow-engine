package com.github.flowengine.util.loadbalance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.remoting.support.RemoteAccessor;

import com.github.flowengine.util.naming.NamingServiceListener;

public class LoadBalanceFactoryBean extends RemoteAccessor implements FactoryBean,NamingServiceListener{
	private int timeout;
	private int retryTimes;
	private boolean check; //是否检查service是否有发布,无发布会报错
	private LoadBalancerInterceptor loadBalancerInterceptor = new LoadBalancerInterceptor();
	private Object proxy;
	private List<String> serviceUrls;
	
	public boolean isEnableHealthCheck() {
		return loadBalancerInterceptor.isEnableHealthCheck();
	}

	public void setEnableHealthCheck(boolean enableHealthCheck) {
		loadBalancerInterceptor.setEnableHealthCheck(enableHealthCheck);
	}

	public int getCheckInterval() {
		return loadBalancerInterceptor.getCheckInterval();
	}

	public void setCheckInterval(int checkInterval) {
		loadBalancerInterceptor.setCheckInterval(checkInterval);
	}

	public String getLbType() {
		return loadBalancerInterceptor.getLbType();
	}

	public void setLbType(String lbType) {
		loadBalancerInterceptor.setLbType(lbType);
	}
	
	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public int getRetryTimes() {
		return retryTimes;
	}

	public void setRetryTimes(int retryTimes) {
		this.retryTimes = retryTimes;
	}

	public boolean isCheck() {
		return check;
	}

	public void setCheck(boolean check) {
		this.check = check;
	}

	public List<String> getServiceUrls() {
		return serviceUrls;
	}

	public void setServiceUrls(List<String> serviceUrls) {
		this.serviceUrls = serviceUrls;
	}

	public LoadBalancerInterceptor getLoadBalancerInterceptor() {
		return loadBalancerInterceptor;
	}

	public List<String> getServiceUrls(Class serviceInterface) {
		return serviceUrls;
	}
	
	public void updateServiceUrls(List<String> serviceUrls) {
		Map<String,Object> map = getInstanceMap(getServiceInterface(),serviceUrls);
		loadBalancerInterceptor.setInstanceMap(map);
	}
	
	public Object serviceReference(Class serviceInterface,int timeout,int retryTimes,boolean check) {
		List<String> urls = getServiceUrls(serviceInterface);
		Map<String, Object> instanceMap = getInstanceMap(serviceInterface,urls);
		return createLoadBalancerProxy(serviceInterface, instanceMap);
	}

	private Object createLoadBalancerProxy(Class serviceInterface,Map<String, Object> instanceMap) {
		ProxyFactory pf = new ProxyFactory();
		pf.setTargetClass(serviceInterface);
		loadBalancerInterceptor.setInstanceMap(instanceMap);
		pf.addAdvice(loadBalancerInterceptor);
		return pf.getProxy();
	}

	private Map<String, Object> getInstanceMap(Class serviceInterface,List<String> serivceUrls) {
		Map<String,Object> instanceMap = new HashMap<String,Object>();
		for(String serviceUrl : serivceUrls) {
			Object instance = createObject(serviceInterface, serviceUrl);
			instanceMap.put(serviceUrl, instance);
		};
		return instanceMap;
	}

	protected Object createObject(Class serviceInterface, String serviceUrl) {
//		HttpInvokerProxyFactoryBean f = new  HttpInvokerProxyFactoryBean();
//		f.setServiceUrl(serviceUrl);
//		f.setServiceInterface(serviceInterface);
//		f.afterPropertiesSet();
//		Object instance = f.getObject();
		return null;
	}

	@Override
	public synchronized Object getObject() throws Exception {
		if(proxy == null) {
			proxy = serviceReference(getServiceInterface(),timeout,retryTimes,check);
		}
		return proxy;
	}

	@Override
	public Class getObjectType() {
		return getServiceInterface();
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	public void notify(List<String> urls) {
		updateServiceUrls(urls);
	}


}