package com.duowan.flowengine.stream.bolt;

import org.springframework.util.Assert;

import com.duowan.flowengine.stream.OutputCollector;
import com.duowan.flowengine.stream.TopologyContext;

public abstract class BasicBolt implements Bolt{

	protected TopologyContext context;
	protected OutputCollector collector;
	
	@Override
	public void init(OutputCollector collector, TopologyContext context) {
		Assert.notNull(collector,"collector must no null");
		Assert.notNull(context,"context must no null");
		this.collector = collector;
		this.context = context;
	}

}
