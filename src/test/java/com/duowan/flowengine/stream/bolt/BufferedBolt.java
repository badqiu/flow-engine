package com.duowan.flowengine.stream.bolt;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.duowan.flowengine.stream.OutputCollector;
import com.duowan.flowengine.stream.TopologyContext;
import com.duowan.flowengine.util.SystemTimer;

/**
 * 缓冲bolt的输入,定时定长的flush数据
 * 
 * @author badqiu
 *
 */
public class BufferedBolt implements Bolt{
	private static Logger log = LoggerFactory.getLogger(BufferedBolt.class);
	public static final int DEFAULT_BUF_SIZE = 10000;
	public static final int DEFAULT_BUF_INTERVAL = 500;
	
	private Bolt proxy;
	private int bufSize = DEFAULT_BUF_SIZE;
	private int bufInterval = DEFAULT_BUF_INTERVAL;
	private long lastFlushTime = 0;
	private List<Object> buf = new ArrayList<Object>();
	
	private BufferedBolt lock = this;
	
	public BufferedBolt(Bolt proxy) {
		this(proxy,DEFAULT_BUF_SIZE,DEFAULT_BUF_INTERVAL);
	}
	
	public BufferedBolt(Bolt proxy,int bufSize,int bufInterval) {
		this.proxy = proxy;
		this.bufSize = bufSize;
		this.bufInterval = bufInterval;
	}

	@Override
	public void init(OutputCollector collector, TopologyContext context) {
	}
	
	@Override
	public void process(List objects) throws Exception {
//		buf.addAll(objects);
//		notifyIfNeed();
	}
	
	public void process(Object hashKey,Object obj) {
		if(obj == null) 
			return;
		buf.add(obj);
		notifyIfNeed();
	}

	private void notifyIfNeed() {
		long interval = SystemTimer.currentTimeMillis() - lastFlushTime;
		if(buf.size() > bufSize || interval > bufInterval) {
			synchronized (lock) {
				lock.notifyAll();
			}
		}
	}

	public Runnable getAutoFlushTask() {
		return new Runnable() {
			@Override
			public void run() {
				while(true) {
					try {
						synchronized (lock) {
							lock.wait(bufInterval);
						}
						flush();
					}catch(Exception e) {
						log.error("auto flush error",e);
					}
				}
			}
		};
	}

	public void flush() throws Exception {
		lastFlushTime = SystemTimer.currentTimeMillis();
		if(buf == null || buf.isEmpty()) {
			return;
		}
		final List<Object> tempBuf = buf;
		buf = new ArrayList<Object>(bufSize);
		proxy.process(tempBuf);
	}
	
	
	public void startAutoFlushTask() {
		Thread t = new Thread(getAutoFlushTask());
		t.setName("flush_bufferd_bolt");
		t.start();
	}

}


