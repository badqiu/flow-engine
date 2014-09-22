package com.duowan.flowengine.stream;

import java.util.ArrayList;
import java.util.List;

import com.duowan.flowengine.stream.bolt.Bolt;
import com.duowan.flowengine.stream.bolt.BufferedBolt;
/**
 * 
 * @author badqiu
 *
 */
public class BoltProcessRouter {
	
	private List<BufferedBolt> bufs = new ArrayList<BufferedBolt>();
	
	public BoltProcessRouter(Bolt business,int bufInstanceNum,int bufSize,int bufInterval,TopologyContext context) {
		for(int i = 0; i < bufInstanceNum; i++) {
			BufferedBolt task = new BufferedBolt(business,bufSize,bufInterval);
			bufs.add(task);
			context.getExecutorService().execute(task.getAutoFlushTask());
		}
	}

	public void process(Object hashKey,Object obj) {
		int index = Math.abs(hashKey.hashCode() % bufs.size());
		bufs.get(index).process(hashKey,obj);
	}
	
	public void flush() throws Exception {
		for(BufferedBolt b : bufs) {
			b.flush();
		}
	}
	
}
