package com.github.flowengine.util;

import static org.junit.Assert.*;

import java.util.concurrent.Callable;

import org.junit.Test;

public class RetryTest {

	Callable<Object> errorCmd = new Callable<Object>() {
		@Override
		public Object call() throws Exception {
			execCount++;
			if(true) throw new IllegalAccessException();
			return null;
		}
	};
	
	int execCount = 0;
	@Test
	public void testTimes() {
		try {
			Retry.retry(2, 10,errorCmd);
		}catch(Exception e) {
			e.printStackTrace();
		}
		assertEquals(execCount,3);
	}
	
	@Test
	public void test_interval() {
		long start = System.currentTimeMillis();
		try {
			Retry.retry(2, 1000,errorCmd);
		}catch(Exception e) {
			e.printStackTrace();
		}
		long cost = System.currentTimeMillis() - start;
		assertTrue(cost > 2000 && cost < 2100);
	}
	
	@Test
	public void test_retry_timeout() {
		long start = System.currentTimeMillis();
		try {
			Retry.retry(1000, 1000,2800,errorCmd);
		}catch(Exception e) {
			e.printStackTrace();
		}
		long cost = System.currentTimeMillis() - start;
		assertTrue(cost > 2800 && cost < 3100);
	}
	
//	@Test
//	public void test() throws Exception {
//		try {
//			throwException();
//		}catch(Exception e) {
//			e.printStackTrace();
//			throw e;
//		}
//	}
//
//	private void throwException() throws Exception {
//		throw new Exception("error on throwException()");
//	}

}
