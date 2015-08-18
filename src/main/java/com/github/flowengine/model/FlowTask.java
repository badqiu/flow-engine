package com.github.flowengine.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import com.github.flowengine.engine.AsyncTaskExecutor;
import com.github.flowengine.engine.TaskExecResult;
import com.github.flowengine.engine.TaskExecutor;
import com.github.flowengine.model.def.FlowTaskDef;
import com.github.flowengine.util.Listener;
import com.github.flowengine.util.Listenerable;

import static com.github.flowengine.util.NumUtil.defaultInt;

import com.github.rapid.common.util.ScriptEngineUtil;
/**
 * 流程任务实例
 * 
 * TODO: 支持组合模式，任务又支持顺序执行任务
 * 
 * @author badqiu
 *
 */
public class FlowTask extends FlowTaskDef<FlowTask> implements Comparable<FlowTask>,InitializingBean{

	private static Logger logger = LoggerFactory.getLogger(FlowTask.class);
	
	public static String STATUS_RUNNING = "RUNNING";
	public static String STATUS_END = "END";
	
	private String instanceId; //实例ID
	private String flowInstanceId; //任务执行批次ID,可以使用如( flow instanceId填充)
	
	private String status; //任务状态: 可运行,运行中,阻塞(睡眠,等待),停止
	private int execResult = Integer.MIN_VALUE; //执行结果: 0成功,非0为失败
	private boolean executed = false; // 是否已经执行
	private boolean forceExec; //是否强制执行
	private int usedRetryTimes; //已经重试执行次数
	/**
     * 任务执行耗时       
     */ 	
	private long execCostTime;
    /**
     * 任务执行的开发时间       
     */ 	
	private java.util.Date execStartTime;
	/**
     * 任务执行日志       db_column: task_log 
     */ 	
	private java.lang.StringBuilder taskLog = new java.lang.StringBuilder();
	
	private Map context = new HashMap(); //保存上下文内容
	
	/**
	 * 最后执行的异常
	 */
	private Throwable exception;
	
	private transient Listenerable<FlowTask> listenerable = new Listenerable<FlowTask>();
	
	/**
	 * 未执行完成的父亲节点
	 */
	private Set<FlowTask> unFinishParents = new HashSet<FlowTask>();
	
	public FlowTask() {
	}
	
	public FlowTask(String taskId) {
		this(null,taskId);
	}
	
	public FlowTask(String flowId, String taskId) {
		super(flowId,taskId);
	}
	
	public FlowTask(String taskId,String depends,Class<? extends TaskExecutor> scriptType) {
		this(null,taskId);
		setDepends(depends);
		setScriptType(scriptType);
	}
	
	public FlowTask(String flowId, String taskId,String instanceId,String flowInstanceId) {
		super(flowId, taskId);
		this.instanceId = instanceId;
		this.flowInstanceId = flowInstanceId;
	}
	
	
	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	public String getFlowInstanceId() {
		return flowInstanceId;
	}

