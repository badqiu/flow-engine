package com.duowan.flowengine.model;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.duowan.flowengine.model.def.Edge;
import com.duowan.flowengine.model.def.FlowDef;

/**
 * 流程实例,一个流程包括多个任务(FlowTask)
 * 
 * @author badqiu
 *
 */
public class Flow extends FlowDef{
	private String instanceId;
	private String status; //任务状态: 可运行,运行中,阻塞(睡眠,等待),停止
	private int execResult; //执行结果: 0成功,非0为失败

	private Set<FlowTask> tasks = new HashSet<FlowTask>();
	private Set<Edge> edges = new HashSet<Edge>();
	
	public Flow(String flowCode,String instanceId) {
		super();
		this.instanceId = instanceId;
		setFlowCode(flowCode);
	}
	
	public Set<FlowTask> getTasks() {
		return tasks;
	}

	public void setTasks(Set<FlowTask> tasks) {
		this.tasks = tasks;
	}

	public Set<Edge> getEdges() {
		return edges;
	}

	public void setEdges(Set<Edge> edges) {
		this.edges = edges;
	}

	public FlowTask getTask(String taskCode) {
		for(FlowTask t : tasks) {
			if(t.getTaskCode().equals(taskCode)) {
				return t;
			}
		}
		return null;
	}
	
	public FlowTask getRequiredTask(String taskCode) {
		FlowTask t  = getTask(taskCode);
		if(t == null) {
			throw new RuntimeException("not found flow task by taskCode:"+taskCode+" on "+this);
		}
		return t;
	}
	
	public void addFlowTask(FlowTask t) {
		t.setFlowCode(getFlowCode());
		addDepends(t.getTaskCode(), t.getDepends());
		tasks.add(t);
	}
	
	/**
	 * 增加任务的依赖
	 * @param taskCode
	 * @param dependsTaskCode 可以用逗号分隔依赖
	 */
	public void addDepends(String taskCode,String dependsTaskCode) {
		if(StringUtils.isBlank(dependsTaskCode)) {
			return;
		}
		
		String[] dependsArray = dependsTaskCode.split("[\\s,]+");
		for(String depend : dependsArray) {
			if(StringUtils.isNotBlank(depend)) {
				Edge edge = new Edge();
				edge.setBeginTaskCode(depend);
				edge.setEndTaskCode(taskCode);
				addEdge(edge);
			}
		}
	}
	
	/**
	 * 增加任务的依赖的边
	 */
	public void addEdge(Edge edge) {
		if(edges.contains(edge)) {
			return;
		}
		
		FlowTask begin = getRequiredTask(edge.getBeginTaskCode());
		FlowTask end = getRequiredTask(edge.getEndTaskCode());
		begin.addChild(end);
		end.addParent(begin);
		
		edge.setFlowCode(getFlowCode());
		edges.add(edge);
	}
	
}
