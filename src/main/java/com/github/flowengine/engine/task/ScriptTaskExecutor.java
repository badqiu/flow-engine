package com.github.flowengine.engine.task;

import org.springframework.util.Assert;

import com.github.flowengine.engine.TaskExecResult;
import com.github.flowengine.engine.TaskExecutor;
import com.github.flowengine.model.FlowContext;
import com.github.flowengine.model.FlowTask;
import com.github.rapid.common.util.ScriptEngineUtil;

public class ScriptTaskExecutor implements TaskExecutor {

	@Override
	public TaskExecResult exec(FlowTask task, FlowContext flowContext) throws Exception {
		String lang = (String)task.getProps().get("lang");
		Assert.hasText(lang,"'lang' props must be not empty");
		ScriptEngineUtil.eval(lang, task.getScript(), flowContext.getParams());
		return null;
	}

}
