package com.github.flowengine.util;

import org.junit.Test;

import com.github.rapid.common.util.ThreadUtil;

public class JVMLockUtilTest {

	@Test
	public void test() throws InterruptedException {
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				JVMLockUtil.lock("badqiu_lock");
				System.out.println("get lock on thread");
				ThreadUtil.sleep(1000 * 10);
				JVMLockUtil.unlock("badqiu_lock");
				System.out.println("Thread END");
			}
		}).start();
		
		ThreadUtil.sleep(1000);
		
		JVMLockUtil.lock("badqiu_lock");
		System.out.println("Main END");
	}

}
