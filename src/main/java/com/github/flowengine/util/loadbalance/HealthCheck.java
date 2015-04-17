package com.github.flowengine.util.loadbalance;

/**
 * 检查对象自身是否健康
 * @author badqiu
 *
 */
public interface HealthCheck {

	/**
	 * 验证实例是否健康
	 */
	public boolean isHealth();
	
}
