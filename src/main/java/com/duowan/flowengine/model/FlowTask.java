package com.duowan.flowengine.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.util.Assert;

import com.duowan.common.util.ScriptEngineUtil;
import com.duowan.flowengine.engine.AsyncTaskExecutor;
import com.duowan.flowengine.engine.TaskExecutor;
import com.duowan.flowengine.model.def.FlowTaskDef;
import com.duowan.flowengine.util.Listener;
import com.duowan.flowengine.util.Listenerable;
import com.duowan.flowengine.util.Retry;
/**
 * 流程任务实例
 * @author badqiu
 *
 */
public class FlowTask extends FlowTaskDef<FlowTask> implements Comparable<FlowTask>{

	private String instanceId; //实例ID
	private String flowInstanceId; //任务执行批次ID,可以使用如( flow instanceId填充)
	
	private String status; //任务状态: 可运行,运行中,阻塞(睡眠,等待),停止
	private int execResult = Integer.MIN_VALUE; //执行结果: 0成功,非0为失败
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
	private java.lang.String taskLog;
	/**
	 * 最后执行的异常
	 */
	private Throwable exception;
	
	private transient Listenerable<FlowTask> listenerable = new Listenerable<FlowTask>();
	
	public FlowTask() {
	}
	
	public FlowTask(String taskCode) {
		this(null,taskCode);
	}
	
	public FlowTask(String flowCode, String taskCode) {
		super(flowCode,taskCode);
	}
	
	public FlowTask(String taskCode,String depends,Class<? extends TaskExecutor> programClass) {
		this(null,taskCode);
		setDepends(depends);
		setProgramClass(programClass);
	}
	
	public FlowTask(String flowCode, String taskCode,String instanceId,String flowInstanceId) {
		super(flowCode, taskCode);
		this.instanceId = instanceId;
		this.flowInstanceId = flowInstanceId;
	}
	
	public void exec(final FlowContext context,final boolean execParents,final boolean execChilds) {
		if(execParents) {
			execAll(context,execParents, execChilds,getParents(),true);
		}
		
		try {
			execSelf(context);
		} catch (Exception e) {
			throw new RuntimeException("error on exec,flowTask:"+this,e);
		} 
		
		if(execChilds) {
			execAll(context,execParents, execChilds,getChilds(),false);
		}
	}

	private void execSelf(final FlowContext context) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException,
			InterruptedException, IOException {
		Assert.hasText(getProgramClass(),"programClass must be not empty");
		String flowCodeWithTaskCode = getFlowCode() + "/" + getTaskCode();
		if(context.getVisitedTaskCodes().contains(flowCodeWithTaskCode)) {
			return;
		}
		context.getVisitedTaskCodes().add(flowCodeWithTaskCode);
		
		TaskExecutor executor = (TaskExecutor)Class.forName(getProgramClass()).newInstance();
		execStartTime = new Date();
		evalGroovy(context,getBeforeGroovy());
		
		
//		Retry.retry(getRetryTimes(), getRetryInterval(), getTimeout(), new Callable<Object>() {
//			@Override
//			public Object call() throws Exception {
//				
//				return null;
//			}
//		});
		while(true) {
			long start = System.currentTimeMillis();
			try {
				status = "RUNNING";
				this.exception = null;
				
				if(getPreSleepTime() > 0) {
					Thread.sleep(getPreSleepTime());
				}
				
				
				notifyListeners();
				executor.exec(this,context.getParams(),context.getFlowEngine());
				
				waitIfRunning(executor, context, this);
				
				if(executor instanceof AsyncTaskExecutor) {
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
				this.exception = e;
				if(this.usedRetryTimes >= getRetryTimes()) {
					break;
				}
				Assert.isTrue(getRetryInterval() > 0," getRetryInterval() > 0 must be true");
				this.usedRetryTimes = this.usedRetryTimes + 1;
				notifyListeners();
				Thread.sleep(getRetryInterval());
			}finally {
				this.execCostTime = System.currentTimeMillis() - start;
				if(getTimeout() > 0) {
					if(this.execCostTime > getTimeout() ) {
						break;
					}
				}
			}
		}
		
		if(this.execResult != 0) {
			evalGroovy(context,getErrorGroovy());
		}
		
		if(executor instanceof AsyncTaskExecutor) {
			this.taskLog = IOUtils.toString(((AsyncTaskExecutor)executor).getLog(this, context.getParams()));
		}else {
			if(exception != null) {
				this.taskLog = ExceptionUtils.getFullStackTrace(exception);
			}
		}
		
		this.status = "END";
		notifyListeners();
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
		listenerable.notifyListeners(this, null);
	}

	public void addListener(Listener<FlowTask> t) {
		listenerable.addListener(t);
	}

	/**
	 * 通过计算得出的权重,如孩子越多,则权重越高,孩子自身的权重可以传递给父亲
	 * @return
	 */
	public int computePriority() {
		return getPriority();
	}
	
	@Override
	public int compareTo(FlowTask o) {
		return -Integer.compare(computePriority(), o.computePriority());
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
}
