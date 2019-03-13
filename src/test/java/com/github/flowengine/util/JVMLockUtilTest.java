package com.github.flowengine.util;

import org.apache.hadoop.util.ThreadUtil;
import org.junit.Test;

public class JVMLockUtilTest {

	@Test
	public void test() throws InterruptedException {
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				JVMLockUtil.lock("badqiu_lock");
				System.out.println("get lock on thread");
				ThreadUtil.sleepAtLeastIgnoreInterrupts(1000 * 10);
				JVMLockUtil.unlock("badqiu_lock");
			}
		}).start();
		
		ThreadUtil.sleepAtLeastIgnoreInterrupts(1000);
		
		JVMLockUtil.lock("badqiu_lock");
		System.out.println("END");
	}

}
