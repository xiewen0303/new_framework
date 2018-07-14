package com.junyou.configure.parser;

import javax.annotation.PostConstruct;
import com.junyou.configure.export.impl.ConfigureContext;
import com.junyou.configure.loader.DirType;
import com.junyou.configure.loader.IConfigureLoader;
import com.junyou.log.GameLog;

/**
 * 解析配置表
 * @author wind
 * @date 2018年7月13日
 */
public abstract class AbsConfigureParser {
	
	protected String sign;

	public String _getSign() {
		return sign;
	}

	/**
	 * 初始化
	 * @param
	 */
	@PostConstruct
	public void init() {
		
		try{
			Object[] signInfo = getLoader().loadSign(getConfigureName(),DirType.GLOBAL);
			if(signInfo == null){
				return;
			}
			
			sign = (String)signInfo[0];
			String path = (String)signInfo[1];
			
//			byte[] data = getLoader().load(path);
			
			
			//TODO need modify
//			configureDataResolve(data);
			
		}catch(Exception e){
			GameLog.error(getConfigureName(), e);
			GameLog.error(getConfigureName()+" is null");
		}
		
	}
	
	/**
	 * 配置数据解析
	 * @param
	 */
	protected abstract void configureDataResolve(byte[] data);

	protected abstract String getConfigureName();
	
	/**
	 * 获取配置资源加载器
	 */
	protected abstract IConfigureLoader getLoader();
	
	/**
	 * 
	 * @param
	 */
	protected String getIdentity(){
		return ConfigureContext.CONFIGURE_IDENTITY.toString();
	}
	
	
}
