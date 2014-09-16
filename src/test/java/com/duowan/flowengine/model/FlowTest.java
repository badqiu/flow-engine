package com.duowan.flowengine.model;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

import com.duowan.flowengine.engine.FlowEngine;
import com.duowan.flowengine.engine.task.NothingTaskExecutor;
import com.duowan.flowengine.model.def.FlowDef;
import com.duowan.flowengine.model.def.FlowTaskDef;

public class FlowTest {

	private Flow f = new Flow();
	@Before
	public void setUp() {
		f.setFlowCode("demo_flow");
		f.setMaxParallel(3);
		
		FlowTask startTask = new FlowTask("start");
		startTask.setProgramClass(NothingTaskExecutor.class);
		f.addFlowTask(startTask);
		
		for(int i = 0; i < 10; i++) {
			FlowTask taskDef = new FlowTask("demo_task_"+i);
			taskDef.setDepends("start"); //依赖start任务
			taskDef.setProgramClass(SystemOutTaskExecutor.class);
			f.addFlowTask(taskDef);
		}
		
		//初始化Flow
		f.init();
	}
	
	@Test
	public void testWithNoParentsTasks() throws InterruptedException {
		//流程执行参数
		Map params = new HashMap();
		
		// 创建流程执行引擎,并从没有任何依赖的入口任务开始执行
		FlowEngine engien = new FlowEngine();
		FlowContext context = engien.exec(f, f.getNoParentsTasks(), params);
		
		//等待流程执行完成
		context.awaitTermination(1000, TimeUnit.HOURS);
	}
	
	@Test
	public void test() {
		Map params = new HashMap();
		FlowEngine engien = new FlowEngine();
		engien.exec(f,params);
	}

}
