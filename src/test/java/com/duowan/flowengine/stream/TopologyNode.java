package com.duowan.flowengine.stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.duowan.flowengine.graph.GraphNode;
import com.duowan.flowengine.stream.bolt.Bolt;
import com.duowan.flowengine.stream.bolt.BufferedBolt;

/**
 * 任务拓扑图的某个任务节点
 * 
 * @author badqiu
 *
 */
public class TopologyNode extends GraphNode<TopologyNode>{

	private static final int DEFAULT_THREAD_NUM = 1;

	private static Logger log = LoggerFactory.getLogger(TopologyNode.class);
	
	private String stream = "default";
	private Class<? extends Bolt> blotClass;
	/**
	 * 线程数,指定bolt通过多少个线程处理
	 */
	private int threadNum = DEFAULT_THREAD_NUM;
	private int bufSize = BufferedBolt.DEFAULT_BUF_SIZE;
	private int bufInterval = BufferedBolt.DEFAULT_BUF_INTERVAL;
	
	private TopologyContext context = null;
	private Bolt business;
	private OutputCollector outputCollector = null;
	private transient boolean spoutRunning = false;
	
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
	
	public void init() throws InstantiationException, IllegalAccessException {
		Assert.notNull(context,"context must be not null");
		Assert.notNull(blotClass,"blotClass must be not null");
		Assert.isTrue(threadNum > 0,"taskNum > 0 must true");
		Assert.notNull(getBusinessBolt());
	}
	
	private synchronized Bolt getBusinessBolt() throws InstantiationException, IllegalAccessException {
		if(business == null) {
			business = buildBolt();
		}
		return business;
	}
	
	private Bolt buildBolt() throws InstantiationException,IllegalAccessException {
		Bolt business = blotClass.newInstance();
		return initBolt(business);
	}

	private Bolt initBolt(Bolt business) throws InstantiationException,
			IllegalAccessException {
		outputCollector = new OutputCollector();
		for(TopologyNode childNode : getChilds()) {
			Bolt childBolt = childNode.getBusinessBolt();
			BoltProcessRouter router = new BoltProcessRouter(childBolt,childNode.threadNum,bufSize,bufInterval,context);
			
			outputCollector.addBoltProcessRouter(childNode.stream, router);
		}

		Assert.notNull(context,"context must be not null ,on id:"+getGraphNodeId());
		business.init(outputCollector, context);
		return business;
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
	
}
