package com.github.flowengine.engine.task;

import org.apache.commons.lang.StringUtils;

import com.github.flowengine.engine.TaskExecutor;
import com.github.flowengine.model.Flow;
import com.github.flowengine.model.FlowContext;
import com.github.flowengine.model.FlowTask;

public class SubFlowTaskExecutor implements TaskExecutor{

	@Override
	public void exec(FlowTask task, FlowContext flowContext) {
		String flowCode = StringUtils.trim(task.getProgram());
		Flow flow = flowContext.getFlowEngine().getRequiredFlow(flowCode);
		flowContext.getFlowEngine().exec(flow, flowContext.getParams());
	}

}
