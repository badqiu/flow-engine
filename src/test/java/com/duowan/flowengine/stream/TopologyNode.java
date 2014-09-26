package com.duowan.flowengine.stream;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.duowan.flowengine.graph.GraphNode;
import com.duowan.flowengine.stream.bolt.Bolt;
import com.duowan.flowengine.stream.bolt.BoltConfig;
import com.duowan.flowengine.stream.bolt.BufferedBolt;

/**
 * 任务拓扑图的某个任务节点
 * 
 * @author badqiu
 *
 */
public class TopologyNode extends GraphNode<TopologyNode> implements Bolt{

	private static final int DEFAULT_THREAD_NUM = 1;

	private static Logger log = LoggerFactory.getLogger(TopologyNode.class);
	
	private Class<? extends Bolt> blotClass;
	private String stream = "default";
	private int threadNum = DEFAULT_THREAD_NUM;
	private int bufSize = BufferedBolt.DEFAULT_BUF_SIZE;
	private int bufInterval = BufferedBolt.DEFAULT_BUF_INTERVAL;
	private Bolt business;
	private boolean isGlobalUnique; //是否多台机器唯一的实例
	
	private TopologyContext context = null;
	private OutputCollector outputCollector = null;
	private transient boolean spoutRunning = false; // 是否的当作输入spout
	private boolean isInitBolt = false;
	public Class<? extends Bolt> getBlotClass() {
		return blotClass;
	}

	public void setBlotClass(Class<? extends Bolt> blotClass) {
		this.blotClass = blotClass;
	}

	public int getThreadNum() {
		return threadNum;
	}

	public void setThreadNum(int taskNum) {
		this.threadNum = taskNum;
	}

	public String getStream() {
		return stream;
	}

	public void setStream(String stream) {
		this.stream = stream;
	}

	public void setContext(TopologyContext context) {
		this.context = context;
	}
	
	public Bolt getBusiness() {
		return business;
	}

	public void setBusiness(Bolt business) {
		this.business = business;
	}

	public void setBufSize(int bufSize) {
		this.bufSize = bufSize;
	}

	public void setBufInterval(int bufInterval) {
		this.bufInterval = bufInterval;
	}

	public void setGlobalUnique(boolean isGlobalUnique) {
		this.isGlobalUnique = isGlobalUnique;
	}

	public void setOutputCollector(OutputCollector outputCollector) {
		this.outputCollector = outputCollector;
	}

	public void init() throws InstantiationException, IllegalAccessException {
		Assert.notNull(context,"context must be not null");
		Assert.isTrue(threadNum > 0,"taskNum > 0 must true");
		Assert.notNull(initBusinessBolt());
	}
	
	private synchronized Bolt initBusinessBolt() throws InstantiationException, IllegalAccessException {
		if(business == null) {
			business = buildBolt();
		}
		if(!isInitBolt) {
			isInitBolt = true;
			initBolt(business);
		}
		return business;
	}
	
	private Bolt buildBolt() throws InstantiationException,IllegalAccessException {
		Bolt business = blotClass.newInstance();
		return business;
	}

	private Bolt initBolt(Bolt business) throws InstantiationException,
			IllegalAccessException {
		if(business instanceof BoltConfig) {
			outputCollector = newOutputCollector();
	
			Assert.notNull(context,"context must be not null ,on id:"+getGraphNodeId());
			((BoltConfig)business).init(outputCollector, context);
		}
		return business;
	}

	private OutputCollector newOutputCollector() throws InstantiationException,
			IllegalAccessException {
		OutputCollector outputCollector = new OutputCollector();
		for(TopologyNode childNode : getChilds()) {
			childNode.initBusinessBolt();
			BoltProcessRouter router = new BoltProcessRouter(childNode.getGraphNodeId(),childNode.stream,childNode,childNode.threadNum,bufSize,bufInterval,context);
			outputCollector.addBoltProcessRouter(childNode.stream, router);
		}
		return outputCollector;
	}

	public void startSpout()  {
		spoutRunning = true;
		while(spoutRunning) {
			try {
				business.process(null);
			}catch(Exception e) {
				log.error("error on startSpout",e);
			}
		}
	}

	@Override
	public void process(List objects) throws Exception {
		business.process(objects);
	}

}
