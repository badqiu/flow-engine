package com.github.flowengine.util.loadbalance;

import java.util.List;


/**
 * 实现Master,多个Slavers的数据复制模式
 * 
 * 并且实现主从可切换
 * 
 */
public class MasterSlavers {

	private Object master;
	private List<Object> slavers;
	
}
