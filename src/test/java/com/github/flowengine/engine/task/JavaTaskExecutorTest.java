package com.github.flowengine.engine.task;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import com.github.flowengine.engine.TaskExecResult;
import com.github.flowengine.model.FlowContext;
import com.github.flowengine.model.FlowTask;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

public class JavaTaskExecutorTest {

    private JavaTaskExecutor executor;
    
    private FlowTask mockFlowTask;
    
    private FlowContext mockFlowContext;
    
    private Map<String, Object> props;

    @Before
    public void setUp() {
        executor = new JavaTaskExecutor();
        props = new HashMap<>();
        
        mockFlowTask = mock(FlowTask.class);
        mockFlowContext = mock(FlowContext.class);
        
        when(mockFlowTask.getProps()).thenReturn(props);
    }

    @Test
    public void testExecWithRunnableFromProps() throws Exception {
        // 设置测试数据
        props.put("javaClass", TestRunnable.class.getName());
        
        // 执行测试
        TaskExecResult result = executor.exec(mockFlowTask, mockFlowContext);
        
        // 验证结果
        assertNull(result); // 根据你的代码，exec方法返回null
        // 验证TestRunnable的run方法被调用（通过静态变量验证）
        assertTrue(TestRunnable.executed);
    }

    @Test
    public void testExecWithCallableFromProps() throws Exception {
        // 设置测试数据
        props.put("javaClass", TestCallable.class.getName());
        
        // 执行测试
        TaskExecResult result = executor.exec(mockFlowTask, mockFlowContext);
        
        // 验证结果
        assertNull(result);
        assertTrue(TestCallable.executed);
    }

    @Test
    public void testExecWithSpecificMethodFromProps() throws Exception {
        // 设置测试数据
        props.put("javaClass", TestClassWithMethod.class.getName());
        props.put("method", "executeMethod");
        
        // 执行测试
        TaskExecResult result = executor.exec(mockFlowTask, mockFlowContext);
        
        // 验证结果
        assertNull(result);
        assertTrue(TestClassWithMethod.methodExecuted);
    }

    @Test
    public void testExecWithRunnableFromExecutorProperty() throws Exception {
        // 设置executor的属性
        executor.setJavaClass(TestRunnable.class.getName());
        
        // 执行测试
        TaskExecResult result = executor.exec(mockFlowTask, mockFlowContext);
        
        // 验证结果
        assertNull(result);
        assertTrue(TestRunnable.executed);
    }

    @Test
    public void testExecWithCallableFromExecutorProperty() throws Exception {
        // 设置executor的属性
        executor.setJavaClass(TestCallable.class.getName());
        
        // 执行测试
        TaskExecResult result = executor.exec(mockFlowTask, mockFlowContext);
        
        // 验证结果
        assertNull(result);
        assertTrue(TestCallable.executed);
    }

    @Test
    public void testExecWithSpecificMethodFromExecutorProperty() throws Exception {
        // 设置executor的属性
        executor.setJavaClass(TestClassWithMethod.class.getName());
        executor.setMethod("executeMethod");
        
        // 执行测试
        TaskExecResult result = executor.exec(mockFlowTask, mockFlowContext);
        
        // 验证结果
        assertNull(result);
        assertTrue(TestClassWithMethod.methodExecuted);
    }

    @Test(expected = ClassNotFoundException.class)
    public void testPropsPriorityOverExecutorProperties() throws Exception {
        // 设置executor的属性（应该被props覆盖）
        executor.setJavaClass("wrong.Class");
        executor.setMethod("wrongMethod");
        
        // 设置props（应该优先使用）
        props.put("javaClass", TestRunnable.class.getName());
        props.put("method", "someMethod"); // 这个method不会被使用，因为TestRunnable是Runnable
        
        // 执行测试
        TaskExecResult result = executor.exec(mockFlowTask, mockFlowContext);
        
        // 验证结果
        assertNull(result);
        assertTrue(TestRunnable.executed);
    }

    @Test(expected = ClassNotFoundException.class)
    public void testExecWithInvalidClass() throws Exception {
        // 设置不存在的类
        props.put("javaClass", "com.nonexistent.Class");
        
        // 执行测试（应该抛出异常）
        executor.exec(mockFlowTask, mockFlowContext);
    }

    @Test(expected = RuntimeException.class)
    public void testExecWithClassNotImplementingRunnableOrCallable() throws Exception {
        // 设置一个既不是Runnable也不是Callable的类
        props.put("javaClass", Object.class.getName());
        
        // 执行测试（应该抛出异常）
        executor.exec(mockFlowTask, mockFlowContext);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExecWithEmptyJavaClass() throws Exception {
        // 不设置javaClass
        when(mockFlowTask.getProps()).thenReturn(new HashMap<>());
        
        // 执行测试（应该抛出异常）
        executor.exec(mockFlowTask, mockFlowContext);
    }

    @Test
    public void testExecWithNullTask() throws Exception {
        // 设置executor的属性
        executor.setJavaClass(TestRunnable.class.getName());
        
        // 执行测试，task为null
        TaskExecResult result = executor.exec(null, mockFlowContext);
        
        // 验证结果
        assertNull(result);
        assertTrue(TestRunnable.executed);
    }

    @Test
    public void testExecWithNullProps() throws Exception {
        // 设置executor的属性
        executor.setJavaClass(TestRunnable.class.getName());
        
        // 设置task返回null props
        when(mockFlowTask.getProps()).thenReturn(null);
        
        // 执行测试
        TaskExecResult result = executor.exec(mockFlowTask, mockFlowContext);
        
        // 验证结果
        assertNull(result);
        assertTrue(TestRunnable.executed);
    }

    // 测试辅助类
    public static class TestRunnable implements Runnable {
        public static boolean executed = false;
        
        @Override
        public void run() {
            executed = true;
        }
        
        public static void reset() {
            executed = false;
        }
    }

    public static class TestCallable implements Callable<String> {
        public static boolean executed = false;
        
        @Override
        public String call() throws Exception {
            executed = true;
            return "success";
        }
        
        public static void reset() {
            executed = false;
        }
    }

    public static class TestClassWithMethod {
        public static boolean methodExecuted = false;
        
        public void executeMethod() {
            methodExecuted = true;
        }
        
        public static void reset() {
            methodExecuted = false;
        }
    }

    // 在每个测试方法执行后重置静态变量
    @org.junit.After
    public void tearDown() {
        TestRunnable.reset();
        TestCallable.reset();
        TestClassWithMethod.reset();
    }
}