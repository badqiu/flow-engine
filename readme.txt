
scriptType支持如下类型:
		cmd: 运行系统命令(默认值)
		subflow: 运行子流程,script填子流程名称
		http: 调用url(http get),script填url地址
		groovy: 运行groovy脚本
		nothing: 不做任何操作,用于创建虚拟节点

注册新的scriptType
	通过:com.github.flowengine.engine.FlowEngine.registerTaskExecutor(shortName,TaskExecutor) 注册

