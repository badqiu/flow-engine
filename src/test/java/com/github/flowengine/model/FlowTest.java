package com.github.flowengine.model;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.github.flowengine.engine.FlowEngine;
import com.github.flowengine.engine.task.NothingTaskExecutor;

public class FlowTest {

	private Flow f = new Flow();
	@Before
	public void setUp() {
		SystemOutTaskExecutor.execCount = 0;
		f.setFlowId("demo_flow");
		f.setMaxParallel(3);
		
		FlowTask startTask = new FlowTask("start");
		startTask.setScriptType(NothingTaskExecutor.class);
		f.addNode(startTask);
		
		for(int i = 0; i < 10; i++) {
			FlowTask t = new FlowTask("demo_task_"+i);
			t.setDepends("start"); //依赖start任务
			t.setScriptType(SystemOutTaskExecutor.class);
			t.setPriority(i);
			f.addNode(t);
		}
		
		//初始化Flow
		f.init();
		System.out.println("dump start ----------------------");
		System.out.println(f.toString());
		System.out.println("dump end ----------------------");
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
		assertEquals(SystemOutTaskExecutor.execCount,10);
	}
	
	@Test
	public void test() throws InterruptedException {
		Map params = new HashMap();
		FlowEngine engien = new FlowEngine();
		FlowContext context = engien.exec(f,params);
		//等待流程执行完成
		context.awaitTermination(1000, TimeUnit.HOURS);
		
		assertEquals(SystemOutTaskExecutor.execCount,10);
	}
	
	@Test
	public void testOnlyOneTask() throws InterruptedException {
		Map params = new HashMap();
		FlowEngine engien = new FlowEngine();
		FlowContext context = engien.exec(f,"demo_task_1",params);
		//等待流程执行完成
		context.awaitTermination(1000, TimeUnit.HOURS);
		
		assertEquals(SystemOutTaskExecutor.execCount,1);
	}
	
	@Test
	public void testByRootTask() throws InterruptedException {
		Map params = new HashMap();
		FlowEngine engien = new FlowEngine();
		FlowContext context = engien.exec(f,"start",params);
		//等待流程执行完成
		context.awaitTermination(1000, TimeUnit.HOURS);
		
		assertEquals(SystemOutTaskExecutor.execCount,10);
	}
	
	@Test
	public void testMultiParents() throws InterruptedException {
		f = new Flow();
		f.setMaxParallel(20);
		List<String> parents = new ArrayList();
		for(int i = 0; i < 10; i++) {
			String taskId = "parent-"+i;
			FlowTask parentTask = new FlowTask(taskId);
			parentTask.setScriptType(NothingTaskExecutor.class);
			f.addNode(parentTask);
			parents.add(taskId);
		}
		
		FlowTask t = new FlowTask("demo_task");
		t.setDepends(StringUtils.join(parents,","));
		t.setScriptType("groovy");
		t.setScript("exec_count.incrementAndGet();System.out.println('child exec before');Thread.sleep(1000);System.out.println('child exec after');");
		f.addNode(t);
		f.init();
		
		System.out.println("dump start ----------------------");
		System.out.println(f.toString());
		System.out.println("dump end ----------------------");
		
		
		Map params = new HashMap();
		AtomicInteger execCount = new AtomicInteger(0);
		params.put("exec_count", execCount);
		FlowEngine engien = new FlowEngine();
		FlowContext context = engien.exec(f,params);
		context.awaitTermination(100, TimeUnit.HOURS);
		
		assertEquals("only exec once",execCount.get(),1);
		
	}
	
	@After
	public void after() {
		System.out.println("----------------------");
	}

}
