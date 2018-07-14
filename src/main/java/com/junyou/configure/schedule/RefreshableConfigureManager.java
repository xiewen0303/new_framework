package com.junyou.configure.schedule;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.junyou.configure.parser.AbsPlatformAndServerConfigure;


/**
 * 热发布配置读取
 */
public class RefreshableConfigureManager {
	
	private static ConcurrentMap<String,AbsPlatformAndServerConfigure> configures = new ConcurrentHashMap<String,AbsPlatformAndServerConfigure>();
	
	public static void addConfig(AbsPlatformAndServerConfigure config){
		configures.put(config.getSortName(), config);
	}
	
	public static int refresh(String sortName){
		AbsPlatformAndServerConfigure config = configures.get(sortName);
		if(config != null){
			return config.versionRefresh();
		}
		return -3;
	}
	
}
