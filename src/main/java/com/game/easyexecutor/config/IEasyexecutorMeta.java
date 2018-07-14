package com.game.easyexecutor.config;

import java.util.List;

import com.game.easyexecutor.Interceptor.IInterceptor;

/**
 * 拦截匹配器
 */
public interface IEasyexecutorMeta {
	
	/**
	 * 包名是否符合
	 * @param packageName
	 * @return
	 */
	public boolean isScanPackage(String packageName);
	/**
	 * 拦截器类
	 * @return
	 */
	public List<IInterceptor> globalInterceptors();
}
