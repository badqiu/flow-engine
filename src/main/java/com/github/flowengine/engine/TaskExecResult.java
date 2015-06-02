package com.github.flowengine.engine;

public class TaskExecResult {

	private String log;
	private String errLog;
	private int exitValue;
	
	public TaskExecResult(){
	}
	
	public TaskExecResult(int exitValue,String log, String errLog) {
		super();
		this.log = log;
		this.errLog = errLog;
		this.exitValue = exitValue;
	}

	public TaskExecResult(String log, String errLog) {
		super();
		this.log = log;
		this.errLog = errLog;
	}
	
	public String getLog() {
		return log;
	}
	public void setLog(String log) {
		this.log = log;
	}
	public String getErrLog() {
		return errLog;
	}
	public void setErrLog(String errLog) {
		this.errLog = errLog;
	}
	public int getExitValue() {
		return exitValue;
	}
	public void setExitValue(int exitValue) {
		this.exitValue = exitValue;
	}
	
}
