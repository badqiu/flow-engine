package com.duowan.flowengine.model.def;

import java.util.Date;
import java.util.Map;

import com.duowan.common.beanutils.PropertyUtils;
import com.duowan.common.util.DateConvertUtils;
import com.duowan.flowengine.engine.TaskExecutor;
import com.duowan.flowengine.model.FlowTask;

/**
 * 流程中的任务 定义
 * 
 * @author badqiu
 *
 */
public class FlowTaskDef {
	private String flowCode;//流程代码
	private String taskCode;//任务代码
	
	private String taskModule; //任务所属模块,无用属性
	private String taskName;//任务名称
	private String remarks; //任务备注
	private boolean enabled;//任务是否激活
	
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
     * 要运行的程序脚本       db_column: program 
     */ 	
	private String program;
    /**
     * 要运行的程序类型(java_class,bat,shell,shell_script,url,hive_sql,jdbc_sql,java_main,groovy)       db_column: program_type 
     */ 	
	private java.lang.String programClass;
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
	private Map props; 
	/**
	 * 依赖的任务
	 */
	private String depends;
	
	public FlowTaskDef(String flowCode, String taskCode) {
		super();
		this.flowCode = flowCode;
		this.taskCode = taskCode;
	}
	
	public String getFlowCode() {
		return flowCode;
	}
	public void setFlowCode(String flowCode) {
		this.flowCode = flowCode;
	}
	public String getTaskCode() {
		return taskCode;
	}
	public void setTaskCode(String taskCode) {
		this.taskCode = taskCode;
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
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
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
	public java.lang.String getProgram() {
		return program;
	}
	public void setProgram(java.lang.String program) {
		this.program = program;
	}
	public java.lang.String getProgramClass() {
		return programClass;
	}
	public void setProgramClass(java.lang.String programClass) {
		this.programClass = programClass;
	}
	public void setProgramClass(Class<? extends TaskExecutor> clazz) {
		setProgramClass(clazz.getName());
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

	public String getDepends() {
		return depends;
	}

	public void setDepends(String depends) {
		this.depends = depends;
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

	public FlowTask newInstance(String batchId) {
		String instanceId = DateConvertUtils.format(new Date(), "yyyyMMddHHmmss");
		FlowTask result = new FlowTask(getFlowCode(),getTaskCode(),instanceId,batchId);
		PropertyUtils.copyProperties(result, this);
		return result;
	}

	@Override
	public String toString() {
		return "FlowTaskDef [flowCode=" + flowCode + ", taskCode=" + taskCode
				+ ", taskModule=" + taskModule + ", taskName=" + taskName
				+ ", remarks=" + remarks + ", enabled=" + enabled
				+ ", retryTimes=" + retryTimes + ", retryInterval="
				+ retryInterval + ", isIgnoreError=" + isIgnoreError
				+ ", preSleepTime=" + preSleepTime + ", timeout=" + timeout
				+ ", execAgent=" + execAgent + ", program=" + program
				+ ", programClass=" + programClass + ", offlineTime="
				+ offlineTime + ", priority=" + priority + ", props=" + props
				+ ", depends=" + depends + "]";
	}
	
	
}
