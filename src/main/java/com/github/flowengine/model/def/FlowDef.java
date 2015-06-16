package com.github.flowengine.model.def;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.github.flowengine.graph.Graph;
import com.github.flowengine.graph.GraphNode;

/**
 * 流程 定义
 * 
 * @author badqiu
 *
 */
public class FlowDef <T extends GraphNode> extends Graph<T> implements Serializable,Cloneable{
	private static final long serialVersionUID = 1L;
	
	private String flowId; //流程代码
	private String flowName; //名称
	private String remarks; //备注
	/**
	 * flow是否激活
	 */
	private boolean enabled = true;
	/**
	 * 最大并行度 db_column: max_parallel
	 */
	private int maxParallel;
	/**
	 * 附加属性
	 */
	private Map props = new HashMap();; 
	/**
	 * cron表达式
	 */
	private String cron;
	/**
	 * 创建时间
	 */
	private Date createdTime;
	/**
	 * 创建人
	 */
	private Date creator;
	/**
	 * 最后操作人
	 */
	private Date operator;
	/**
	 * 最后修改时间
	 */
	private Date modifiedTime;
	
	private int defaultRetryTimes;// 默认错误重试次数
	private int defaultRetryInterval; // 默认错误重试间隔,单位(毫秒)
	private int defaultTimeout; // 默认超时时间(毫秒)
	private String defaultScriptType; // 默认程序脚本类型
//	private Date offlineTime; // 默认程序下线时间
	
	public FlowDef() {
	}
	
	public String getFlowId() {
		return flowId;
	}
	public void setFlowId(String flowId) {
		this.flowId = flowId;
	}
	public String getFlowName() {
		return flowName;
	}
	public void setFlowName(String flowName) {
		this.flowName = flowName;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public java.lang.Integer getMaxParallel() {
		return maxParallel;
	}
	public void setMaxParallel(java.lang.Integer maxParallel) {
		this.maxParallel = maxParallel;
	}
	public Map getProps() {
		return props;
	}
	public void setProps(Map props) {
		this.props = props;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public int getDefaultRetryTimes() {
		return defaultRetryTimes;
	}

	public void setDefaultRetryTimes(int defaultRetryTimes) {
		this.defaultRetryTimes = defaultRetryTimes;
	}

	public int getDefaultRetryInterval() {
		return defaultRetryInterval;
	}

	public void setDefaultRetryInterval(int defaultRetryInterval) {
		this.defaultRetryInterval = defaultRetryInterval;
	}

	public int getDefaultTimeout() {
		return defaultTimeout;
	}

	public void setDefaultTimeout(int defaultTimeout) {
		this.defaultTimeout = defaultTimeout;
	}

	public String getDefaultScriptType() {
		return defaultScriptType;
	}

	public void setDefaultScriptType(String defaultScriptType) {
		this.defaultScriptType = defaultScriptType;
	}

	public T clone() {
		try {
			return (T)super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException("clone error",e);
		}
	}
}
