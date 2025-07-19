package com.github.flowengine.util;

import java.util.ArrayList;
import java.util.List;

public class Listenerable <T>{

	private List<Listener<T>> listeners = new ArrayList<Listener<T>>();
	
	public void notifyListeners(T target,Object args) {
		for(Listener<T> obj : listeners) {
			obj.update(target, args);
		}
	}
	
	public void notifyListenersOnExecutedEnd(T target) {
		for(Listener<T> obj : listeners) {
			obj.onExecutedEnd(target);
		}
	}
	
	public void addListener(Listener<T> t) {
		listeners.add(t);
	}
	
}
