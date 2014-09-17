package com.duowan.flowengine.model;

import com.duowan.flowengine.model.def.FlowDef;

/**
 * 流程实例,一个流程包括多个任务(FlowTask)
 * 
 * @author badqiu
 *
 */
public class Flow extends FlowDef<FlowTask>{
	private String instanceId; //实例ID
	private String status; //任务状态: 可运行,运行中,阻塞(睡眠,等待),停止
	private int execResult; //执行结果: 0成功,非0为失败

	public Flow() {
	}
	
	public Flow(String flowCode,String instanceId) {
		super();
		this.instanceId = instanceId;
		setFlowCode(flowCode);
	}
	
	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getExecResult() {
		return execResult;
	}

	public void setExecResult(int execResult) {
		this.execResult = execResult;
	}

	
}
