package com.duowan.flowengine.stream.bolt;

import com.duowan.flowengine.stream.OutputCollector;
import com.duowan.flowengine.stream.TopologyContext;

public interface BoltConfig {

	public void init(OutputCollector collector,TopologyContext context);
	
}
