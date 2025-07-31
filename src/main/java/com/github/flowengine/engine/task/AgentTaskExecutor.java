package com.github.flowengine.engine.task;

import java.util.Map;

import com.github.flowengine.engine.TaskExecResult;
import com.github.flowengine.engine.TaskExecutor;
import com.github.flowengine.model.FlowContext;
import com.github.flowengine.model.FlowTask;

/**
 * 通过agent远程执行的TaskExecutor
 * 
 * @author badqiu
 *
 */
public class AgentTaskExecutor implements TaskExecutor{

	@Override
	public TaskExecResult exec(FlowTask task, FlowContext flowContext) throws Exception {
		String agentUrl = task.getExecAgent();
		Agent agent = new Agent(agentUrl);
		agent.exec(task, flowContext.getParams());
		return null;
	}

	public static class Agent{
		private String url;
		
		public Agent(String agentUrl) {
		}

		public void exec(FlowTask task, Map params) {
			
		}
		
	}
}
