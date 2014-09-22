package com.duowan.flowengine.engine.task;

import java.util.Map;

import com.duowan.flowengine.engine.FlowEngine;
import com.duowan.flowengine.engine.TaskExecutor;
import com.duowan.flowengine.model.FlowTask;

public class SSHTaskExecutor implements TaskExecutor{

	@Override
	public void exec(FlowTask task, Map params, FlowEngine engine)
			throws Exception {
		String username = (String)task.getProps().get("username");
		String password = (String)task.getProps().get("password");
		String cmd = task.getProgram();
		
	}

}
