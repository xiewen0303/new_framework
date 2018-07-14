package com.junyou.configure.parser.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.junyou.configure.loader.ClasspathConfigureLoader;
import com.junyou.configure.loader.IConfigureLoader;
import com.junyou.configure.parser.AbsConfigureParser;

/**
 * 单个配置文件类路径配置解析器
 */
public abstract class AbsClasspathConfigureParser extends AbsConfigureParser {

	@Autowired
	private ClasspathConfigureLoader configureLoader;
	
	@Override
	protected IConfigureLoader getLoader() {
		return configureLoader;
	}

}
