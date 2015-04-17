package com.github.flowengine.util.naming;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

import com.duowan.common.redis.RedisTemplate;

public class RedisNamingService implements NamingService{
	private static final String UNBIND = "unbind";
	private static final String BIND = "bind";

	private int expirePeriod = 5000;
	private RedisTemplate redis = null;
	
	private Map<String,Set<String>> bindMap = new HashMap<String,Set<String>>();
	private Map<String,List<NamingServiceListener>> listenerMap = new HashMap<String,List<NamingServiceListener>>();
	private Notify notify;
	
	public RedisNamingService(JedisPool jedisPool) {
		super();
		this.redis = new RedisTemplate(jedisPool);
	}
	
	public RedisNamingService(JedisPool jedisPool,int expirePeriod) {
		super();
		this.expirePeriod = expirePeriod;
		this.redis = new RedisTemplate(jedisPool);
	}

	private Set<String> getLocalBind(String name) {
		Set<String> urls = bindMap.get(name);
		if(urls == null) {
			urls = new HashSet<String>();
			bindMap.put(name, urls);
		}
		return urls;
	}
	
	@Override
	public void bind(String name, String url) {
		getLocalBind(name).add(url);
		long expire = System.currentTimeMillis() + expirePeriod;
		redis.hset(name, url, String.valueOf(expire));
		redis.publish(name, BIND);
	}

	@Override
	public void unbind(String name, String url) {
		getLocalBind(name).remove(url);
		redis.hdel(name, url);
		redis.publish(name, UNBIND);
	}

	@Override
	public synchronized void addListener(String name, NamingServiceListener listener) {
		List<NamingServiceListener> list = getListeners(name);
		list.add(listener);
		if(notify == null) {
			notify = new Notify();
			notify.start();
		}
	}

	private synchronized List<NamingServiceListener> getListeners(String name) {
		List<NamingServiceListener> list = listenerMap.get(name);
		if(list == null) {
			list = new ArrayList<NamingServiceListener>();
			listenerMap.put(name, list);
		}
		return list;
	}

	@Override
	public void removeListener(String name, NamingServiceListener listener) {
		List<NamingServiceListener> list = getListeners(name);
		list.remove(listener);
	}

	@Override
	public List<String> list(String name) {
		Map<String,String> map = redis.hgetAll(name);
		List<String> result = new ArrayList<String>();
		for(String url : map.keySet()) {
			result.add(url);
		}
		return result;
	}

	/**
	 * 通过心跳,增加过期时长
	 */
	public void heartbeat() {
		new Thread() {
			public void run() {
				try {
					Thread.sleep(expirePeriod - 500);
					
					for(String name : bindMap.keySet()) {
						for(String url : bindMap.get(name)) {
							bind(name,url);
						}
					}
					
				}catch(Throwable e) {
					//ignore
				}
			}
		};
	}
	
	private Map<String,List<String>> localListenerUrlCache = new HashMap<String,List<String>>();
	private void doNotify(String name) {
		for(NamingServiceListener l : getListeners(name)) {
			List<String> urls = list(name);
			List<String> cacheUrls = localListenerUrlCache.get(name);
			if(cacheUrls == null || !CollectionUtils.isEqualCollection(urls,cacheUrls)) {
				l.notify(urls);
			}
			localListenerUrlCache.put(name, urls);
		}
	}
	
	public class Notify extends Thread {
		
		public void run() {
			JedisPubSub jedisPubSub = new JedisPubSub(){
				@Override
				public void onMessage(String channel, String message) {
					if(BIND.equals(message)) {
						doNotify(channel);
					}else if(UNBIND.equals(message)) {
						doNotify(channel);
					}
				}
				
				@Override
				public void onPMessage(String pattern, String channel,String message) {
				}
				@Override
				public void onSubscribe(String channel, int subscribedChannels) {
				}
				@Override
				public void onUnsubscribe(String channel, int subscribedChannels) {
				}
				@Override
				public void onPUnsubscribe(String pattern,int subscribedChannels) {
				}
				@Override
				public void onPSubscribe(String pattern, int subscribedChannels) {
				}
			};
			
			String[] names = listenerMap.keySet().toArray(new String[0]);
			redis.subscribe(jedisPubSub,names );
		}
	}
}
