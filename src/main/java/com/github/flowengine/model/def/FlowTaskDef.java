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
public class FlowTaskDef <T extends GraphNode> extends GraphNode<T> implements Serializable {
	private static final long serialVersionUID = 8556055799573541106L;
	
	private String flowId;//流程代码
	private String taskId;//任务代码
	
	private String taskModule; //任务所属模块,无用属性
	private String taskName;//任务名称
	private String remarks; //任务备注
	private Boolean enabled = true;//任务是否激活
	
	private int retryTimes;//错误重试次数
	private int retryInterval; //错误重试间隔
	/**
     * 最终失败是否可忽略       db_column: is_ignore_error TODO: 与retryTimes有点重,无限重试可完成该功能
     */ 	
	private boolean isIgnoreError;
	/**
     * 任务执行前睡眠等待时间(秒) 
     */ 	
	private int preSleepTime;	
	/**
	 * 任务执行超时时间,单位(秒)
	 */
	private int timeout;
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
	private int priority;
	/**
	 * 附加属性
	 */
	private Map props = new HashMap(); 
	/**
	 * 最大并行度(控制子任务的并发执行度) db_column: max_parallel
	 */
	private int maxParallel;
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
	
	public FlowTaskDef() {
		this.enabled = true;
	}
	
	public FlowTaskDef(String flowCode, String taskCode) {
		super(taskCode);
		this.flowId = flowCode;
		this.taskId = taskCode;
	}
	
	public String getFlowId() {
		return flowId;
	}
	public void setFlowId(String flowCode) {
		this.flowId = flowCode;
	}
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskCode) {
		this.taskId = taskCode;
		setId(taskCode);
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
	public int getRetryTimes() {
		return retryTimes;
	}
	public void setRetryTimes(int retryTimes) {
		this.retryTimes = retryTimes;
	}
	public int getRetryInterval() {
		return retryInterval;
	}
	public void setRetryInterval(int retryInterval) {
		this.retryInterval = retryInterval;
	}
	public boolean isIgnoreError() {
		return isIgnoreError;
	}
	public void setIgnoreError(boolean isIgnoreError) {
		this.isIgnoreError = isIgnoreError;
	}
	public int getPreSleepTime() {
		return preSleepTime;
	}
	public void setPreSleepTime(int sleepTime) {
		this.preSleepTime = sleepTime;
	}
	public int getTimeout() {
		return timeout;
	}
	public void setTimeout(int timeout) {
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
	public java.util.Date getOfflineTime() {
		return offlineTime;
	}
	public void setOfflineTime(java.util.Date offlineTime) {
		this.offlineTime = offlineTime;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
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
	
	public int getMaxParallel() {
		return maxParallel;
	}

	public void setMaxParallel(int maxParallel) {
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

	@Override
	public String toString() {
		return "FlowTaskDef [flowId=" + getFlowId() + ", taskId=" + getTaskId()
				+ ", taskModule=" + taskModule + ", taskName=" + taskName
				+ ", remarks=" + remarks + ", enabled=" + enabled
				+ ", retryTimes=" + retryTimes + ", retryInterval="
				+ retryInterval + ", isIgnoreError=" + isIgnoreError
				+ ", preSleepTime=" + preSleepTime + ", timeout=" + timeout
				+ ", execAgent=" + execAgent + ", script=" + script
				+ ", scriptType=" + scriptType + ", offlineTime="
				+ offlineTime + ", priority=" + priority + ", props=" + props
				+ ", depends=" + getDepends() + "]";
	}

}
