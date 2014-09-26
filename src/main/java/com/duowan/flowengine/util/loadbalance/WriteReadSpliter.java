package com.duowan.flowengine.util.loadbalance;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;


/**
 * 实现 读写分离
 * @author badqiu
 *
 */
public class WriteReadSpliter implements MethodInterceptor{

	private Object writerObject;
	private Object readerObject;
	
	public boolean isWriteOperation() {
		return true;
	}
	
	public boolean isReadOperation() {
		return true;
	}

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		if(isWriteOperation()) {
			return invocation.getMethod().invoke(writerObject, invocation.getArguments());
		}else if(isReadOperation()) {
			return invocation.getMethod().invoke(readerObject, invocation.getArguments());
		}else {
			throw new RuntimeException("unknow operation,must be read or write");
		}
	}
	
}
