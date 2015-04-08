package com.duowan.flowengine.engine;

import com.duowan.flowengine.model.FlowContext;
import com.duowan.flowengine.model.FlowTask;

public interface TaskExecutor {

	public void exec(FlowTask task, FlowContext flowContext) throws Exception;
	
}
