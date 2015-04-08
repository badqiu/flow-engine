package com.duowan.flowengine.engine.task;

import org.apache.commons.lang.StringUtils;

import com.duowan.flowengine.engine.TaskExecutor;
import com.duowan.flowengine.model.Flow;
import com.duowan.flowengine.model.FlowContext;
import com.duowan.flowengine.model.FlowTask;

public class SubFlowTaskExecutor implements TaskExecutor{

	@Override
	public void exec(FlowTask task, FlowContext flowContext) {
		String flowCode = StringUtils.trim(task.getProgram());
		Flow flow = flowContext.getFlowEngine().getRequiredFlow(flowCode);
		flowContext.getFlowEngine().exec(flow, flowContext.getParams());
	}

}
