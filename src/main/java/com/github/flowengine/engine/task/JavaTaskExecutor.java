package com.github.flowengine.engine.task;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import com.github.flowengine.engine.TaskExecResult;
import com.github.flowengine.engine.TaskExecutor;
import com.github.flowengine.model.FlowContext;
import com.github.flowengine.model.FlowTask;

public class JavaTaskExecutor implements TaskExecutor {

	private String javaClass;
	private String method;
	
	
	public String getJavaClass() {
		return javaClass;
	}

	public void setJavaClass(String javaClass) {
		this.javaClass = javaClass;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	@Override
	public TaskExecResult exec(FlowTask task, FlowContext flowContext)
			throws Exception {
		String javaClass = lookupJavaClass(task,"javaClass",this.javaClass);
		Assert.hasText(javaClass, "javaClass props must be not empty");
		String method = lookupJavaClass(task,"method",this.method);
		
		Class clazz = ClassUtils.forName(javaClass, null);
		
		Object instance = clazz.newInstance();
		if(StringUtils.isBlank(method)) {
			if(instance instanceof Runnable) {
				((Runnable)instance).run();
			}else if(instance instanceof Callable) {
				((Callable)instance).call();
			}else {
				throw new IllegalArgumentException("javaClass must be implement interface:Runnable or Callable");
			}
		}else {
			Method methodObj = ClassUtils.getMethodIfAvailable(clazz, method, null);
			methodObj.invoke(instance);
		}
		
		return null;
	}

	private String lookupJavaClass(FlowTask task,String propName,String value) {
		String result = value;
		if(StringUtils.isBlank(result)) {
			if(task != null && task.getProps() != null) {
				result = (String) task.getProps().get(propName);
			}
		}
		
		return result;
	}

}
