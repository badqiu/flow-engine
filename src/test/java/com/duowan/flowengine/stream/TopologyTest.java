package com.duowan.flowengine.stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Test;

import com.duowan.flowengine.stream.bolt.BasicBolt;
import com.duowan.flowengine.stream.bolt.Bolt;
public class TopologyTest {

	@Test
	public void test() throws InterruptedException, InstantiationException, IllegalAccessException {
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				e.printStackTrace();
			}
		});
		
		Topology t = new Topology();
		
		TopologyNode input_spout = new TopologyNode();
		input_spout.setGraphNodeId("input_spout");
		input_spout.setDepends("");
		input_spout.setBlotClass(RandomSpout.class);
		t.addNode(input_spout);
		
		
		for(int i = 0; i < 1; i++) {
			TopologyNode n = new TopologyNode();
			n.setGraphNodeId(""+i);
			n.setDepends("input_spout");
			n.setBlotClass(SystemBolt.class);
			t.addNode(n);
		}
		t.init();
//		t.initWithContext(newTopologyContext());
		System.out.println(TopologyXmlParser.toXml(t));
		for(int i = 0; i < 1; i++) {
			TopologyNode n = new TopologyNode();
			n.setGraphNodeId(""+i + 1000);
			n.setDepends(""+i);
			n.setBlotClass(SystemBolt.class);
			t.addNode(n);
		}
		
		t.init();
		
		TopologyContext context = newTopologyContext();
		t.start(context);
		
		Thread.sleep(1000 * 5);
		assertEquals(collect.size(),randomEmitTimes);
		
		
	}
	private TopologyContext newTopologyContext() {
		TopologyContext context = new TopologyContext();
		context.setExecutorService(Executors.newFixedThreadPool(3000));
		return context;
	}
	
	static AtomicLong sum = new AtomicLong();
	static long count = 0;
	static List collect = new ArrayList();
	public static class SystemBolt extends BasicBolt implements Bolt{

		@Override
		public void process(List objects) {
			assertFalse(objects.isEmpty());
			sum.addAndGet(objects.size());
			count++;
//			if(sum % 20 == 0) {
				System.out.println(Thread.currentThread()+" - sum+"+ sum+" count:"+count+" avg:"+(sum.longValue()/count));
//			}
			collect.addAll(objects);
		}
		
	}
	
	public static class CountBolt extends BasicBolt implements Bolt{
		private int count = 0;
		private String id = null;
		@Override
		public void process(List objects) {
			count += objects.size();
			System.out.println(id + " CountBolt:"+count);
		}
	}
	
	public static class SumBolt extends BasicBolt implements Bolt{
		private int sum = 0;
		private String id = null;
		@Override
		public void process(List objects) {
			for(Number n : (List<Number>)objects ) {
				sum += n.intValue();
			}
			System.out.println(id+" SumBolt:"+sum);
		}
	}
	
	static int randomEmitTimes = 10;
	
	public static class RandomSpout extends BasicBolt implements Bolt{
		@Override
		public void process(List objects) {
			for(int i = 0; i < randomEmitTimes; i++) {
				collector.emit(i,i);
			}
			
			try {
				Thread.sleep(1000);
				
				
				System.out.println(collect);
				assertEquals(collect.size(),randomEmitTimes);
				Thread.sleep(100000);
			} catch (InterruptedException e) {
				//ignore
			}
		}
		
	}

}
