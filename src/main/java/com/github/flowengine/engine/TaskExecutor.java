package com.github.flowengine.engine;

import com.github.flowengine.model.FlowContext;
import com.github.flowengine.model.FlowTask;

public interface TaskExecutor {

	public TaskExecResult exec(FlowTask task, FlowContext flowContext) throws Exception;
	
}
