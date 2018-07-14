package com.kernel.pool.executor;

import java.util.Map;


public interface IBusinessExecutor {

	public void execute(Runnable runnable,RouteInfo routeInfo);
	
	public void addExecutorGroup(String name,int size);
	
	public Map<String, Map<String, Integer>> getExecutorInfos();
}
