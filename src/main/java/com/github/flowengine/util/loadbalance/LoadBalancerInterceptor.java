package com.github.flowengine.util.loadbalance;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.InitializingBean;
/**
 * 负载均衡拦截器
 * @author Administrator
 *
 */
public class LoadBalancerInterceptor extends LoadBalancer implements MethodInterceptor,InitializingBean{

	private boolean enableHealthCheck = false;
	private int checkInterval = 5000;
	
	private LoadBalanceHealthCheck loadBalanceHealthCheck;
	
	public boolean isEnableHealthCheck() {
		return enableHealthCheck;
	}

	public void setEnableHealthCheck(boolean enableHealthCheck) {
		this.enableHealthCheck = enableHealthCheck;
	}

	public int getCheckInterval() {
		return checkInterval;
	}

	public void setCheckInterval(int checkInterval) {
		this.checkInterval = checkInterval;
	}

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		Object target = getInstance();
		Object result = invocation.getMethod().invoke(target, invocation.getArguments());
		return result;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if(enableHealthCheck) {
			loadBalanceHealthCheck = new LoadBalanceHealthCheck(this);
			new Thread(new Runnable() {
				@Override
				public void run() {
					while(true) {
						try {
							Thread.sleep(checkInterval);
							loadBalanceHealthCheck.checkHealth();
						}catch(Exception e) {
							e.printStackTrace();
						}
					}
				}
			},"loadBalanceHealthCheck").start();
		}
	}

}
