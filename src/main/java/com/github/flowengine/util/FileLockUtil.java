package com.github.flowengine.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileLock;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.util.Assert;

public class FileLockUtil {
	static Map<String,FileLockHolder> locks = new ConcurrentHashMap<String,FileLockHolder>();

	static class FileLockHolder {
		FileLock fileLock;
		Lock lock = new ReentrantLock();
		
		public FileLockHolder(FileLock fileLock) {
			this.fileLock = fileLock;
		}
		
		public void lock() {
			lock.lock();
		}
		
		public void unlock() throws IOException {
			lock.unlock();
			fileLock.release();
		}
	}
	
	public static void lock(String lockId) throws Exception {
		lock("default",lockId);
	}
	
	public static void lock(String lockGroup,String lockId) throws Exception {
		FileLockHolder lock = getLock(lockGroup, lockId,true);
		lock.lock();
	}

	public static void unlock(String lockId) throws Exception {
		unlock("default",lockId);
	}
	
	public static void unlock(String lockGroup,String lockId) throws Exception {
		FileLockHolder lock = getLock(lockGroup, lockId,false);
		if(lock != null) {
			lock.unlock();
		}
		locks.remove(getLockKey(lockGroup, lockId));
	}
	
	public static synchronized FileLockHolder getLock(String lockGroup,String lockId,boolean createLock) throws Exception {
		Assert.hasText(lockGroup,"lockGroup must be not empty");
		Assert.hasText(lockId,"lockId must be not empty");
		
		String lockKey = getLockKey(lockGroup, lockId);
		FileLockHolder fileLockHolder = locks.get(lockKey);
		if(fileLockHolder == null && createLock) {
			File file = new File(System.getProperty("java.io.tmpdir"),lockKey+".lock");
			FileOutputStream output = new FileOutputStream(file);
			FileLock fileLock = null;
			while(fileLock == null) {
				fileLock = output.getChannel().lock();
			}
			
			fileLockHolder = new FileLockHolder(fileLock);
			locks.put(lockKey, fileLockHolder);
		}
		
		return fileLockHolder;
	}

	private static String getLockKey(String lockGroup, String lockId) {
		String lockKey = lockGroup+"@"+lockId;
		return lockKey;
	}
}
