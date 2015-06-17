package com.github.flowengine.engine.task;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.flowengine.engine.TaskExecResult;
import com.github.flowengine.engine.TaskExecutor;
import com.github.flowengine.model.Flow;
import com.github.flowengine.model.FlowContext;
import com.github.flowengine.model.FlowTask;

public class SubFlowTaskExecutor implements TaskExecutor{
	private static Logger logger = LoggerFactory.getLogger(SubFlowTaskExecutor.class);
	
	@Override
	public TaskExecResult exec(FlowTask task, FlowContext flowContext) throws InterruptedException {
		String flowId = StringUtils.trim(task.getScript());
		logger.info("start exec sub flow:"+flowId);
		
		Flow flow = flowContext.getFlowEngine().getRequiredFlow(flowId);
		long start = System.currentTimeMillis();
		FlowContext subFlowContext = flowContext.getFlowEngine().exec(flow, flowContext.getParams());
		subFlowContext.awaitTermination();
		logger.info("executed sub flow:"+flowId+" cost:"+(System.currentTimeMillis() - start));
		return null;
	}

}
