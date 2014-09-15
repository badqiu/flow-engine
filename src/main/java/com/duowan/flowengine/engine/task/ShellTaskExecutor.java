package com.duowan.flowengine.engine.task;

import java.io.InputStream;
import java.util.Map;

import com.duowan.flowengine.engine.AsyncTaskExecutor;
import com.duowan.flowengine.model.FlowTask;

public class ShellTaskExecutor implements AsyncTaskExecutor{

	@Override
	public void exec(FlowTask task, Map params) {
		
	}

	@Override
	public void kill(FlowTask task, Map params) {
		
	}

	@Override
	public boolean isRunning(FlowTask task, Map params) {
		return false;
	}

	@Override
	public InputStream getLog(FlowTask task, Map params) {
		return null;
	}

	@Override
	public int getExitCode(FlowTask task, Map params) {
		return 0;
	}

	@Override
	public void wait(FlowTask task, Map params) {
	}

	
}
