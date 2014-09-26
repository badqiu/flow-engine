package com.duowan.flowengine.stream.bolt;

import java.util.List;

import com.duowan.flowengine.stream.OutputCollector;
import com.duowan.flowengine.stream.TopologyContext;

/**
 * 流处理任务的: 水龙头
 * @author badqiu
 *
 */
public interface Bolt <T>{

	public void process(List<T> objects) throws Exception;
	
}
