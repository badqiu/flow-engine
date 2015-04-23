package com.github.flowengine.graph;

import static org.junit.Assert.*;

import java.util.Collection;

import org.junit.Test;
import org.springframework.util.Assert;

public class GraphTest {

	@Test
	public void test() {
		Graph<GraphNode> graph = new Graph<GraphNode>();
		graph.addNode(new GraphNode("start"));
		for(int i = 0; i < 100; i++) {
			GraphNode t = new GraphNode();
			t.setId("demo_"+(i % 10));
			t.setDepends("start,start");
			graph.addNode(t);
		}
		graph.init();
		
		System.out.println(graph.toString());
		System.out.println("------------------------------------------");
		
		Assert.notEmpty(graph.getEdges());
		assertEquals(10,graph.getEdges().size());
		assertEquals(10,graph.getNode("start").getChilds().size());
		
		System.out.println(graph.toString());
		System.out.println("------------------------------------------");
		
		printNodes(graph.getNoDependNodes(),"");
		for(GraphNode n : graph.getNoDependNodes()) {
			printNodes(n.getChilds(),"\t");
		}
		
	}

	private void printNodes(Collection<GraphNode> nodes,String prefix) {
		for(GraphNode n : nodes) {
			System.out.println(prefix+n.getId());
		}
	}
}
