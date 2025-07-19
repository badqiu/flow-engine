package com.github.flowengine.engine.task;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import com.github.flowengine.engine.TaskExecResult;
import com.github.flowengine.model.FlowContext;
import com.github.flowengine.model.FlowTask;

public class CmdTaskExecutorTest {
	FlowTask task = new FlowTask();
	@Test
	public void testSuccess() throws Exception {
		TaskExecResult result = runCmd("cmd.exe /c echo.exe 'hello world' ");
		Thread.sleep(500);
		assertEquals(result.getExitValue(),0);
	}
	
	@Test
	public void testTimeout() throws Exception {
		task.setTimeout(1000);
		TaskExecResult result = runCmd("cmd.exe /c pause 5 ");
		Thread.sleep(500);
		assertTrue(result.getExitValue() != 0);
	}
	
	@Test
	public void testError() throws Exception {
		TaskExecResult result = runCmd("cmd.exe /c erroraasd.exe 'hello world' ");
		Thread.sleep(500);
		assertTrue(result.getExitValue() != 0);
	}

	private TaskExecResult runCmd(String cmd) throws Exception {
		CmdTaskExecutor e = new CmdTaskExecutor();
		task.setScript(cmd);
		return e.exec(task, new FlowContext());
	}
	
	@Test
	public void test() throws Exception {
		try {
			CmdTaskExecutor.execCmd("cmd.exe /c echo.exe 'hello world'");
		}finally {
			System.out.println("exec end");
			System.out.flush();
		}
	}

}
