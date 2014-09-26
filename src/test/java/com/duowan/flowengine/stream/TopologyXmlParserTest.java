package com.duowan.flowengine.stream;

import java.io.IOException;
import java.util.concurrent.Executors;

import org.junit.Test;
import org.springframework.util.ResourceUtils;

import com.duowan.flowengine.stream.TopologyTest.RandomSpout;

public class TopologyXmlParserTest {

	@Test
	public void test() throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException, InterruptedException {
		TopologyXmlParser xml = new TopologyXmlParser(ResourceUtils.getFile("classpath:topogoty.xml"));
		
		Topology topology = xml.build();
		System.out.println(topology);
		
		TopologyContext context = new TopologyContext();
		context.setExecutorService(Executors.newFixedThreadPool(300));
		topology.start(context);
		
		Thread.sleep(1000 * 5);
	}
	
	
	@Test
	public void toXml() throws ClassNotFoundException, IOException {
		Topology t = new Topology();
		TopologyNode node = new TopologyNode();
		node.setBusiness(new RandomSpout());
		t.addNode(node);
		System.out.println(TopologyXmlParser.toXml(t));
	}

}
