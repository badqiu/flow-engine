package com.github.flowengine.engine.task;

import com.github.flowengine.engine.TaskExecutor;
import com.github.flowengine.model.FlowContext;
import com.github.flowengine.model.FlowTask;

public class HttpTaskExecutor implements TaskExecutor {

	@Override
	public void exec(FlowTask task, FlowContext flowContext)
			throws Exception {
		String url = task.getScript();
		
	}

}
