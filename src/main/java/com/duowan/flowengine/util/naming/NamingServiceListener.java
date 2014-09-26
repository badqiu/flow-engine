package com.duowan.flowengine.util.naming;

import java.util.List;

public interface NamingServiceListener {

	/**
     * 当收到服务变更通知时触发。
     * 
     */
    public void notify(List<String> urls);
    
}
