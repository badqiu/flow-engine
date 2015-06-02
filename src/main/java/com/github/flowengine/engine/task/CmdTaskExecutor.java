package com.github.flowengine.engine.task;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.TeeOutputStream;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.flowengine.engine.TaskExecResult;
import com.github.flowengine.engine.TaskExecutor;
import com.github.flowengine.model.FlowContext;
import com.github.flowengine.model.FlowTask;
import com.github.flowengine.util.AsyncOutputStreamThread;

public class CmdTaskExecutor implements TaskExecutor{

	private static Logger logger = LoggerFactory.getLogger(CmdTaskExecutor.class);
	
	@Override
	public TaskExecResult exec(FlowTask task, FlowContext flowContext) throws Exception {
		String cmd = StringUtils.trim(task.getScript());
		return execCmdForTaskExecResult(cmd);
	}

	public static TaskExecResult execCmd(String cmd) throws IOException, InterruptedException {
		TaskExecResult result =  execCmdForTaskExecResult(cmd);
		if(result.getExitValue() != 0) {
			throw new RuntimeException("error exit value:"+result.getExitValue()+" by script:"+cmd);
		}
		return result;
	}

	public static TaskExecResult execCmdForTaskExecResult(String cmd) throws IOException, InterruptedException {
		logger.info("exec cmd:"+cmd);
		Process process = Runtime.getRuntime().exec(cmd);
		
		InputStream processInputStream = null;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		if(process != null && process.getInputStream() != null){ 
			processInputStream = process.getInputStream();
			new AsyncOutputStreamThread(processInputStream,new TeeOutputStream(out,System.err)).start();
		}
		
		InputStream processErrorStream = null;
		ByteArrayOutputStream errOut = new ByteArrayOutputStream();
		if(process != null && process.getErrorStream() != null) {
			processErrorStream = process.getErrorStream();
			new AsyncOutputStreamThread(processErrorStream,new TeeOutputStream(errOut,System.err)).start();
		}
		
		int exitValue = process.waitFor();
		
		IOUtils.closeQuietly(processInputStream);
		IOUtils.closeQuietly(processErrorStream);
		logger.info("exec exitValue:" + exitValue + "  with cmd:"+cmd);
		return new TaskExecResult(exitValue,out.toString(),errOut.toString());
	}

}
