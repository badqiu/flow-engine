package com.duowan.flowengine.model;

import java.util.Map;

import com.duowan.flowengine.engine.FlowEngine;
import com.duowan.flowengine.engine.TaskExecutor;
import com.duowan.flowengine.model.FlowTask;

public class SystemOutTaskExecutor implements TaskExecutor {

	@Override
	public void exec(FlowTask task, Map params, FlowEngine engine) throws InterruptedException {
		System.out.println(task.getTaskCode());
		Thread.sleep(1000 * 3);
	}

}
