package com.github.flowengine.engine;

import com.github.flowengine.model.FlowContext;
import com.github.flowengine.model.FlowTask;
/**
 * 任务执行器
 * 
 * @author badqiu
 *
 */
public interface TaskExecutor {

	/**
	 * 任务执行器
	 * @param task 当前任务
	 * @param flowContext 任务上下文
	 * @return 执行结果，允许返回null
	 * @throws Exception 
	 */
	public TaskExecResult exec(FlowTask task, FlowContext flowContext) throws Exception;
	
}
