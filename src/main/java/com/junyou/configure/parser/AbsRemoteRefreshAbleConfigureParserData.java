package com.junyou.configure.parser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.junyou.configure.loader.IConfigureLoader;
import com.junyou.configure.loader.RemoteConfigureLoaderData;
import com.junyou.configure.parser.AbsRefreshAbleConfigureParser;

/**
 * 远程可定时刷新配置解析器
 */
public abstract class AbsRemoteRefreshAbleConfigureParserData extends AbsRefreshAbleConfigureParser {

	@Autowired
	@Qualifier("remoteReaderData")
	private RemoteConfigureLoaderData configureLoader;
	
	@Override
	protected IConfigureLoader getLoader() {
		return configureLoader;
	}
	
}
