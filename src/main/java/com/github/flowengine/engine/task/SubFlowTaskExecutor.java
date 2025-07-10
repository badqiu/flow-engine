package com.github.flowengine.engine.task;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.github.flowengine.engine.TaskExecResult;
import com.github.flowengine.engine.TaskExecutor;
import com.github.flowengine.model.Flow;
import com.github.flowengine.model.FlowContext;
import com.github.flowengine.model.FlowTask;

public class SubFlowTaskExecutor implements TaskExecutor{
	private static Logger logger = LoggerFactory.getLogger(SubFlowTaskExecutor.class);
	
	@Override
	public TaskExecResult exec(FlowTask task, FlowContext flowContext) throws InterruptedException {
		Assert.notNull(task.getScript(),"script must be not blank");
		
		String[] flowIds = StringUtils.trim(task.getScript()).split(",");
		for(String flowId : flowIds) {
			executeOneFlowId(flowContext, flowId);
		}
		return null;
	}

	private void executeOneFlowId(FlowContext flowContext, String flowId) throws InterruptedException {
		if(StringUtils.isBlank(flowId)) {
			return;
		}
		
		
		flowId = StringUtils.trim(flowId);
		logger.info("start exec sub flow:"+flowId);
		
		Flow flow = flowContext.getFlowEngine().getRequiredFlow(flowId);
		long start = System.currentTimeMillis();
		FlowContext subFlowContext = flowContext.getFlowEngine().exec(flow, flowContext.getParams());
		subFlowContext.awaitTermination();
		long cost = System.currentTimeMillis() - start;
		logger.info("executed sub flow:"+flowId+" cost seconds:"+(cost/1000));
	}

}
