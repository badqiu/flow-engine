package com.duowan.flowengine.stream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.Assert;

/**
 * 用于收集bolt的数据
 * @author badqiu
 *
 */
public class OutputCollector {
	private static final Integer DEFAULT_HASH_KEY = 1;

	/**
	 * Map<stream,BoltProcessRouter>
	 */
	private Map<String,List<BoltProcessRouter>> boltProcessRouters = new HashMap<String,List<BoltProcessRouter>>();

	public void addBoltProcessRouter(String stream,BoltProcessRouter router) {
		List<BoltProcessRouter> list = this.boltProcessRouters.get(stream);
		if(list == null) {
			list = new ArrayList<BoltProcessRouter>();
			this.boltProcessRouters.put(stream, list);
		}
		list.add(router);
	}

	/**
	 * 数据发送至下一个bolt处理,使用默认流名称发送: default,默认hashKey
	 * 
	 * @param obj 要emit的数据
	 */
	public void emit(Object obj) {
		emit("default",DEFAULT_HASH_KEY,obj);
	}
	
	/**
	 * 数据发送至下一个bolt处理,使用默认流名称发送: default
	 * 
	 * @param hashKey 根据hashCode决定将数据分发到那个线程的Bolt处理
	 * @param obj 要emit的数据
	 */
	public void emit(Object hashKey,Object obj) {
		emit("default",hashKey,obj);
	}

	/**
	 * 将数据发送至下一个bolt处理
	 * 
	 * hashKey应用场景: 如对userId排重,即可以使用userId作为hashKey,这样将可以把相同hash的userId,分发至同一个线程处理
	 * 
	 * @param stream 需要传递的后续流
	 * @param hashKey 根据hashCode决定将数据分发到那个线程的Bolt处理
	 * @param obj 要emit的数据
	 */
	public void emit(String stream,Object hashKey,Object obj) {
		List<BoltProcessRouter> routers = getBoltProcessRouter(stream);
		if(routers == null) {
			return;
		}
		Assert.notNull(obj,"obj must be not null");
		for(BoltProcessRouter router : routers) {
			router.process(hashKey, obj);
		}
	}

	private List<BoltProcessRouter> getBoltProcessRouter(String stream) {
		return boltProcessRouters.get(stream);
	}
	
}
