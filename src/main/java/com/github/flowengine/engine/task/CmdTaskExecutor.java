package com.github.flowengine.engine.task;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import com.github.flowengine.util.LimitOutputStream;

public class CmdTaskExecutor implements TaskExecutor{

	private static final int MAX_OUTPUT_LENGTH = 1024 * 1024 * 3;
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
		return execCmdForTaskExecResult(cmd,MAX_OUTPUT_LENGTH);
	}
	
	// 使用正则表达式正确解析命令行参数（处理带空格的引号参数）
    private static final Pattern CMD_PATTERN = Pattern.compile(
        "\"([^\"]*)\"|'([^']*)'|(\\S+)"
    );
    public static String[] parseCommand(String command) {
        List<String> commandList = new ArrayList<>();
        Matcher matcher = CMD_PATTERN.matcher(command);
        
        while (matcher.find()) {
            String token = null;
            // 尝试匹配三种情况：双引号字符串、单引号字符串、无空格单词
            if (matcher.group(1) != null) { // 双引号字符串
                token = matcher.group(1);
            } else if (matcher.group(2) != null) { // 单引号字符串
                token = matcher.group(2);
            } else { // 无引号单词
                token = matcher.group(3);
            }
            commandList.add(token);
        }
        
        return commandList.toArray(new String[0]);
    }
    
	public static TaskExecResult execCmdForTaskExecResult(String cmd,int maxOutputLength) throws IOException, InterruptedException {
		String[] parsedCmd = parseCommand(cmd);
		logger.info("exec cmd:"+cmd);
		logger.info("parsedCmd:"+StringUtils.join(parsedCmd," "));
		long start = System.currentTimeMillis();
		Process process = Runtime.getRuntime().exec(parsedCmd);
		
		InputStream processInputStream = null;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		if(process != null && process.getInputStream() != null){ 
			processInputStream = process.getInputStream();
			new AsyncOutputStreamThread(processInputStream,new TeeOutputStream(new LimitOutputStream(out,maxOutputLength),System.err)).start();
		}
		
		InputStream processErrorStream = null;
		ByteArrayOutputStream errOut = new ByteArrayOutputStream();
		if(process != null && process.getErrorStream() != null) {
			processErrorStream = process.getErrorStream();
			new AsyncOutputStreamThread(processErrorStream,new TeeOutputStream(new LimitOutputStream(errOut,maxOutputLength),System.err)).start();
		}
		
		int exitValue = process.waitFor();
		
		IOUtils.closeQuietly(processInputStream);
		IOUtils.closeQuietly(processErrorStream);
		long cost = System.currentTimeMillis() - start;
		logger.info("exec exitValue:" + exitValue + "  with cmd:"+cmd+" costSeconds:" + (cost / 1000));
		return new TaskExecResult(exitValue,out.toString(),errOut.toString());
	}

	
}
