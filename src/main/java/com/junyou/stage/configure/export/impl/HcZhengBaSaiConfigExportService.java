package com.junyou.stage.configure.export.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.junyou.configure.parser.impl.AbsClasspathConfigureParser;
import com.junyou.constants.GameConstants;
import com.junyou.gameconfig.utils.ConfigAnalysisUtils;
import com.junyou.gameconfig.utils.GameConfigUtil;
import com.junyou.stage.configure.HcZhengBaSaiConfig;
import com.junyou.utils.collection.ReadOnlyMap;
import com.junyou.utils.common.CovertObjectUtil;

@Service
public class HcZhengBaSaiConfigExportService extends AbsClasspathConfigureParser {
	/**
	 * configFileName
	 */
	private String configureName = "YunGongZhiZhan.jat";

	private Map<Integer, HcZhengBaSaiConfig> configMap; 
	
	private HcZhengBaSaiConfig hcZhengBaSaiConfig;  //就一张地图

	private HcZhengBaSaiConfig createHcZbsConfig(Map<String, Object> tmp) {
		HcZhengBaSaiConfig config = new HcZhengBaSaiConfig();
		config.setMap(CovertObjectUtil.object2int(tmp.get("map")));

		String flag = CovertObjectUtil.object2String(tmp.get("zuobiao"));
		String[] zuobiao = flag.split(GameConstants.CONFIG_SUB_SPLIT_CHAR);
		config.setZuobiao(new int[] { Integer.parseInt(zuobiao[0]), Integer.parseInt(zuobiao[1]) });

		String fuhuo1Str = CovertObjectUtil.object2String(tmp.get("fuhuo1"));
		String[] fuhuo1 = fuhuo1Str.split(GameConstants.CONFIG_SUB_SPLIT_CHAR);
		String fuhuo2Str = CovertObjectUtil.object2String(tmp.get("fuhuo2"));
		String[] fuhuo2 = fuhuo2Str.split(GameConstants.CONFIG_SUB_SPLIT_CHAR);
		int[] fuhu1 = new int[]{Integer.parseInt(fuhuo1[0]),Integer.parseInt(fuhuo1[1])};
		int[] fuhu2 = new int[]{Integer.parseInt(fuhuo2[0]),Integer.parseInt(fuhuo2[1])};
		Object[] gfh = new Object[]{fuhu1,fuhu2};
		config.setGongfuhuo(gfh);
		
		String fuhuo3Str = CovertObjectUtil.object2String(tmp.get("fuhuo3"));
		String[] fuhuo3 = fuhuo3Str.split(GameConstants.CONFIG_SUB_SPLIT_CHAR);
		String fuhuo4Str = CovertObjectUtil.object2String(tmp.get("fuhuo4"));
		String[] fuhuo4 = fuhuo4Str.split(GameConstants.CONFIG_SUB_SPLIT_CHAR);
		int[] fuhu3 = new int[]{Integer.parseInt(fuhuo3[0]),Integer.parseInt(fuhuo3[1])};
		int[] fuhu4 = new int[]{Integer.parseInt(fuhuo4[0]),Integer.parseInt(fuhuo4[1])};
		Object[] sfh = new Object[]{fuhu3,fuhu4};
		config.setShoufuhuo(sfh);

		config.setExp(CovertObjectUtil.object2Long(tmp.get("exp")));
		config.setNeedlevel(CovertObjectUtil.object2int(tmp.get("needlevel")));
		config.setZhenqi(CovertObjectUtil.object2int(tmp.get("zhenqi")));

		config.setAddexp(CovertObjectUtil.object2Float(tmp.get("addexp")));
		config.setAddzhenqi(CovertObjectUtil.object2Float(tmp.get("addzhenqi")));
		Map<String, Integer> items = ConfigAnalysisUtils.getConfigMap(CovertObjectUtil.object2String(tmp.get("jiangitem")));
		config.setJiangitem(items);

		Map<String, Long> attrs = ConfigAnalysisUtils.setAttributeVal(tmp);
		config.setAttrs(new ReadOnlyMap<>(attrs));
		return config;
	}

	@Override
	protected void configureDataResolve(byte[] data) {
		Object[] dataList = GameConfigUtil.getResource(data);
		Map<Integer, HcZhengBaSaiConfig> configMap = new HashMap<>();
		for (Object obj : dataList) {
			Map<String, Object> tmp = (Map<String, Object>) obj;
			if (null != tmp) {
				HcZhengBaSaiConfig config = createHcZbsConfig(tmp);
				this.hcZhengBaSaiConfig  = config;
			}
		}
	}

	@Override
	protected String getConfigureName() {
		return configureName;
	}
    
	public HcZhengBaSaiConfig  loadPublicConfig(){
		
		return this.hcZhengBaSaiConfig;
	}
	

}
