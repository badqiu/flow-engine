package com.duowan.flowengine.engine;

import java.util.Map;

import com.duowan.flowengine.model.FlowTask;

public interface TaskExecutor {

	public void exec(FlowTask task,Map params,FlowEngine engine);
	
}
