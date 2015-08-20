package com.github.flowengine.engine.task;

import com.github.flowengine.engine.TaskExecutor;

public class JavaScriptTaskExecutor extends ScriptTaskExecutor implements TaskExecutor {

	public JavaScriptTaskExecutor() {
		setLang("javascript");
	}

}
