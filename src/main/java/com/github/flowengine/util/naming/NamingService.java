package com.github.flowengine.util.naming;

import java.util.List;

import javax.naming.Context;
import javax.naming.NamingException;

import org.springframework.jndi.JndiCallback;
import org.springframework.jndi.JndiTemplate;

public interface NamingService {

	public void bind(String name,String url);
	
	public void unbind(String name,String url);
	
	public void addListener(String name,NamingServiceListener listener);
	
	public void removeListener(String name,NamingServiceListener listener);
	
	public List<String> list(String name);
	
}
