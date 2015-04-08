package com.duowan.flowengine.engine.task;

import com.duowan.flowengine.engine.TaskExecutor;
import com.duowan.flowengine.model.FlowContext;
import com.duowan.flowengine.model.FlowTask;

public class HttpTaskExecutor implements TaskExecutor {

	@Override
	public void exec(FlowTask task, FlowContext flowContext)
			throws Exception {
		String url = task.getProgram();
		
	}

}