	public void setFlowInstanceId(String flowInstanceId) {
		this.flowInstanceId = flowInstanceId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getExecResult() {
		return execResult;
	}

	public void setExecResult(int execResult) {
		this.execResult = execResult;
	}
	
	public boolean isExecuted() {
		return executed;
	}

	public long getExecCostTime() {
		return execCostTime;
	}

	public void setExecCostTime(long execCostTime) {
		this.execCostTime = execCostTime;
	}

	public java.util.Date getExecStartTime() {
		return execStartTime;
	}

	public void setExecStartTime(java.util.Date execStartTime) {
		this.execStartTime = execStartTime;
	}

	public String getTaskLog() {
		return taskLog == null ? null : taskLog.toString();
	}

	public void setTaskLog(String taskLog) {
		this.taskLog = new StringBuilder(StringUtils.defaultString(taskLog));
	}
	
	public void addTaskLog(String txt) {
		if(taskLog == null) {
			taskLog = new StringBuilder();
		}
		this.taskLog.append(txt);
	}

	public Throwable getException() {
		return exception;
	}

	public void setException(Throwable exception) {
		this.exception = exception;
	}

	public boolean isForceExec() {
		return forceExec;
	}

	public void setForceExec(boolean forceExec) {
		this.forceExec = forceExec;
	}

	public int getUsedRetryTimes() {
		return usedRetryTimes;
	}

	public void setUsedRetryTimes(int usedRetryTimes) {
		this.usedRetryTimes = usedRetryTimes;
	}
	
	public boolean isExecFail() {
		return !isExecSuccess();
	}

	public boolean isExecSuccess() {
		return getExecResult() == 0;
	}
	
	public Map getContext() {
		return context;
	}

	public void setContext(Map context) {
		this.context = context;
	}

	public Listenerable<FlowTask> getListenerable() {
		return listenerable;
	}

	public void setListenerable(Listenerable<FlowTask> listenerable) {
		this.listenerable = listenerable;
	}
	
	public Set<FlowTask> getUnFinishParents() {
		return unFinishParents;
	}

	public void setUnFinishParents(Set<FlowTask> unFinishParents) {
		this.unFinishParents = unFinishParents;
	}
	
	public void addUnFinisheParent(FlowTask unFinisheParent) {
		if(!unFinishParents.contains(unFinisheParent)) {
			unFinishParents.add(unFinisheParent);
		}
	}

	public void exec(final FlowContext context,final boolean execParents,final boolean execChilds) {
		executed = true;
		logger.info("start exec task,id:" + getId() + " execParents:"+execParents+" execChilds:"+execChilds);
		
		beforeExec(context);
		
		if(execParents) {
			execAll(context,execParents, execChilds,getParents(),true);
		}
		
		
		try {
			execSelf(execParents,context);
		} catch (Exception e) {
			throw new RuntimeException("error on exec,flowTask:"+this,e);
		} 
		
		if(execChilds) {
			execAll(context,execParents, execChilds,getChilds(),true);
		}
		
		afterExec(context);
	}

	protected void afterExec(FlowContext context2) {
	}

	protected void beforeExec(FlowContext context2) {
	}

	private synchronized void execSelf(boolean execParents, final FlowContext context) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException,
			InterruptedException, IOException {
		//判断所有父亲是否已完全执行
		if(execParents && CollectionUtils.isNotEmpty(getUnFinishParents())) {
			return;
		}
		if(isEnabled() == null || !isEnabled()) {
			throw new RuntimeException("task no enabled,taskId:"+getTaskId());
		}
		
		TaskExecutor executor = lookupTaskExecutor(context);
		execStartTime = new Date();
		evalGroovy(context,getBeforeGroovy());
		
		while(true) {
			try {
				status = STATUS_RUNNING;
				logger.info("start execute task,id:"+getTaskId()+" usedRetryTimes:"+this.usedRetryTimes+" TaskExecutor:"+executor+" exception:"+exception);
				
				this.exception = null;
				
				if(defaultInt(getPreSleepTime()) > 0) {
					Thread.sleep(getPreSleepTime());
				}
				
				notifyListeners();
				TaskExecResult taskExecResult = executor.exec(this, context);
				
				waitIfRunning(executor, context, this);
				
				if(taskExecResult != null) {
					addTaskLog(taskExecResult.getLog());
					this.execResult = taskExecResult.getExitValue();
				}else if(executor instanceof AsyncTaskExecutor) {
					this.execResult = ((AsyncTaskExecutor)executor).getExitCode(this, context.getParams());
				}else {
					this.execResult = 0;
				}
				
				if(execResult != 0) {
					throw new RuntimeException("execResult not zero,execResult:"+this.execResult);
				}
				
				evalGroovy(context,getAfterGroovy());
				
				notifyListeners();
				break;
			}catch(Exception e) {
				this.execResult = this.execResult == 0 ? 1 : this.execResult;
				
				logger.warn("exec "+getTaskId()+" error",e);
				if(isTimeout()) {
					break;
				}
				this.exception = e;
				if(this.usedRetryTimes >= defaultInt(getRetryTimes())) {
					break;
				}
				this.usedRetryTimes++;
				notifyListeners();
				
				logger.warn("retry exec "+getTaskId() + ",usedRetryTimes:"+usedRetryTimes+" retryInterval():"+getRetryInterval()+" exception:" + e.getMessage());
				if(defaultInt(getRetryInterval()) > 0) {
					Thread.sleep(getRetryInterval());
				}
			}finally {
				if(isTimeout()) {
					break;
				}
			}
		}
		afterExecuteEnd(context, executor);
	}

	private void afterExecuteEnd(final FlowContext context,
			TaskExecutor executor) throws IOException {
		try {
			this.status = STATUS_END;
			
			if(this.execResult != 0) {
				evalGroovy(context,getErrorGroovy());
			}
			
			if(executor instanceof AsyncTaskExecutor) {
				addTaskLog( IOUtils.toString(((AsyncTaskExecutor)executor).getLog(this, context.getParams())) );
			}
			
			//执行成功,或者执行不成功但失败可忽略,在其孩子的未完成父亲集合中去掉当前任务
			if(this.execResult == 0 || (this.execResult != 0 && this.isIgnoreError())) {
				for(FlowTask flowTask : this.getChilds()) {
					flowTask.getUnFinishParents().remove(this);
				}
			}
			else {
				//否则整个流程标记为失败，并且其孩子节点将不会执行
				context.getFlow().setExecResult(1);
			}
		}finally {
			notifyListeners();
		}
	}

	private boolean isTimeout() {
		this.execCostTime =  System.currentTimeMillis() - execStartTime.getTime();
		if(defaultInt(getTimeout()) > 0) {
			if(this.execCostTime > getTimeout() ) {
				return true;
			}
		}
		return false;
	}

	private TaskExecutor lookupTaskExecutor(FlowContext context) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		if(getTaskExecutor() == null) {
			Assert.hasText(getScriptType(),"scriptType must be not empty");
			TaskExecutor taskExecutor = context.getFlowEngine().getTaskExecutor(getScriptType());
			if(taskExecutor == null) {
				return (TaskExecutor)Class.forName(getScriptType()).newInstance();
			}
			setTaskExecutor(taskExecutor);
		}
		return getTaskExecutor();
	}

