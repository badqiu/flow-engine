package com.duowan.flowengine.model.def;


/**
 * 任务的边,有方向,代表一个任务依赖另外一个任务
 * 
 * @author badqiu
 */
public class Edge {
	private String flowCode;
	private String beginTaskCode;
	private String endTaskCode;
	
	public String getFlowCode() {
		return flowCode;
	}
	public void setFlowCode(String flowCode) {
		this.flowCode = flowCode;
	}
	public String getBeginTaskCode() {
		return beginTaskCode;
	}
	public void setBeginTaskCode(String beginTaskCode) {
		this.beginTaskCode = beginTaskCode;
	}
	public String getEndTaskCode() {
		return endTaskCode;
	}
	public void setEndTaskCode(String endTaskCode) {
		this.endTaskCode = endTaskCode;
	}
}
