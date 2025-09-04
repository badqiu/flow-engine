package com.github.flowengine.engine.task;

import java.io.File;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.github.flowengine.engine.TaskExecResult;
import com.github.flowengine.engine.TaskExecutor;
import com.github.flowengine.model.FlowContext;
import com.github.flowengine.model.FlowTask;

public class ShellTaskExecutor implements TaskExecutor{

	private static Logger logger = LoggerFactory.getLogger(ShellTaskExecutor.class);
	
	String tmpDir = System.getProperty("java.io.tmpdir");
	
	@Override
	public TaskExecResult exec(FlowTask task, FlowContext flowContext)
			throws Exception {
		String shellScript = task.getScript();
		Assert.hasText(tmpDir,"java.io.tmpdir must be not blank");
		Assert.hasText(shellScript,"shell script must be not blank");
		
		File shellFile = new File(tmpDir,"ShellTaskExecutor_"+DigestUtils.md5Hex(shellScript)+".sh");
		try {
			FileUtils.writeStringToFile(shellFile, shellScript);
		
			logger.info("exec shell script:"+shellScript);
			return CmdTaskExecutor.execCmd("/bin/bash " + shellFile.getAbsolutePath());
		}finally {
			FileUtils.deleteQuietly(shellFile);
		}
	}
	
}
