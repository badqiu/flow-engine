package com.github.flowengine.engine.task;

import com.github.flowengine.engine.TaskExecutor;

public class GroovyTaskExecutor extends ScriptTaskExecutor implements TaskExecutor {

	public GroovyTaskExecutor() {
		setLang("groovy");
	}

}
