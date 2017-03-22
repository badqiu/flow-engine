package com.github.flowengine.model;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.github.flowengine.model.def.FlowDef;
import com.github.flowengine.util.Listener;
import com.github.flowengine.util.Listenerable;

/**
 * 流程实例,一个流程包括多个任务(FlowTask)
 * 
 * @author badqiu
 *
 */
public class Flow extends FlowDef<FlowTask>{
	private String instanceId; //实例ID
	private String status; //任务状态: 可运行,运行中,阻塞(睡眠,等待),停止
	private int execResult = 0; //执行结果: 0成功,非0为失败

	private Date startTime;
	private Date endTime;
	private StringBuffer log = new StringBuffer();
	
	private transient Listenerable<Flow> listenerable = new Listenerable<Flow>();
	
	private Map context = new HashMap();
	
	public Flow() {
	}
	
	public Flow(String flowId,String instanceId) {
		super();
		this.instanceId = instanceId;
		setFlowId(flowId);
	}
	
	/**
	 * 初始化图
	 */
	@Override
	public void init(boolean ignoreNotFoundDependsError) {
		super.init(ignoreNotFoundDependsError);
//		initUnFinishParents();
		initNodeDefaultValues();
	}

	private void initNodeDefaultValues() {
		for(FlowTask t : super.getNodes()) {
			if(getDefaultTimeout() > 0) {
				if(t.getTimeout() == null) {
					t.setTimeout(getDefaultTimeout());
				}
			}
			
			if(getDefaultRetryInterval() > 0) {
				if(t.getRetryInterval() == null) {
					t.setRetryInterval(getDefaultRetryInterval());
				}
			}
			
			if(getDefaultRetryTimes() > 0) {
				if(t.getRetryTimes() == null) {
					t.setRetryTimes(getDefaultRetryTimes());
				}
			}
			
			if(StringUtils.isNotBlank(getDefaultScriptType())) {
				if(t.getScriptType() == null) {
					t.setScriptType(getDefaultScriptType());
				}
			}
		}
	}

//	private void initUnFinishParents() {
//		for(FlowTask flowTask : super.getNodes()) {
//			if(CollectionUtils.isNotEmpty(flowTask.getParents())) {
//				Set<FlowTask> unFinishParents = new HashSet<FlowTask>(flowTask.getParents());
//				flowTask.setUnFinishParents(unFinishParents);
//			}
//		}
//	}
	
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
	

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public StringBuffer getLog() {
		return log;
	}

	public void setLog(StringBuffer log) {
		this.log = log;
	}
	
	public void addLog(String txt) {
		this.log.append(txt);
	}

	public void notifyListeners() {
		listenerable.notifyListeners(this, null);
	}

	public void addListener(Listener<Flow> t) {
		listenerable.addListener(t);
	}

	/**
	 * @return the context
	 */
	public Map getContext() {
		return context;
	}

	/**
	 * @param context the context to set
	 */
	public void setContext(Map context) {
		this.context = context;
	}
}
