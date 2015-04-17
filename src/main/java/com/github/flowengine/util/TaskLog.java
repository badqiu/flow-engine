package com.github.flowengine.util;

public class TaskLog {
	/**
     *  系统       db_column: system 
     */ 	
	private java.lang.String system;
	/**
	 * 日志分组
	 */
	private String group;
	/**
	 * 日志事件
	 */
	private String event;
	/**
	 * 外部业务类型
	 */
	private String outBizType;
	/**
	 * 外部业务ID
	 */
	private String outBizId;
	/**
	 * 任务状态: INIT:初始化,RUNNING:运行中,BLOCK:阻塞(睡眠,等待),END:停止
	 */
	private String status; //
	/**
	 * 执行结果: 0成功,非0为失败
	 */
	private int execResult = Integer.MIN_VALUE; //
	/**
	 * 已经重试执行次数
	 */
	private int usedRetryTimes;
	/**
     * 任务执行耗时       
     */ 	
	private long execCostTime;
    /**
     * 任务执行的开发时间       
     */ 	
	private java.util.Date execStartTime;
	/**
     * 任务执行日志       db_column: task_log 
     */ 	
	private java.lang.String taskLog;
	/**
	 * 最后执行的异常
	 */
	private Throwable exception;
	/**
	 * 某台主机发生的异常
	 */
	private String host;
	/**
     *  任务执行结果集大小 ,默认0       db_column: result_size 
     */ 	
	private java.lang.Long resultSize;
    /**
     *  任务执行循环次数 ,默认1       db_column: loop_count 
     */ 	
	private java.lang.Long loopCount;
	
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public String getEvent() {
		return event;
	}
	public void setEvent(String event) {
		this.event = event;
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
	public int getUsedRetryTimes() {
		return usedRetryTimes;
	}
	public void setUsedRetryTimes(int usedRetryTimes) {
		this.usedRetryTimes = usedRetryTimes;
	}
	public long getExecCostTime() {
		return execCostTime;
	}
	public void setExecCostTime(long execCostTime) {
		this.execCostTime = execCostTime;
	}
	public java.util.Date getExecStartTime() {
		return execStartTime;
	}
	public void setExecStartTime(java.util.Date execStartTime) {
		this.execStartTime = execStartTime;
	}
	public java.lang.String getTaskLog() {
		return taskLog;
	}
	public void setTaskLog(java.lang.String taskLog) {
		this.taskLog = taskLog;
	}
	public Throwable getException() {
		return exception;
	}
	public void setException(Throwable exception) {
		this.exception = exception;
	}
	public java.lang.String getSystem() {
		return system;
	}
	public void setSystem(java.lang.String system) {
		this.system = system;
	}
	public String getOutBizType() {
		return outBizType;
	}
	public void setOutBizType(String outBizType) {
		this.outBizType = outBizType;
	}
	public String getOutBizId() {
		return outBizId;
	}
	public void setOutBizId(String outBizId) {
		this.outBizId = outBizId;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public java.lang.Long getResultSize() {
		return resultSize;
	}
	public void setResultSize(java.lang.Long resultSize) {
		this.resultSize = resultSize;
	}
	public java.lang.Long getLoopCount() {
		return loopCount;
	}
	public void setLoopCount(java.lang.Long loopCount) {
		this.loopCount = loopCount;
	}
	
}
