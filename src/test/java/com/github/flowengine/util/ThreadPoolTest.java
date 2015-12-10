package com.github.flowengine.util;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.junit.Test;

public class ThreadPoolTest {

	@Test
	public void test() throws Exception {
		Executor executor = Executors.newFixedThreadPool(1);
		
		for(int i = 0; i < 10; i++) {
			final int count = i;
			executor.execute(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(1000 * 5);
						System.out.println(Thread.currentThread().getName()+" i="+count);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
		}
		System.out.println("put all end");
		Thread.sleep(1000 * 50);
	}
}
