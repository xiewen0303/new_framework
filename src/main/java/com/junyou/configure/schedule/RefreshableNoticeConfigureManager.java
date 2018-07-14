package com.junyou.configure.schedule;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import com.junyou.configure.parser.impl.AbsRefreshByPS;

/**
 * 公告配置读取
 */
public class RefreshableNoticeConfigureManager {
	private static ConcurrentMap<String,AbsRefreshByPS> configures = new ConcurrentHashMap<String,AbsRefreshByPS>();
	
	public static void addConfig(AbsRefreshByPS config){
		configures.put(config.getSortName(), config);
	}
	
	public static int refresh(String sortName){
		AbsRefreshByPS config = configures.get(sortName);
		if(config != null){
			return config.versionRefresh();
		}
		return -3;
	}
	
}