	private void evalGroovy(final FlowContext context,String script) {
		if(StringUtils.isNotBlank(script)) {
			ScriptEngineUtil.eval("groovy", script, context.getParams());
		}
	}

	private void waitIfRunning(TaskExecutor executor,final FlowContext context,final FlowTask flowTask) throws InterruptedException {
		if(executor instanceof AsyncTaskExecutor) {
			while(((AsyncTaskExecutor)executor).isRunning(flowTask, context.getParams())) {
				Thread.sleep(1000 * 5);
			}
		}
	}
	
	public void notifyListeners() {
		if(listenerable != null) {
			listenerable.notifyListeners(this, null);
		}
	}

	public void addListener(Listener<FlowTask> t) {
		if(listenerable == null) {
			listenerable = new Listenerable<FlowTask>();
		}
		listenerable.addListener(t);
	}

	/**
	 * 通过计算得出的权重,如孩子越多,则权重越高,孩子自身的权重可以传递给父亲
	 * @return
	 */
	public int computePriority() {
		return defaultInt(getPriority());
	}
	
	@Override
	public int compareTo(FlowTask o) {
		return -new Integer(computePriority()).compareTo(o.computePriority());
	}

	public static void execAll(final FlowContext context, final boolean execParents,final boolean execChilds, Collection<FlowTask> tasks,boolean waitTasksExecEnd) {
		if(CollectionUtils.isEmpty(tasks)) {
			return;
		}
		
		Assert.notNull(context.getExecutorService(),"context.getExecutorService() must be not null");
		
		List<FlowTask> sortedTasks = new ArrayList<FlowTask>(tasks);
		Collections.sort(sortedTasks);
		
		final CountDownLatch dependsCountDownLatch = new CountDownLatch(tasks.size());
		for(final FlowTask depend : sortedTasks) {
			context.getExecutorService().execute(new Runnable() {
				@Override
				public void run() {
					try {
						depend.exec(context,execParents,execChilds);
					}catch(Exception e) {
						e.printStackTrace();
					}finally {
						dependsCountDownLatch.countDown();
					}
				}
			});
		}
		
		if(waitTasksExecEnd) {
			try {
				dependsCountDownLatch.await();
			} catch (InterruptedException e) {
				throw new RuntimeException("interrupt",e);
			}
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if(isEnabled() == null) {
			setEnabled(true);
		}
	}
	
	
}
