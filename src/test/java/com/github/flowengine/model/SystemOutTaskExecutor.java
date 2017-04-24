package com.github.flowengine.model;

import com.github.flowengine.engine.TaskExecResult;
import com.github.flowengine.engine.TaskExecutor;

public class SystemOutTaskExecutor implements TaskExecutor {

	public int execCount = 0;
	@Override
	public synchronized TaskExecResult exec(FlowTask task, FlowContext flowContext) throws InterruptedException {
		System.out.println(task.getTaskId());
		Thread.sleep(500);
		execCount++;
		return null;
	}

}
