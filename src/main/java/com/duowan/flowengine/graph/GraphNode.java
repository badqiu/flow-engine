package com.duowan.flowengine.graph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * 代表图的某个节点
 * 
 * @author badqiu
 * 
 */
public class GraphNode<T extends GraphNode> implements Serializable {

	private static final long serialVersionUID = 5097996371381799833L;
	
	/**
	 * 节点ID
	 */
	private String graphNodeId;
	/**
	 * 节点依赖的节点
	 */
	private String depends;

	/**
	 * 根据depends,计算得到,当前Node所有的孩子
	 */
	private List<T> childs = new ArrayList<T>();
	/**
	 * 根据depends,计算得到,当前Node所有的父亲
	 */
	private List<T> parents = new ArrayList<T>();

	public GraphNode() {
	}
	
	public GraphNode(String graphNodeId) {
		super();
		this.graphNodeId = graphNodeId;
	}
	
	public GraphNode(String graphNodeId, String depends) {
		super();
		this.graphNodeId = graphNodeId;
		this.depends = depends;
	}

	public String getDepends() {
		return depends;
	}

	public void setDepends(String depends) {
		this.depends = depends;
	}

	public String getGraphNodeId() {
		return graphNodeId;
	}

	public void setGraphNodeId(String id) {
		this.graphNodeId = id;
	}

	public List<T> getChilds() {
		return childs;
	}

	public void setChilds(List<T> childs) {
		this.childs = childs;
	}

	public List<T> getParents() {
		return parents;
	}

	public void setParents(List<T> parents) {
		this.parents = parents;
	}

	public void addChild(T child) {
		if(!childs.contains(child))
			childs.add(child);
	}

	public void addParent(T parent) {
		if(!parents.contains(parent))
			parents.add(parent);
	}
	
	public String dump(int tabsCount) {
		StringBuilder sb = new StringBuilder();
		sb.append(StringUtils.repeat("\t", tabsCount)+getGraphNodeId());
		for(T n : getChilds()) {
			sb.append("\n"+n.dump(tabsCount+1));
		}
		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getGraphNodeId() == null) ? 0 : getGraphNodeId().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GraphNode other = (GraphNode) obj;
		if (getGraphNodeId() == null) {
			if (other.getGraphNodeId() != null)
				return false;
		} else if (!getGraphNodeId().equals(other.getGraphNodeId()))
			return false;
		return true;
	}
	
}
