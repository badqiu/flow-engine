package com.github.flowengine.util;

import static org.junit.Assert.*;

import org.apache.hadoop.util.ThreadUtil;
import org.junit.Test;

public class FileLockUtilTest {

	@Test
	public void test() throws Exception {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					FileLockUtil.lock("badqiu_lock");
					System.out.println("get lock on thread");
					Thread.sleep(1000 * 15);
					
					System.out.println("Thread END");
					FileLockUtil.unlock("badqiu_lock");
				}catch(Exception e) {
					throw new RuntimeException(e);
				}
			}
		}).start();
		
		Thread.sleep(1000);
		
		System.out.println("Main START");
		FileLockUtil.lock("badqiu_lock");
		System.out.println("Main END");
		
		Thread.sleep(100);
		
	}
	

}
