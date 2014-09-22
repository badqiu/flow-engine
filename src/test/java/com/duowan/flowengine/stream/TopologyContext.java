package com.duowan.flowengine.stream;

import java.util.Map;
import java.util.concurrent.ExecutorService;
/**
 * 拓
 * @author Administrator
 *
 */
public class TopologyContext {

	private Map params;
	private ExecutorService executorService = null;
	
	public ExecutorService getExecutorService() {
		return executorService;
	}

	public void setExecutorService(ExecutorService executorService) {
		this.executorService = executorService;
	}

	public Map getParams() {
		return params;
	}

	public void setParams(Map params) {
		this.params = params;
	}
	
}
