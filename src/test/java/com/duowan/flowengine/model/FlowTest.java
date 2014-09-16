package com.duowan.flowengine.model;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.duowan.flowengine.engine.FlowEngine;
import com.duowan.flowengine.engine.task.NothingTaskExecutor;
import com.duowan.flowengine.model.def.FlowDef;
import com.duowan.flowengine.model.def.FlowTaskDef;

public class FlowTest {

	private FlowDef def = new FlowDef();
	Flow f = null;
	@Before
	public void setUp() {
		def.setFlowCode("demo_flow");
		def.setMaxParallel(3);
		f = def.newInstance();
		
		FlowTaskDef startTask = new FlowTaskDef(def.getFlowCode(),"start");
		startTask.setProgramClass(NothingTaskExecutor.class);
		f.addFlowTaskWithDepends(startTask.newInstance(f.getInstanceId()));
		
		for(int i = 0; i < 10; i++) {
			FlowTaskDef taskDef = new FlowTaskDef(def.getFlowCode(),"demo_task_"+i);
			taskDef.setDepends("start");
			taskDef.setProgramClass(SystemOutTaskExecutor.class);
			f.addFlowTaskWithDepends(taskDef.newInstance(f.getInstanceId()));
		}
	}
	
	@Test
	public void test() {
		Map params = new HashMap();
		FlowEngine engien = new FlowEngine();
		engien.exec(f,params);
	}
	
	@Test
	public void testWithNoParentsTasks() {
		Map params = new HashMap();
		FlowEngine engien = new FlowEngine();
		engien.exec(f, f.getNoParentsTasks(), params);
	}

}
