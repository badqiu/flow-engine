package com.duowan.flowengine.model;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

import com.duowan.common.util.ScriptEngineUtil;
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
		if(execParents) {
			execAll(context,execParents, execChilds,getParentsTask(),true);
		}
		
		try {
			execSelf(context);
		} catch (Exception e) {
			throw new RuntimeException("error on exec,flowTask:"+this,e);
		} 
		
		if(execChilds) {
			execAll(context,execParents, execChilds,getChildsTask(),false);
		}
	}

	private void execSelf(final FlowContext context) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException,
			InterruptedException, IOException {
		Assert.hasText(getProgramClass(),"programClass must be not empty");
		if(context.getVisitedTaskCodes().contains(getTaskCode())) {
			return;
		}
		context.getVisitedTaskCodes().add(getTaskCode());
		
		final FlowTask flowTask = this;
		
		TaskExecutor executor = (TaskExecutor)Class.forName(getProgramClass()).newInstance();
		execStartTime = new Date();
		evalGroovy(context,getBeforeGroovy());
		while(true) {
			try {
				status = "RUNNING";
				if(getPreSleepTime() > 0) {
					Thread.sleep(getPreSleepTime());
				}
				
				long start = System.currentTimeMillis();
				
//				if(executor.isAsync()) {
//					if(!executor.isRunning(flowTask, context.getParams())) {
//						executor.exec(flowTask,context.getParams());
//					}
//				}
				executor.exec(flowTask,context.getParams(),context.getFlowEngine());
				
				waitIfRunning(executor, context, flowTask);
				
				if(executor instanceof AsyncTaskExecutor) {
					this.execResult = ((AsyncTaskExecutor)executor).getExitCode(flowTask, context.getParams());
				}else {
					this.execResult = 0;
				}
				
				if(execResult != 0) {
					throw new RuntimeException("execResult not zero,execResult:"+this.execResult);
				}
				
				evalGroovy(context,getAfterGroovy());
				
				this.execCostTime = System.currentTimeMillis() - start;
				break;
			}catch(Exception e) {
				this.lastException = e;
				if(this.usedRetryTimes >= getRetryTimes()) {
					break;
				}
				
				Assert.isTrue(getRetryInterval() > 0," getRetryInterval() > 0 must be true");
				status = "SLEEP";
				this.usedRetryTimes = this.usedRetryTimes + 1;
				Thread.sleep(getRetryInterval());
			}
		}
		
		if(this.lastException != null) {
			evalGroovy(context,getErrorGroovy());
		}
		
		if(executor instanceof AsyncTaskExecutor) {
			this.taskLog = IOUtils.toString(((AsyncTaskExecutor)executor).getLog(flowTask, context.getParams()));
		}
		
		this.status = "END";
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
	
	/**
	 * 通过计算得出的权重,如孩子越多,则权重越高,孩子自身的权重可以传递给父亲
	 * @return
	 */
	public int computeWeight() {
		return getPriority();
	}

	public Set<FlowTask> getChildsTask() {
		return childs;
	}

	public Set<FlowTask> getParentsTask() {
		return parents;
	}

	public void addChild(FlowTask t) {
		childs.add(t);
	}

	public void addParent(FlowTask t) {
		parents.add(t);
	}
	
	
	
	public static void execAll(final FlowContext context, final boolean execParents,final boolean execChilds, Collection<FlowTask> tasks,boolean waitExecEnd) {
		if(CollectionUtils.isEmpty(tasks)) {
			return;
		}
		
		final CountDownLatch dependsCountDownLatch = new CountDownLatch(tasks.size());
		for(final FlowTask depend : tasks) {
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
		if(waitExecEnd) {
			try {
				dependsCountDownLatch.await();
			} catch (InterruptedException e) {
				throw new RuntimeException("interrupt",e);
			}
		}
	}
}
