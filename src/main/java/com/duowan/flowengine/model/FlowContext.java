package com.duowan.flowengine.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import com.duowan.flowengine.engine.FlowEngine;

/**
 * 流程执行的上下文
 * 
 * @author badqiu
 * 
 */
public class FlowContext {

	private transient ExecutorService executorService;
	private transient FlowEngine flowEngine;
	private Map params; // 流程参数
	private Flow flow; // 流程
	private List<String> visitedTaskCodes = new ArrayList<String>(); //已经访问过的流程任务节点
	
	public ExecutorService getExecutorService() {
		return executorService;
	}

	public void setExecutorService(ExecutorService executorService) {
		this.executorService = executorService;
	}

	public Map getParams() {
		return params;
	}

	public void setParams(Map params) {
		this.params = params;
	}
	
	public Flow getFlow() {
		return flow;
	}

	public void setFlow(Flow flow) {
		this.flow = flow;
	}

	public List<String> getVisitedTaskCodes() {
		return visitedTaskCodes;
	}

	public void setVisitedTaskCodes(List<String> visitedTaskCodes) {
		this.visitedTaskCodes = visitedTaskCodes;
	}

	public FlowEngine getFlowEngine() {
		return flowEngine;
	}

	public void setFlowEngine(FlowEngine flowEngine) {
		this.flowEngine = flowEngine;
	}
	
}
