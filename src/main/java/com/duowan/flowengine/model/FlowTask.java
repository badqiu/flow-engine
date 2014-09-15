package com.duowan.flowengine.model;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.io.IOUtils;
import org.springframework.util.Assert;

import com.duowan.flowengine.engine.AsyncTaskExecutor;
import com.duowan.flowengine.engine.TaskExecutor;
import com.duowan.flowengine.model.def.FlowTaskDef;
/**
 * 流程任务实例
 * @author badqiu
 *
 */
public class FlowTask extends FlowTaskDef{

	private String instanceId; //实例ID
	private String batchId; //任务执行批次ID,可以使用如( flow instanceId填充)
	
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
	private Throwable lastException;
	
	private Set<FlowTask> childs = new LinkedHashSet<FlowTask>();
	private Set<FlowTask> parents= new LinkedHashSet<FlowTask>();
	
	public FlowTask(String flowCode, String taskCode,String instanceId,String batchId) {
		super(flowCode, taskCode);
		this.instanceId = instanceId;
		this.batchId = batchId;
	}
	
	public void exec(final FlowContext context,final boolean execParents,final boolean execChilds) {
		execDepends(context, execParents, execChilds);
		
		try {
			execSelf(context);
		} catch (Exception e) {
			throw new RuntimeException("error on execSelf",e);
		} 
		
		execChilds(context, execParents, execChilds);
	}

	private void execChilds(final FlowContext context,
			final boolean execParents, final boolean execChilds) {
		if(execChilds) {
			Set<FlowTask> childs = getChildsTask();
			for(final FlowTask child : childs) {
				context.getExecutorService().execute(new Runnable() {
					@Override
					public void run() {
						try {
							child.exec(context,execParents,execChilds);
						}catch(Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		}
	}

	private void execDepends(final FlowContext context,
			final boolean execParents, final boolean execChilds) {
		Set<FlowTask> depends = getDependsTask();
		if(execParents && !depends.isEmpty()) {
			final CountDownLatch dependsCountDownLatch = new CountDownLatch(depends.size());
			for(final FlowTask depend : depends) {
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
			try {
				dependsCountDownLatch.await();
			} catch (InterruptedException e) {
				throw new RuntimeException("interrupt",e);
			}
		}
	}

	private void execSelf(final FlowContext context) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException,
			InterruptedException, IOException {
		if(context.getVisitedTaskCodes().contains(getTaskCode())) {
			return;
		}
		context.getVisitedTaskCodes().add(getTaskCode());
		
		final FlowTask flowTask = this;
		
		TaskExecutor executor = (TaskExecutor)Class.forName(getProgramClass()).newInstance();
		execStartTime = new Date();
		while(true) {
			try {
				status = "RUNNING";
				if(getSleepTime() > 0) {
					Thread.sleep(getSleepTime());
				}
				
				long start = System.currentTimeMillis();
				
//				if(executor.isAsync()) {
//					if(!executor.isRunning(flowTask, context.getParams())) {
//						executor.exec(flowTask,context.getParams());
//					}
//				}
				executor.exec(flowTask,context.getParams());
				
				waitIfRunning(executor, context, flowTask);
				
				if(executor instanceof AsyncTaskExecutor) {
					this.execResult = ((AsyncTaskExecutor)executor).getExitCode(flowTask, context.getParams());
				}else {
					this.execResult = 0;
				}
				
				if(execResult != 0) {
					throw new RuntimeException("execResult not zero,execResult:"+this.execResult);
				}
				
				this.execCostTime = System.currentTimeMillis() - start;
				break;
			}catch(Exception e) {
				this.lastException = e;
				this.usedRetryTimes = this.usedRetryTimes + 1;
				if(this.usedRetryTimes > getRetryTimes()) {
					break;
				}
				
				Assert.isTrue(getRetryInterval() > 0," getRetryInterval() > 0 must be true");
				status = "SLEEP";
				Thread.sleep(getRetryInterval());
			}
		}
		if(executor instanceof AsyncTaskExecutor) {
			this.taskLog = IOUtils.toString(((AsyncTaskExecutor)executor).getLog(flowTask, context.getParams()));
		}
		this.status = "END";
	}

	private void waitIfRunning(TaskExecutor executor,final FlowContext context,final FlowTask flowTask) throws InterruptedException {
		if(executor instanceof AsyncTaskExecutor) {
			while(((AsyncTaskExecutor)executor).isRunning(flowTask, context.getParams())) {
				Thread.sleep(1000 * 5);
			}
		}
	}
	
	/**
	 * 通过计算得出的权重,如孩子越多,则权重越高,孩子自身的权重可以传递给父亲
	 * @return
	 */
	public int computeWeight() {
		return getPriority();
	}

	private Set<FlowTask> getChildsTask() {
		return childs;
	}

	private Set<FlowTask> getDependsTask() {
		return parents;
	}

	public void addChild(FlowTask t) {
		childs.add(t);
	}

	public void addParent(FlowTask t) {
		parents.add(t);
	}
}
