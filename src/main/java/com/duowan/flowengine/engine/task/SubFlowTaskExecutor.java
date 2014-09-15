package com.duowan.flowengine.engine.task;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.duowan.flowengine.engine.FlowEngine;
import com.duowan.flowengine.engine.TaskExecutor;
import com.duowan.flowengine.model.FlowTask;

public class SubFlowTaskExecutor implements TaskExecutor{

	@Override
	public void exec(FlowTask task, Map params,FlowEngine engine) {
		String flowCode = StringUtils.trim(task.getProgram());
		engine.exec(flowCode, params);
	}

}
