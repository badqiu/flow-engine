package com.github.flowengine.engine.task;

import com.github.flowengine.engine.TaskExecResult;
import com.github.flowengine.engine.TaskExecutor;
import com.github.flowengine.model.FlowContext;
import com.github.flowengine.model.FlowTask;

public class SSHTaskExecutor implements TaskExecutor{

	@Override
	public TaskExecResult exec(FlowTask task, FlowContext flowContext)
			throws Exception {
		String username = (String)task.getProps().get("username");
		String password = (String)task.getProps().get("password");
		String cmd = task.getScript();
		return null;
	}

}
