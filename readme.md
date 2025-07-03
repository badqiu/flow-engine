
# scriptType支持如下类型:
	cmd: 运行系统命令(默认值)
	subflow: 运行子流程,script填子流程名称
	http: 调用url(http get),script填url地址
	groovy: 运行groovy脚本, script填groovy脚本内容
	shell: 运行bash shell脚本, script填shell脚本内容
	nothing: 不做任何操作,用于创建虚拟节点,可以创建一个虚拟节点如dwd层，其它任务依赖这个，避免依赖某个dwd_具体任务，导致依赖变动


# 注册新的scriptType
	通过:com.github.flowengine.engine.FlowEngine.registerTaskExecutor(shortName,TaskExecutor) 注册

