package com.duowan.flowengine.engine.task;

import com.duowan.flowengine.engine.TaskExecutor;
import com.duowan.flowengine.model.FlowContext;
import com.duowan.flowengine.model.FlowTask;

public class SSHTaskExecutor implements TaskExecutor{

	@Override
	public void exec(FlowTask task, FlowContext flowContext)
			throws Exception {
		String username = (String)task.getProps().get("username");
		String password = (String)task.getProps().get("password");
		String cmd = task.getProgram();
		
	}

}
