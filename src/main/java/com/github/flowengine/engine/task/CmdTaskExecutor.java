package com.github.flowengine.engine.task;

import java.io.IOException;

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
		
		if(process != null && process.getInputStream() != null){ 
			new AsyncOutputStreamThread(process.getInputStream(),System.out).start();
		}
		if(process != null && process.getErrorStream() != null) {
			new AsyncOutputStreamThread(process.getErrorStream(),System.err).start();
		}
		
		process.waitFor();
		
		int exitValue = process.exitValue();
		if(exitValue == 0) {
			return;
		}else {
			throw new RuntimeException("error exit value:"+exitValue+" by script:"+cmd);
		}
	}

}
