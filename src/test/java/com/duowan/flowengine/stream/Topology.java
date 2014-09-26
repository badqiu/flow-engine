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
	
	private String id;
	private String author;
	private String remarks;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
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

	public void initWithContext(final TopologyContext context)
			throws InstantiationException, IllegalAccessException {
		for(final TopologyNode node : getNodes()) {
			node.setContext(context);
		}
		for(final TopologyNode node : getNodes()) {
			node.init();
		}
	}
}
