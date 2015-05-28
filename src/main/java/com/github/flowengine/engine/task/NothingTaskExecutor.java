package com.github.flowengine.engine.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.flowengine.engine.TaskExecResult;
import com.github.flowengine.engine.TaskExecutor;
import com.github.flowengine.model.FlowContext;
import com.github.flowengine.model.FlowTask;
/**
 * 不做任何事情的TaskExecutor
 * 
 * @author badqiu
 *
 */
public class NothingTaskExecutor implements TaskExecutor{

	private static Logger logger = LoggerFactory.getLogger(NothingTaskExecutor.class);
	@Override
	public TaskExecResult exec(FlowTask task, FlowContext flowContext) {
		logger.info("do no thing,taskId:"+task.getId()+" script:"+task.getScript());
		return null;
	}

}
