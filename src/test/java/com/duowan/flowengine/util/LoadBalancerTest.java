package com.duowan.flowengine.util;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.aop.framework.ProxyFactory;

public class LoadBalancerTest {

	@Test
	public void test_round_robin() {
		
		LBTestBean target = newLBTestProxy("round_robin");
		
		for(int i = 0; i < 20; i++) {
			assertEquals(i % 10,target.print());
		}
	}
	
	@Test
	public void test_host_hash() {
		
		LBTestBean target = newLBTestProxy("host_hash");
		int firstValue = -1;
		for(int i = 0; i < 20; i++) {
			if(i == 0) {
				firstValue = target.print();
			}
			assertEquals(firstValue,target.print());
		}
	}

	private LBTestBean newLBTestProxy(String lbType) {
		ProxyFactory p = new ProxyFactory();
		LoadBalancerInterceptor advice = new LoadBalancerInterceptor();
		for(int i = 0; i < 10; i++) {
			advice.addInstance(""+i, new LBTestBean(i));
		}
		advice.setLbType(lbType);
		p.addAdvice(advice);
		p.setTarget(new LBTestBean(-100000000));
		LBTestBean target = (LBTestBean)p.getProxy();
		return target;
	}

	

}
