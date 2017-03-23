package com.github.flowengine.engine.task;

import org.junit.Test;

import com.github.flowengine.model.FlowTask;

public class ShellTaskExecutorTest {

	@Test
	public void test() throws Exception {
		ShellTaskExecutor ste = new ShellTaskExecutor();
		FlowTask task = new FlowTask();
		task.setScript("echo 'hello world'");
		ste.exec(task , null);
	}

}
