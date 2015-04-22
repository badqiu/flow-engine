package com.github.flowengine.engine.task;

import org.junit.Test;

import com.github.flowengine.model.FlowContext;
import com.github.flowengine.model.FlowTask;

public class HttpTaskExecutorTest {

	@Test
	public void test() throws Exception {
		HttpTaskExecutor e = new HttpTaskExecutor();
		FlowTask task = new FlowTask();
		task.setScript("http://www.163.com");
		e.exec(task, new FlowContext());
	}

}
