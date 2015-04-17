package com.github.flowengine.engine;

import java.io.InputStream;
import java.util.Map;

import com.github.flowengine.model.FlowContext;
import com.github.flowengine.model.FlowTask;

public interface AsyncTaskExecutor {

	public void exec(FlowTask task,Map params);
	
	public void kill(FlowTask task,Map params);
	
	public boolean isRunning(FlowTask task,Map params);
	
	public InputStream getLog(FlowTask task,Map params);
	
	/**
	 * 得到执行结果,0代表正常,非0代表异常
	 * @param context
	 * @return
	 */
	public int getExitCode(FlowTask task,Map params);
	
	/**
	 * 等待任务执行完成
	 * @param task
	 * @param params
	 */
	public void wait(FlowTask task,Map params);
	
}
