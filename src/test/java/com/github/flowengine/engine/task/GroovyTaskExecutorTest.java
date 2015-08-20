package com.github.flowengine.engine.task;

import org.junit.Test;

import com.github.flowengine.model.FlowContext;
import com.github.flowengine.model.FlowTask;

public class GroovyTaskExecutorTest {

	GroovyTaskExecutor executor = new GroovyTaskExecutor();
	@Test
	public void test() throws Exception {
		FlowTask task = new FlowTask();
		task.setScript("System.out.println('hello by groovy')");
		executor.exec(task, new FlowContext());
	}
	
}
