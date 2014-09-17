package com.duowan.flowengine.graph;

import java.util.Collection;

import org.junit.Test;
import org.springframework.util.Assert;

public class GraphTest {

	@Test
	public void test() {
		Graph<GraphNode> graph = new Graph<GraphNode>();
		graph.addNode(new GraphNode("start"));
		for(int i = 0; i < 10; i++) {
			GraphNode t = new GraphNode();
			t.setGraphNodeId("demo_"+i);
			t.setDepends("start,start");
			graph.addNode(t);
		}
		graph.init();
		
		Assert.notEmpty(graph.getEdges());
		
		printNodes(graph.getNoDependNodes(),"");
		for(GraphNode n : graph.getNoDependNodes()) {
			printNodes(n.getChilds(),"\t");
		}
	}

	private void printNodes(Collection<GraphNode> nodes,String prefix) {
		for(GraphNode n : nodes) {
			System.out.println(prefix+n.getGraphNodeId());
		}
	}
}
