package com.github.flowengine.engine.task;

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

	
	@Override
	public void exec(FlowTask task, FlowContext flowContext) {
	}

}
