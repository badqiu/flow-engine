package com.github.flowengine.engine.task;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

import com.github.flowengine.engine.TaskExecResult;
import com.github.flowengine.engine.TaskExecutor;
import com.github.flowengine.model.FlowContext;
import com.github.flowengine.model.FlowTask;
import com.github.rapid.common.util.ScriptEngineUtil;

public class ScriptTaskExecutor implements TaskExecutor {

	private String lang;

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	@Override
	public TaskExecResult exec(FlowTask task, FlowContext flowContext)
			throws Exception {
		String lang = lookupLang(task);
		ScriptEngineUtil.eval(lang, task.getScript(), flowContext.getParams());
		return null;
	}

	private String lookupLang(FlowTask task) {
		String lang = this.lang;
		if(StringUtils.isBlank(lang)) {
			if(task != null && task.getProps() != null) {
				lang = (String) task.getProps().get("lang");
			}
		}
		Assert.hasText(lang, "'lang' props must be not empty");
		return lang;
	}

}
