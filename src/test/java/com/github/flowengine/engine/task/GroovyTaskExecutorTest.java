package com.github.flowengine.engine.task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.junit.Test;

import com.github.flowengine.model.FlowContext;
import com.github.flowengine.model.FlowTask;
import com.github.rapid.common.util.MapUtil;

public class GroovyTaskExecutorTest {

	GroovyTaskExecutor executor = new GroovyTaskExecutor();
	@Test
	public void test() throws Exception {
		FlowTask task = new FlowTask();
		task.setScript("System.out.println('hello by groovy')");
		executor.exec(task, new FlowContext());
	}
	
	@Test
	public void test2() throws Exception {
		FlowTask task = new FlowTask();
		task.setScript("println('''hello by groovy \n hibernate'''); println 'hello'");
		executor.exec(task, new FlowContext());
	}
	
	@Test
	public void testBinding() throws Exception {
		
		ScriptEngineManager mgr = new ScriptEngineManager();
		ScriptEngine engine = mgr.getEngineByName("groovy");
		
		List<Map> rows = new ArrayList<Map>();
		long count = 5000000;
		for(int i = 0; i < count; i++) {
			rows.add(MapUtil.newMap("name","badqiu"+i));
		}
		
		Bindings bindings = new IteratorBindings(rows);
//		bindings.put("name", "badqiu");
//		bindings.put("_bindings", bindings);
		System.out.println("start");
		long start = System.currentTimeMillis();
		engine.eval("Object _bindings = bindings; for(row in rows) {'hello:'+name + ' row.name='+row.name; if(_bindings.hasNext()) _bindings.next()}", bindings);
		long cost = System.currentTimeMillis() - start;
		System.out.println("cost:"+cost+" tps:"+(count * 1000 / cost));
	}
	
	
	public static class IteratorBindings implements Bindings,Iterator{
		private List<Map> rows = new ArrayList<Map>();
		private int index;
		private Map map = new HashMap();
		
		public IteratorBindings(List<Map> rows) {
			this.rows = rows;
		}
		
		@Override
		public int size() {
			return getCurrentMap().size();
		}

		private Map getCurrentMap() {
			return rows.get(index);
		}

		@Override
		public boolean isEmpty() {
			return getCurrentMap().isEmpty();
		}

		@Override
		public boolean containsValue(Object value) {
			return getCurrentMap().containsValue(value);
		}

		@Override
		public void clear() {
			getCurrentMap().clear();
		}

		@Override
		public Set<String> keySet() {
			return getCurrentMap().keySet();
		}

		@Override
		public Collection<Object> values() {
			return getCurrentMap().values();
		}

		@Override
		public Set<java.util.Map.Entry<String, Object>> entrySet() {
			return getCurrentMap().entrySet();
		}

		@Override
		public Object put(String name, Object value) {
			return getCurrentMap().put(name, value);
		}

		@Override
		public void putAll(Map<? extends String, ? extends Object> toMerge) {
			getCurrentMap().putAll(toMerge);
		}

		@Override
		public boolean containsKey(Object key) {
			if("rows".equals(key)) {
				return true;
			}
			if("bindings".equals(key)) {
				return true;
			}
			return getCurrentMap().containsKey(key);
		}

		@Override
		public Object get(Object key) {
			if("rows".equals(key)) {
				return rows;
			}
			if("bindings".equals(key)) {
				return this;
			}
			return getCurrentMap().get(key);
		}

		@Override
		public Object remove(Object key) {
			return getCurrentMap().remove(key);
		}
		
		@Override
		public boolean hasNext() {
			return index + 1 < rows.size();
		}

		@Override
		public Map next() {
			index++;
			return rows.get(index);
		}
	}
	
	
}
