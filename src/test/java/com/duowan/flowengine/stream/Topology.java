package com.duowan.flowengine.stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.duowan.flowengine.graph.Graph;

/**
 * 代表一张任务拓扑图
 * 
 * @author badqiu
 *
 */
public class Topology extends Graph<TopologyNode>{

	private static Logger log = LoggerFactory.getLogger(Topology.class);
	public void build() {
		
	}
	
	@Override
	public void init() {
		super.init();
	}
	
	public void start(final TopologyContext context) throws InstantiationException, IllegalAccessException {
		initWithContext(context);
		for(final TopologyNode node : getNoDependNodes()) {
			context.getExecutorService().execute(new Runnable() {
				@Override
				public void run() {
					try {
						node.startSpout();
					} catch (Exception e) {
						log.error("error on start",e);
					}
				}
			});
		}
	}

	private void initWithContext(final TopologyContext context)
			throws InstantiationException, IllegalAccessException {
		for(final TopologyNode node : getNodes()) {
			node.setContext(context);
		}
		for(final TopologyNode node : getNodes()) {
			node.init();
		}
	}
}
