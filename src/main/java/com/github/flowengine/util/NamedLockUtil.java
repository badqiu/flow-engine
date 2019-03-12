package com.github.flowengine.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.util.Assert;


public class NamedLockUtil {

	static Map<String,Lock> locks = new ConcurrentHashMap<String,Lock>();
	
	public static void lock(String lockId) {
		lock("default",lockId);
	}
	
	public static void lock(String lockGroup,String lockId) {
		Lock lock = getLock(lockGroup, lockId);
		lock.lock();
	}

	public static void unlock(String lockId) {
		unlock("default",lockId);
	}
	
	public static void unlock(String lockGroup,String lockId) {
		Lock lock = getLock(lockGroup, lockId);
		lock.unlock();
		locks.remove(getLockKey(lockGroup, lockId));
	}
	
	public static synchronized Lock getLock(String lockGroup,String lockId) {
		Assert.hasText(lockGroup,"lockGroup must be not empty");
		Assert.hasText(lockId,"lockId must be not empty");
		
		String lockKey = getLockKey(lockGroup, lockId);
		Lock lock = locks.get(lockKey);
		if(lock == null) {
			lock = new ReentrantLock();
			locks.put(lockKey, lock);
		}
		return lock;
	}

	private static String getLockKey(String lockGroup, String lockId) {
		String lockKey = lockGroup+"@"+lockId;
		return lockKey;
	}

}
