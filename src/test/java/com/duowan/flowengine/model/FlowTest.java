package com.duowan.flowengine.model;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.duowan.flowengine.engine.FlowEngine;
import com.duowan.flowengine.engine.task.NothingTaskExecutor;

public class FlowTest {

	private Flow f = new Flow();
	@Before
	public void setUp() {
		f.setFlowCode("demo_flow");
		f.setMaxParallel(3);
		
		FlowTask startTask = new FlowTask("start");
		startTask.setProgramClass(NothingTaskExecutor.class);
		f.addNode(startTask);
		
		for(int i = 0; i < 10; i++) {
			FlowTask t = new FlowTask("demo_task_"+i);
			t.setDepends("start"); //依赖start任务
			t.setProgramClass(SystemOutTaskExecutor.class);
			t.setPriority(i);
			f.addNode(t);
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
		FlowContext context = engien.exec(f, f.getNoDependNodes(), params);
		
		//等待流程执行完成
		context.awaitTermination(1000, TimeUnit.HOURS);
	}
	
	@Test
	public void test() throws InterruptedException {
		Map params = new HashMap();
		FlowEngine engien = new FlowEngine();
		FlowContext context = engien.exec(f,params);
		//等待流程执行完成
		context.awaitTermination(1000, TimeUnit.HOURS);
	}
	
	@After
	public void after() {
		System.out.println("----------------------");
	}

}
