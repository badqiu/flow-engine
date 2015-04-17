package com.github.flowengine.util.loadbalance;
public  class LBTestBean {
	private int i;
	
	public LBTestBean() {
	}
	
	public LBTestBean(int i) {
		super();
		this.i = i;
	}

	public int print() {
		System.out.println(i);
		return i;
	}
}