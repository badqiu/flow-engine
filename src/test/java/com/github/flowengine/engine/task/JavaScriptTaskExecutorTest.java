package com.github.flowengine.engine.task;

import org.junit.Test;

import com.github.flowengine.model.FlowContext;
import com.github.flowengine.model.FlowTask;

public class JavaScriptTaskExecutorTest {

	JavaScriptTaskExecutor executor = new JavaScriptTaskExecutor();
	@Test
	public void test() throws Exception {
		FlowTask task = new FlowTask();
		task.setScript("print('hello by javascript')");
		executor.exec(task, new FlowContext());
	}

}
