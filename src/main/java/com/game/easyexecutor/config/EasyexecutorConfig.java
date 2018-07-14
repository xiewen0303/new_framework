package com.game.easyexecutor.config;

import java.util.ArrayList;
import java.util.List;

import com.game.easyexecutor.Interceptor.IInterceptor;

/**
 * 执行协议跳转到Action时过滤器配置
 *
 */
public class EasyexecutorConfig {
	
	/**
	 * 过滤包名
	 */
	private List<String> scanPackages = new ArrayList<String>();
	
	/**
	 * 拦截器具体执行类
	 */
	private List<IInterceptor> globalInterceptors = new ArrayList<>();
	
	public List<String> getScanPackages(){
		return scanPackages;
	}
	
	public void addScanPackage(String scanPackage){
		scanPackages.add(scanPackage);
	}
	
	public void addGlobalInterceptor(IInterceptor interceptor){
		globalInterceptors.add(interceptor);
	}

	public List<IInterceptor> getGlobalInterceptors() {
		return globalInterceptors;
	}
	
}
