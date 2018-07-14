package com.game.easyexecutor.config;

import com.game.easyexecutor.Interceptor.IInterceptor;

/**
 * 拦截器
 */
public class InterceptorConfig {

	private IInterceptor interceptor;
	
	public InterceptorConfig(IInterceptor interceptor) {
		this.interceptor = interceptor;
	}

	public IInterceptor getInterceptor() {
		return interceptor;
	}
	
}
