package com.github.flowengine.engine.task;

import com.github.flowengine.engine.TaskExecResult;
import com.github.flowengine.engine.TaskExecutor;
import com.github.flowengine.model.FlowContext;
import com.github.flowengine.model.FlowTask;
import com.github.rapid.common.util.ScriptEngineUtil;

public class GroovyTaskExecutor implements TaskExecutor {

	@Override
	public TaskExecResult exec(FlowTask task, FlowContext flowContext) throws Exception {
		ScriptEngineUtil.eval("groovy", task.getScript(), flowContext.getParams());
		return null;
	}

}
