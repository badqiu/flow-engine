package com.github.flowengine.engine.task;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.flowengine.engine.TaskExecutor;
import com.github.flowengine.model.FlowContext;
import com.github.flowengine.model.FlowTask;
import com.github.flowengine.util.AsyncOutputStreamThread;

public class CmdTaskExecutor implements TaskExecutor{

	private static Logger logger = LoggerFactory.getLogger(CmdTaskExecutor.class);
	
	@Override
	public void exec(FlowTask task, FlowContext flowContext) throws Exception {
		String cmd = StringUtils.trim(task.getScript());
		execCmd(cmd);
	}

	public static void execCmd(String cmd) throws IOException, InterruptedException {
		logger.info("exec cmd:"+cmd);
		Process process = Runtime.getRuntime().exec(cmd);
		
		InputStream processInputStream = null;
		if(process != null && process.getInputStream() != null){ 
			processInputStream = process.getInputStream();
			new AsyncOutputStreamThread(processInputStream,System.out).start();
		}
		InputStream processErrorStream = null;
		if(process != null && process.getErrorStream() != null) {
			processErrorStream = process.getErrorStream();
			new AsyncOutputStreamThread(processErrorStream,System.err).start();
		}
		
		int exitValue = process.waitFor();
		
		IOUtils.closeQuietly(processInputStream);
		IOUtils.closeQuietly(processErrorStream);
		logger.info("exec exitValue:" + exitValue + "  with cmd:"+cmd);
		if(exitValue == 0) {
			return;
		}else {
			throw new RuntimeException("error exit value:"+exitValue+" by script:"+cmd);
		}
	}

}
