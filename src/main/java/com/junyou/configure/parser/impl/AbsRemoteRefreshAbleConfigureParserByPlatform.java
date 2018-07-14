package com.junyou.configure.parser.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.junyou.configure.loader.IConfigureLoader;
import com.junyou.configure.loader.RemoteConfigureLoaderByPlatform;
import com.junyou.configure.parser.AbsRefreshAbleConfigureParser;

/**
 * 远程可定时刷新配置解析器
 */
public abstract class AbsRemoteRefreshAbleConfigureParserByPlatform extends AbsRefreshAbleConfigureParser {

	@Autowired
	@Qualifier("remoteReaderByPlatform")
	private RemoteConfigureLoaderByPlatform configureLoader;
	
	@Override
	protected IConfigureLoader getLoader() {
		return configureLoader;
	}
	
}
