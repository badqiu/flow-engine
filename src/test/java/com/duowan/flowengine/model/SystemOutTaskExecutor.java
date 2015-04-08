package com.duowan.flowengine.model;

import com.duowan.flowengine.engine.TaskExecutor;

public class SystemOutTaskExecutor implements TaskExecutor {

	@Override
	public void exec(FlowTask task, FlowContext flowContext) throws InterruptedException {
		System.out.println(task.getTaskCode());
		Thread.sleep(1000 * 3);
	}

}
