package com.game.easyexecutor.config;

import java.util.List;

import com.game.easyexecutor.Interceptor.IInterceptor;

public class EasyexecutorConfigManager implements IEasyexecutorMeta{

	private EasyexecutorConfig easyConfig;
	
	public EasyexecutorConfigManager(EasyexecutorConfig easyConfig) {
		this.easyConfig = easyConfig;
	}

	@Override
	public boolean isScanPackage(String packageName) {
		
		for(String s : easyConfig.getScanPackages()){
			if(packageName.startsWith(s)) return true;
		}
		return false;
		
	}

	@Override
	public List<IInterceptor> globalInterceptors() {
		return easyConfig.getGlobalInterceptors();
	}
	
}
