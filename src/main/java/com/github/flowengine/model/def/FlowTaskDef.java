package com.github.flowengine.model.def;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.github.flowengine.engine.TaskExecutor;
import com.github.flowengine.graph.GraphNode;

/**
 * 流程中的任务 定义
 * 
 * @author badqiu
 *
 */
public class FlowTaskDef <T extends GraphNode<?>> extends GraphNode<T> implements Serializable,Cloneable {
	private static final long serialVersionUID = 8556055799573541106L;
	
	private String flowId;//流程代码
//	private String taskId;//任务代码
	
	private String taskModule; //任务所属模块,无用属性
	private String taskName;//任务名称
	private String remarks; //任务备注
	private Boolean enabled = true;//任务是否激活
	
	private Integer retryTimes;//错误重试次数
	private Integer retryInterval; //错误重试间隔,单位(毫秒)
	/**
     * 最终失败是否可忽略       db_column: is_ignore_error 
     */ 	
	private boolean ignoreError;
	/**
     * 任务执行前睡眠等待时间(毫秒) 
     */ 	
	private Integer preSleepTime;	
	/**
	 * 任务执行超时时间,单位(毫秒)
	 */
	private Integer timeout;
	/**
	 * 在那一台agent(机器)执行程序
	 */
	private String execAgent;
	/**
	 * 在任务执行之前,执行的groovy脚本
	 */
	private String beforeGroovy;
	/**
	 * 在任务执行之后,执行的groovy脚本
	 */
	private String afterGroovy;
	/**
	 * 在任务执行发生异常后,执行的groovy脚本
	 */
	private String errorGroovy;
    /**
     * 要运行的程序脚本       db_column: script 
     */ 	
	private String script;
    /**
     * 要运行的程序类型(java_class,bat,shell,shell_script,url,hive_sql,jdbc_sql,java_main,groovy)       db_column: program_type 
     */ 	
	private java.lang.String scriptType;
    /**
     * 任务下线时间       db_column: offline_time 
     */ 	
	private java.util.Date offlineTime;
    /**
     * 优先级(数值越高,优先级越高)       db_column: priority 
     */ 	
	private Integer priority;
	/**
	 * 附加属性
	 */
	private Map props = new HashMap(); 
	/**
	 * 最大并行度(控制子任务的并发执行度) db_column: max_parallel
	 */
	private Integer maxParallel;
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
	private String creator;
	/**
	 * 最后操作人
	 */
	private String operator;
	/**
	 * 最后修改时间
	 */
	private Date modifiedTime;
	/**
	 * 通过scriptType生成的TaskExecutor
	 */
	private transient TaskExecutor taskExecutor;
	
	public FlowTaskDef() {
		this.enabled = true;
	}
	
	public FlowTaskDef(String flowId, String taskId) {
		super(taskId);
		this.flowId = flowId;
		setId(taskId);
	}
	
	public String getFlowId() {
		return flowId;
	}
	public void setFlowId(String flowId) {
		this.flowId = flowId;
	}
	public String getTaskId() {
		return getId();
	}
	public void setTaskId(String taskId) {
		setId(taskId);
	}
	public String getTaskModule() {
		return taskModule;
	}
	public void setTaskModule(String taskModule) {
		this.taskModule = taskModule;
	}
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public Boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}
	public Integer getRetryTimes() {
		return retryTimes;
	}
	public void setRetryTimes(Integer retryTimes) {
		this.retryTimes = retryTimes;
	}
	public Integer getRetryInterval() {
		return retryInterval;
	}
	public void setRetryInterval(Integer retryInterval) {
		this.retryInterval = retryInterval;
	}
	public boolean isIgnoreError() {
		return ignoreError;
	}
	public void setIgnoreError(boolean ignoreError) {
		this.ignoreError = ignoreError;
	}
	public Integer getPreSleepTime() {
		return preSleepTime;
	}
	public void setPreSleepTime(Integer sleepTime) {
		this.preSleepTime = sleepTime;
	}
	public Integer getTimeout() {
		return timeout;
	}
	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}
	public String getExecAgent() {
		return execAgent;
	}
	public void setExecAgent(String execAgent) {
		this.execAgent = execAgent;
	}
	public String getScript() {
		return script;
	}
	public void setScript(String script) {
		this.script = script;
	}
	public java.lang.String getScriptType() {
		return scriptType;
	}
	public void setScriptType(java.lang.String scriptType) {
		this.scriptType = scriptType;
	}
	public void setScriptType(Class<? extends TaskExecutor> clazz) {
		setScriptType(clazz.getName());
	}
	public TaskExecutor getTaskExecutor() {
		return taskExecutor;
	}
	public void setTaskExecutor(TaskExecutor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}
	public java.util.Date getOfflineTime() {
		return offlineTime;
	}
	public void setOfflineTime(java.util.Date offlineTime) {
		this.offlineTime = offlineTime;
	}
	public Integer getPriority() {
		return priority;
	}
	public void setPriority(Integer priority) {
		this.priority = priority;
	}
	public Map getProps() {
		return props;
	}

	public void setProps(Map props) {
		this.props = props;
	}

	public String getBeforeGroovy() {
		return beforeGroovy;
	}

	public void setBeforeGroovy(String preGroovy) {
		this.beforeGroovy = preGroovy;
	}

	public String getAfterGroovy() {
		return afterGroovy;
	}

	public void setAfterGroovy(String afterGroovy) {
		this.afterGroovy = afterGroovy;
	}

	public String getErrorGroovy() {
		return errorGroovy;
	}

	public void setErrorGroovy(String exceptionGroovy) {
		this.errorGroovy = exceptionGroovy;
	}
	
	public Integer getMaxParallel() {
		return maxParallel;
	}

	public void setMaxParallel(Integer maxParallel) {
		this.maxParallel = maxParallel;
	}

	public String getCron() {
		return cron;
	}

	public void setCron(String cron) {
		this.cron = cron;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public Date getModifiedTime() {
		return modifiedTime;
	}

	public void setModifiedTime(Date modifiedTime) {
		this.modifiedTime = modifiedTime;
	}
	
	public T clone() {
		try {
			return (T)super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException("clone error",e);
		}
	}

	@Override
	public String toString() {
		return "FlowTaskDef [flowId=" + getFlowId() + ", taskId=" + getTaskId()
				+ ", taskModule=" + taskModule + ", taskName=" + taskName
				+ ", remarks=" + remarks + ", enabled=" + enabled
				+ ", retryTimes=" + retryTimes + ", retryInterval="
				+ retryInterval + ", ignoreError=" + ignoreError
				+ ", preSleepTime=" + preSleepTime + ", timeout=" + timeout
				+ ", execAgent=" + execAgent + ", script=" + script
				+ ", scriptType=" + scriptType + ", offlineTime="
				+ offlineTime + ", priority=" + priority + ", props=" + props
				+ ", depends=" + getDepends() + "]";
	}

}
