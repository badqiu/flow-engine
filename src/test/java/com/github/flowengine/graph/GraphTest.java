package com.github.flowengine.graph;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;
import org.springframework.util.Assert;

public class GraphTest {

	@Test
	public void testPrintValue() {
		Graph<GraphNode> graph = new Graph<GraphNode>();
		graph.addNode(new GraphNode("start",""));
		graph.addNode(newGraphNode("step1","start",10));
		graph.addNode(newGraphNode("step1-1","step1-1",20));
		graph.addNode(newGraphNode("step1-1-1","step1-1",30));
		graph.addNode(newGraphNode("step2","start",100));
		graph.addNode(newGraphNode("step2-1","step2",10));
		graph.init(true);
		
		printGraph(graph);
		
	}


	public static void printGraph(Graph<GraphNode> graph) {
		printNodes(0,graph.getNoDependNodes(),"");
		for(GraphNode n : graph.getNoDependNodes()) {
			printNodes(n.getValue(),n.getChilds(),"\t");
		}
	}


	private GraphNode newGraphNode(String id,String depends,int value) {
		GraphNode r = new GraphNode(id,depends);
		r.setValue(value);
		return r;
	}
	
	
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
		
		printGraph(graph);
		
	}

	public static void printNodes(int parentValue,Collection<GraphNode> nodes,String prefix) {
		@SuppressWarnings("unchecked")
		List<GraphNode> sortedNodes = new ArrayList(nodes);
		Collections.sort((List)sortedNodes,new Comparator() {
			public int compare(Object o1, Object o2) {
				GraphNode n1 = (GraphNode)o1;
				GraphNode n2 = (GraphNode)o2;
				return new Integer(n1.getValue()).compareTo(n2.getValue());
			}
		});
		for(GraphNode n : sortedNodes) {
			int graphSumValue = parentValue+n.getValue();
			System.out.println(prefix+n.getId()+",value="+(n.getValue())+" graphSumValue:"+graphSumValue);
			if(CollectionUtils.isNotEmpty(n.getChilds())) {
				printNodes(graphSumValue,n.getChilds(),"\t"+prefix);
			}
		}
	}
}
