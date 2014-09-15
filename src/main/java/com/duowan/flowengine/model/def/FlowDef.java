package com.duowan.flowengine.model.def;

import java.util.Date;
import java.util.Map;

import com.duowan.common.beanutils.BeanUtils;
import com.duowan.common.beanutils.PropertyUtils;
import com.duowan.common.util.DateConvertUtils;
import com.duowan.flowengine.model.Flow;

/**
 * 流程 定义
 * 
 * @author badqiu
 *
 */
public class FlowDef {
	private String flowCode;
	private String flowName;
	private String remarks;
	/**
	 * 最大并行度 db_column: max_parallel
	 */
	private java.lang.Integer maxParallel;
	/**
	 * 附加属性
	 */
	private Map props; 
	
	public String getFlowCode() {
		return flowCode;
	}
	public void setFlowCode(String flowCode) {
		this.flowCode = flowCode;
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
	
	public Flow newInstance() {
		String instanceId = DateConvertUtils.format(new Date(), "yyyyMMddHHmmss");
		Flow result = new Flow(getFlowCode(),instanceId);
		PropertyUtils.copyProperties(result, this);
		return result;
	}
	
}
