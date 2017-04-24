package com.github.flowengine.engine;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.util.Assert;

import com.github.flowengine.engine.task.CmdTaskExecutor;
import com.github.flowengine.engine.task.GroovyTaskExecutor;
import com.github.flowengine.engine.task.HttpTaskExecutor;
import com.github.flowengine.engine.task.JavaScriptTaskExecutor;
import com.github.flowengine.engine.task.NothingTaskExecutor;
import com.github.flowengine.engine.task.ScriptTaskExecutor;
import com.github.flowengine.engine.task.ShellTaskExecutor;
import com.github.flowengine.engine.task.SubFlowTaskExecutor;
import com.github.flowengine.model.Flow;
import com.github.flowengine.model.FlowContext;
import com.github.flowengine.model.FlowTask;

public class FlowEngine {

	private Set<Flow> flows = new HashSet<Flow> ();
	private Map<String,TaskExecutor> taskExecutorShortNames = new HashMap<String,TaskExecutor>();
	{
		registerTaskExecutor("subflow",new SubFlowTaskExecutor());
		registerTaskExecutor("cmd",new CmdTaskExecutor());
		registerTaskExecutor("http",new HttpTaskExecutor());
		registerTaskExecutor("shell",new ShellTaskExecutor());
		registerTaskExecutor("nothing",new NothingTaskExecutor());
		
		registerTaskExecutor("groovy",new GroovyTaskExecutor());
		registerTaskExecutor("script",new ScriptTaskExecutor());
		registerTaskExecutor("javascript",new JavaScriptTaskExecutor());
	}
	
	public void registerTaskExecutor(String shortName,TaskExecutor executor) {
		taskExecutorShortNames.put(shortName,executor);
	}
	
	public TaskExecutor getTaskExecutor(String shortName) {
		return taskExecutorShortNames.get(shortName);
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
	
	/* exec by startTaskId */
	public FlowContext exec(String flowId,String startTaskId,Map params) {
		Flow flow = getRequiredFlow(flowId);
		return exec(flow,startTaskId, params);
	}
	
	/* exec by startTaskId */
	public FlowContext exec(Flow flow,String startTaskId, Map params) {
		Assert.isTrue(flow.getMaxParallel() > 0,"flow.getMaxParallel() > 0 must be true");
		FlowContext context = newFlowContext(params, flow);
		return exec(flow, startTaskId, context);
	}
	
	/* exec by startTaskId */
	public FlowContext exec(Flow flow, String startTaskId, FlowContext context) {
		FlowTask task = flow.getNode(startTaskId);
		Assert.notNull(task,"not found by taskId:"+startTaskId);
		task.exec(context,true);
		return context;
	}
	
	public FlowContext exec(Flow flow, List<FlowTask> tasks, FlowContext context) {
		FlowTask.execAll(context, true, tasks, true);
		return context;
	}

	private FlowContext newFlowContext(Map params, Flow flow) {
		FlowContext context = new FlowContext();
		context.setParams(params);
		Integer maxParallel = flow.getMaxParallel();
		Assert.isTrue(maxParallel > 0,"maxParallel > 0 must be true");
		ExecutorService es = Executors.newFixedThreadPool(maxParallel,new CustomizableThreadFactory("flow-"+flow.getFlowId()+"-"));
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

	public Flow getFlow(String flowId) {
		for(Flow f : flows) {
			if(f.getFlowId().equals(flowId)) {
				return f;
			}
		}
		return null;
	}
	
	public Flow getRequiredFlow(String flowId) {
		Flow flow = getFlow(flowId);
		if(flow == null) {
			throw new RuntimeException("not found Flow by flowId:"+flowId);
		}
		return flow;
	}
	
}
