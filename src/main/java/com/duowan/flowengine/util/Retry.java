package com.duowan.flowengine.util;

import java.util.concurrent.Callable;

import org.springframework.util.Assert;

public class Retry {
	
	private int retryTimes;  // 重试次数
	private int retryInterval;// 重试间隔
//	private int failovers; //failover retry次数
//	private int timeout; //超时时间
	private Callable cmd;
	private int useRetryCount;
	private Exception exception;
	
	private boolean retryByIsNetworkException(Exception e) {
		return false;
	}
	
	private boolean shouldRetry(Exception e, int retries, int failovers) throws Exception {
		return false;
	}
	
	public Object exec() throws Exception {
		while(true) {
			try {
				Object result = cmd.call();
				return result;
			} catch (Exception e) {
				exception = e;
				if(useRetryCount > retryTimes) {
					break;
				}
				useRetryCount++;
				Assert.isTrue(retryInterval > 0 ,"retryInterval must be true");
				Thread.sleep(retryInterval);
			}
		}
		throw exception;
	}
}
