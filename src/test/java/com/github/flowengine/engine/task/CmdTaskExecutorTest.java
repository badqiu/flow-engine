package com.github.flowengine.engine.task;

import org.junit.Test;

import com.github.flowengine.model.FlowContext;
import com.github.flowengine.model.FlowTask;

public class CmdTaskExecutorTest {

	@Test
	public void testSuccess() throws Exception {
		runCmd("cmd.exe /c echo.exe 'hello world' ");
		Thread.sleep(500);
	}
	
	@Test(expected=RuntimeException.class)
	public void testError() throws Exception {
		
		runCmd("cmd.exe /c erroraasd.exe 'hello world' ");
		Thread.sleep(500);
	}

	private void runCmd(String cmd) throws Exception {
		CmdTaskExecutor e = new CmdTaskExecutor();
		FlowTask task = new FlowTask();
		task.setScript(cmd);
		e.exec(task, new FlowContext());
	}

}
