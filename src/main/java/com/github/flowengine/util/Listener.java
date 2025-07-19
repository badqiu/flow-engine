package com.github.flowengine.util;

public interface Listener <T> {

	public void update(T obj,Object args);
	
	public void onExecutedEnd(T obj);
	
}
