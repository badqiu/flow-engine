package com.duowan.flowengine.engine.task;

import com.duowan.flowengine.engine.TaskExecutor;
import com.duowan.flowengine.model.FlowContext;
import com.duowan.flowengine.model.FlowTask;
/**
 * 不做任何事情的TaskExecutor
 * 
 * @author badqiu
 *
 */
public class NothingTaskExecutor implements TaskExecutor{

	@Override
	public void exec(FlowTask task, FlowContext flowContext) {
	}

}
