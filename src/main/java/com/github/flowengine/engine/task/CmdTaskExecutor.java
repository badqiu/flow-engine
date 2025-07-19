package com.github.flowengine.engine.task;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
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
    private static Logger logger = LoggerFactory.getLogger(CmdTaskExecutor.class);
    
    private static final int MAX_OUTPUT_LENGTH = 1024 * 1024 * 3;
    private static final int DEFAULT_TIMEOUT_SECONDS = 0; // 默认不超时
    
    @Override
    public TaskExecResult exec(FlowTask task, FlowContext flowContext) throws Exception {
        String cmd = StringUtils.trim(task.getScript());
        int timeoutSeconds = task.getTimeout() / 1000;
        return execCmdForTaskExecResult(cmd, MAX_OUTPUT_LENGTH, timeoutSeconds);
    }

    public static TaskExecResult execCmd(String cmd) throws IOException, InterruptedException {
        return execCmd(cmd,DEFAULT_TIMEOUT_SECONDS);
    }
    
    public static TaskExecResult execCmd(String cmd, int timeoutSeconds) 
        throws IOException, InterruptedException {
        
        TaskExecResult result = execCmdForTaskExecResult(cmd, MAX_OUTPUT_LENGTH, timeoutSeconds);
        if(result.getExitValue() != 0) {
            throw new RuntimeException("Error exit value:"+result.getExitValue()+" by script:"+cmd);
        }
        return result;
    }

    public static TaskExecResult execCmdForTaskExecResult(String cmd) 
        throws IOException, InterruptedException, TimeoutException {
        
        return execCmdForTaskExecResult(cmd, MAX_OUTPUT_LENGTH, DEFAULT_TIMEOUT_SECONDS);
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
    
    public static TaskExecResult execCmdForTaskExecResult(String cmd, int maxOutputLength, int timeoutSeconds) 
        throws IOException, InterruptedException {
        
        String[] parsedCmd = parseCommand(cmd);
        logger.info("Executing command: {}", cmd);
        logger.info("Parsed command: {}", StringUtils.join(parsedCmd, " "));
        logger.info("Timeout: {} seconds", timeoutSeconds > 0 ? timeoutSeconds : "disabled");
        
        long start = System.currentTimeMillis();
        Process process = Runtime.getRuntime().exec(parsedCmd);
        
        // 创建输出流处理
        InputStream processInputStream = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        if(process != null && process.getInputStream() != null){ 
            processInputStream = process.getInputStream();
            new AsyncOutputStreamThread(processInputStream, 
                new TeeOutputStream(new LimitOutputStream(out, maxOutputLength), System.err))
                .start();
        }
        
        // 创建错误流处理
        InputStream processErrorStream = null;
        ByteArrayOutputStream errOut = new ByteArrayOutputStream();
        if(process != null && process.getErrorStream() != null) {
            processErrorStream = process.getErrorStream();
            new AsyncOutputStreamThread(processErrorStream, 
                new TeeOutputStream(new LimitOutputStream(errOut, maxOutputLength), System.err))
                .start();
        }
        
        // 创建执行监视器
        ProcessMonitor monitor = new ProcessMonitor(process, cmd, timeoutSeconds);
        monitor.start();
        
        try {
            // 等待进程完成（带超时控制）
            monitor.awaitCompletion();
            
            int exitValue = monitor.getExitValue();
            long cost = System.currentTimeMillis() - start;
            logger.info("Command execution completed: exitValue={}, costSeconds={}", 
                        exitValue, cost/1000.0);
            
            return new TaskExecResult(exitValue, out.toString(), errOut.toString());
        } catch (TimeoutException te) {
            // 强制终止进程
            logger.error("Command timed out after {} seconds: {}", timeoutSeconds, cmd);
            
            // 添加超时信息到错误输出
            errOut.write(("\n[ERROR] Command timed out after " + timeoutSeconds + " seconds").getBytes());
            return new TaskExecResult(-9, out.toString(), errOut.toString());
        } finally {
            IOUtils.closeQuietly(processInputStream);
            IOUtils.closeQuietly(processErrorStream);
            monitor.cleanup();
        }
    }

    /**
     * 进程监视器，处理超时强制终止
     */
    private static class ProcessMonitor {
        private final Process process;
        private final String command;
        private long timeoutMillis;
        private Future<?> timeoutFuture;
        private final ExecutorService executor = Executors.newSingleThreadExecutor();
        
        private volatile int exitValue = Integer.MIN_VALUE;
        private volatile boolean completed = false;
        
        public ProcessMonitor(Process process, String command, int timeoutSeconds) {
            this.process = process;
            this.command = command;
            this.timeoutMillis = timeoutSeconds * 1000L;
        }
        
        public void start() {
        	if(timeoutMillis <= 0) {
        		timeoutMillis = Long.MAX_VALUE;
        	}
        	
        	// 设置超时强制终止任务
            this.timeoutFuture = executor.submit(() -> {
                try {
                    if (!process.waitFor(timeoutMillis, TimeUnit.MILLISECONDS)) {
                        // 超时处理
                        logger.warn("Process timeout detected, forcing termination..., command:"+command);
                        if (process.isAlive()) {
                            process.destroyForcibly();
                            logger.error("Process was forcibly terminated due to timeout");
                        }
                        throw new TimeoutException("Command execution timed out");
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.info("Timeout thread interrupted");
                }
                return null;
            });
        }
        
        public void awaitCompletion() throws InterruptedException, TimeoutException {
            try {
                timeoutFuture.get();
                completed = true;
                exitValue = process.exitValue();
            } catch (ExecutionException e) {
                if (e.getCause() instanceof TimeoutException) {
                    throw (TimeoutException) e.getCause();
                } else {
                    throw new RuntimeException("Unexpected error in process monitoring", e.getCause());
                }
            } catch (CancellationException e) {
                logger.info("Process monitoring was cancelled");
            }
        }
        
        public int getExitValue() {
            return exitValue;
        }
        
        public void cleanup() {
            // 确保所有资源都被释放
            timeoutFuture.cancel(true);
            executor.shutdownNow();
            
            try {
                if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                    logger.warn("Executor failed to terminate promptly");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // 如果进程仍在运行，强制终止
            if (process.isAlive()) {
                logger.error("Process still alive after cleanup, forcing termination...");
                process.destroyForcibly();
            }
        }
    }
}