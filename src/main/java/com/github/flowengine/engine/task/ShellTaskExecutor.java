package com.github.flowengine.engine.task;

import java.io.File;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.flowengine.engine.TaskExecResult;
import com.github.flowengine.engine.TaskExecutor;
import com.github.flowengine.model.FlowContext;
import com.github.flowengine.model.FlowTask;

public class ShellTaskExecutor implements TaskExecutor{

	private static Logger logger = LoggerFactory.getLogger(ShellTaskExecutor.class);
	
	@Override
	public TaskExecResult exec(FlowTask task, FlowContext flowContext)
			throws Exception {
		String shellScript = task.getScript();
		File shellFile = new File("/tmp","ShellTaskExecutor_"+DigestUtils.md5(shellScript)+".sh");
		FileUtils.writeStringToFile(shellFile, shellScript);
		
		logger.info("exec shell script:"+shellScript);
		try {
			return CmdTaskExecutor.execCmd("/bin/bash " + shellFile.getAbsolutePath());
		}finally {
			FileUtils.deleteQuietly(shellFile);
		}
	}
	
}
