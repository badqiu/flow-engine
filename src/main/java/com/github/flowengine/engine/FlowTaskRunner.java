package com.github.flowengine.engine;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.flowengine.engine.FlowEngine;
import com.github.flowengine.model.Flow;
import com.github.flowengine.model.FlowContext;
import com.github.flowengine.model.FlowTask;

public class FlowTaskRunner {
	
    private final int maxConcurrency;
    private final ExecutorService taskExecutor;
    private final AtomicInteger runningTasks = new AtomicInteger(0);
    private final BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();
    
    public FlowTaskRunner(int maxConcurrency) {
        this.maxConcurrency = maxConcurrency;
        this.taskExecutor = new ThreadPoolExecutor(
            maxConcurrency, maxConcurrency, 0L, TimeUnit.MILLISECONDS, taskQueue);
    }
    
    public void schedule(List<FlowTask> tasks,FlowContext context)  {
        try {
			schedule0(tasks, context);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
    }

	private void schedule0(List<FlowTask> tasks, FlowContext context) throws InterruptedException {
		// 1. 初始化入度图和就绪队列
        Map<FlowTask, Integer> indegreeMap = new HashMap<>();
        Queue<FlowTask> readyQueue = new LinkedList<>();
        
        initIndegreeMap(tasks, indegreeMap, readyQueue);
        
        // 2. 创建完成队列
        BlockingQueue<FlowTask> completionQueue = new LinkedBlockingQueue<>();
        
        // 3. 调度主循环
        int totalTasks = tasks.size();
        int completedTasks = 0;
        
        while (completedTasks < totalTasks) {
            // 提交就绪任务（不超过最大并发数）
            while (!readyQueue.isEmpty() && runningTasks.get() < maxConcurrency) {
                FlowTask task = readyQueue.poll();
                runningTasks.incrementAndGet();
                
                taskExecutor.execute(() -> {
                    try {
                        task.exec(context, false);
                    } finally {
                        completionQueue.offer(task);
                    }
                });
            }
            
            // 等待任务完成
            FlowTask completed = completionQueue.take();
            runningTasks.decrementAndGet();
            completedTasks++;
            
            updateChildsIndegree(indegreeMap, readyQueue, completed);
        }
        
        // 关闭线程池
        taskExecutor.shutdown();
	}

	private void initIndegreeMap(List<FlowTask> tasks, Map<FlowTask, Integer> indegreeMap, Queue<FlowTask> readyQueue) {
		for (FlowTask task : tasks) {
            int indegree = task.getParents().size();
            indegreeMap.put(task, indegree);
            if (indegree == 0) {
                readyQueue.add(task);
            }
        }
	}

	private void updateChildsIndegree(Map<FlowTask, Integer> indegreeMap, Queue<FlowTask> readyQueue,
			FlowTask completed) {
		// 更新子任务入度
		for (FlowTask child : completed.getChilds()) {
		    Integer indegree = indegreeMap.get(child);
			int newIndegree = indegree - 1;
		    indegreeMap.put(child, newIndegree);
		    
		    if (newIndegree == 0) {
		        readyQueue.add(child);
		    }
		}
	}
}

// 使用示例
class DemoFlowTaskRunner {
	
	public static FlowTask newFlowTask(String id) {
		FlowTask r = new FlowTask(id);
		r.setScriptType("cmd");
		r.setScript("cmd /c echo helloworld-"+id);
		return r;
	}
	
    public static void main(String[] args) throws InterruptedException {
        // 创建任务节点
        FlowTask A = newFlowTask("A");
        FlowTask B = newFlowTask("B");
        FlowTask C = newFlowTask("C");
        FlowTask D = newFlowTask("D");
        
        // 构建依赖关系
        A.addChild(B);
        A.addChild(C);
        B.addChild(D);
        C.addChild(D);
        
        // 创建任务列表
        List<FlowTask> tasks = Arrays.asList(A, B, C, D);
        
        // 启动调度器（最大并发数=2）
        FlowTaskRunner scheduler = new FlowTaskRunner(2);
        FlowContext context = new FlowContext();
        FlowEngine flowEngine = new FlowEngine();
        context.setFlow(new Flow());
        context.setFlowEngine(flowEngine);
        
		scheduler.schedule(tasks,context);
    }
}