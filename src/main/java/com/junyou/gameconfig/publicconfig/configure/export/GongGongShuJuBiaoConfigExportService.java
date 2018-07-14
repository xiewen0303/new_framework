package com.junyou.gameconfig.publicconfig.configure.export;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.stereotype.Component;
import com.junyou.configure.parser.impl.AbsClasspathConfigureParser;
import com.junyou.gameconfig.constants.PublicConfigConstants;
import com.junyou.gameconfig.utils.GameConfigUtil;

/**
 * 
 * 公共数据表配置 
 */
@Component
public class GongGongShuJuBiaoConfigExportService extends  AbsClasspathConfigureParser {
	
	/**
	  * configFileName
	 */
	private String configureName = "GongGongShuJu.jat";
	
	private Map<String, AdapterPublicConfig> configs;
	
	
	@SuppressWarnings("unchecked")
	protected void configureDataResolve(byte[] data) {
		if(data==null)return; 
		Object obj = GameConfigUtil.getPublicResource(data);
		Map<String, AdapterPublicConfig> configs = new HashMap<>();
		Map<String, Object> tmp = (Map<String, Object>)obj;
		if (null != tmp) {
			for (Entry<String, Object> entry : tmp.entrySet()) {
				String key=entry.getKey();
				Map<String, Object> value=(Map<String, Object>)entry.getValue();
				AdapterPublicConfig config = GongGongShuJuConfigCreateFactory.createPublicConfig(key,value);
				if(config != null){
					configs.put(config.getId(),config);
				}
			}
		}
		this.configs = configs;
	}
	
	
	protected String getConfigureName() {
		return configureName;
	}
	
	/**
	 *  
	 * @param mod {@link PublicConfigConstants}
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <P> P loadPublicConfig(String mod){
		return (P)configs.get(mod);
	}
}