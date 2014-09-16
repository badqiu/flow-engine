package com.duowan.flowengine.engine;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.util.Assert;

import com.duowan.flowengine.model.Flow;
import com.duowan.flowengine.model.FlowContext;
import com.duowan.flowengine.model.FlowTask;

public class FlowEngine {

	public static String DEFAULT_START_TASK_CODE = "start";
	
	private Set<Flow> flows = new HashSet<Flow> ();
	
	public void exec(String flowCode,Map params) {
		exec(flowCode,DEFAULT_START_TASK_CODE,params);
	}
	
	public void exec(String flowCode,String startTaskCode,Map params) {
		Flow flow = getRequiredFlow(flowCode,params);
		exec(flow,startTaskCode, params);
	}

	public void exec(Flow flow, Map params) {
		exec(flow, DEFAULT_START_TASK_CODE, params);
	}
	
	public void exec(Flow flow,List<FlowTask> tasks, Map params) {
		Assert.isTrue(flow.getMaxParallel() > 0,"flow.getMaxParallel() > 0 must be true");
		FlowContext context = newFlowContext(params, flow);
		FlowTask.execAll(context, false, true, tasks);
	}
	
	public void exec(Flow flow,String startTaskCode, Map params) {
		Assert.isTrue(flow.getMaxParallel() > 0,"flow.getMaxParallel() > 0 must be true");
		FlowContext context = newFlowContext(params, flow);
		exec(flow, startTaskCode, context);
	}

	public void exec(Flow flow, String startTaskCode, FlowContext context) {
		FlowTask task = flow.getTask(startTaskCode);
		task.exec(context,false,true);
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

	private Flow getFlow(String flowCode,Map params) {
		for(Flow f : flows) {
			if(f.getFlowCode().equals(flowCode)) {
				return f;
			}
		}
		return null;
	}
	
	private Flow getRequiredFlow(String flowCode,Map params) {
		Flow flow = getFlow(flowCode,params);
		if(flow == null) {
			throw new RuntimeException("not found flow by code:"+flowCode);
		}
		return flow;
	}
	
}
