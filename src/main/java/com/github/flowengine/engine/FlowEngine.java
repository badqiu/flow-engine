package com.github.flowengine.engine;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.util.Assert;

import com.github.flowengine.model.Flow;
import com.github.flowengine.model.FlowContext;
import com.github.flowengine.model.FlowTask;

public class FlowEngine {

	private Set<Flow> flows = new HashSet<Flow> ();
	
	public FlowContext exec(String flowCode,String startTaskCode,Map params) {
		Flow flow = getRequiredFlow(flowCode);
		return exec(flow,startTaskCode, params);
	}

	public FlowContext exec(Flow flow,List<FlowTask> tasks, Map params) {
		Assert.isTrue(flow.getMaxParallel() > 0,"flow.getMaxParallel() > 0 must be true");
		FlowContext context = newFlowContext(params, flow);
		exec(flow, tasks, context);
		return context;
	}
	
	public FlowContext exec(Flow flow, Map params) {
		return exec(flow,flow.getNoDependNodes(),params);
	}
	
	public FlowContext exec(Flow flow,String startTaskCode, Map params) {
		Assert.isTrue(flow.getMaxParallel() > 0,"flow.getMaxParallel() > 0 must be true");
		FlowContext context = newFlowContext(params, flow);
		return exec(flow, startTaskCode, context);
	}

	public FlowContext exec(Flow flow, String startTaskCode, FlowContext context) {
		FlowTask task = flow.getNode(startTaskCode);
		Assert.notNull(task,"not found task by code:"+startTaskCode);
		task.exec(context,false,true);
		return context;
	}
	
	public FlowContext exec(Flow flow, List<FlowTask> tasks, FlowContext context) {
		FlowTask.execAll(context, false, true, tasks, true);
		return context;
	}

	private FlowContext newFlowContext(Map params, Flow flow) {
		FlowContext context = new FlowContext();
		context.setParams(params);
		ExecutorService es = Executors.newFixedThreadPool(flow.getMaxParallel());
		context.setExecutorService(es);
		context.setFlow(flow);
		context.setFlowEngine(this);
		return context;
	}
	
	public void addFlow(Flow flow) {
		flows.add(flow);
	}
	
	public Set<Flow> getFlows() {
		return flows;
	}

	public void setFlows(Set<Flow> flows) {
		this.flows = flows;
	}

	public Flow getFlow(String flowCode) {
		for(Flow f : flows) {
			if(f.getFlowId().equals(flowCode)) {
				return f;
			}
		}
		return null;
	}
	
	public Flow getRequiredFlow(String flowCode) {
		Flow flow = getFlow(flowCode);
		if(flow == null) {
			throw new RuntimeException("not found flow by code:"+flowCode);
		}
		return flow;
	}
	
}
