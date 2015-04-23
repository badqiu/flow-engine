package com.github.flowengine.engine.task;

import com.github.flowengine.engine.TaskExecutor;
import com.github.flowengine.model.FlowContext;
import com.github.flowengine.model.FlowTask;

public class CmdTaskExecutor implements TaskExecutor{

	@Override
	public void exec(FlowTask task, FlowContext flowContext) throws Exception {
		Process process = Runtime.getRuntime().exec(task.getScript());
		process.getInputStream();
		process.getErrorStream();
		process.waitFor();
		int exitValue = process.exitValue();
		if(exitValue == 0) {
			return;
		}else {
			throw new RuntimeException("error exit value:"+exitValue+" by script:"+task.getScript());
		}
	}

}
