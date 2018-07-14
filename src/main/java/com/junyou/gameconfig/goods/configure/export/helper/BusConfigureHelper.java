package com.junyou.gameconfig.goods.configure.export.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.junyou.bus.role.service.UserRoleService;
import com.junyou.gameconfig.goods.configure.export.EquipOrderConfigExportService;
import com.junyou.gameconfig.goods.configure.export.GoodsConfigExportService;
import com.junyou.gameconfig.goods.configure.export.GoodsTypeOrderConfigExportService;
import com.junyou.gameconfig.publicconfig.configure.export.GongGongShuJuBiaoConfigExportService;
import com.junyou.gameconfig.role.configure.export.PingbiziConfigExportService;

@Component
public class BusConfigureHelper {
 
	private static GoodsConfigExportService goodsConfigExportService;
	private static GoodsTypeOrderConfigExportService goodsTypeOrderConfigExportService;
	private static EquipOrderConfigExportService equipOrderConfigExportService;
	private static PingbiziConfigExportService PingbiziConfigExportService;
	private static GongGongShuJuBiaoConfigExportService gongGongShuJuBiaoConfigExportService;
//	private static SuiJiShuXingConfigExportService suiJiShuXingConfigExportService;
//	private static TaoZhuangZhuShenConfigExportService taoZhuangZhuShenConfigExportService;
	
	private static UserRoleService roleExportService;
	
	public static UserRoleService getRoleExportService() {
		return roleExportService;
	}

	
	
	@Autowired
	public void setUserRoleService(UserRoleService roleExportService) {
		BusConfigureHelper.roleExportService = roleExportService;
	}
	
//
//	public static SuiJiShuXingConfigExportService getSuiJiShuXingConfigExportService() {
//		return suiJiShuXingConfigExportService;
//	}
//	
//	@Autowired
//	public void setSuiJiShuXingConfigExportService(
//			SuiJiShuXingConfigExportService suiJiShuXingConfigExportService) {
//		BusConfigureHelper.suiJiShuXingConfigExportService = suiJiShuXingConfigExportService;
//	}

	public static GongGongShuJuBiaoConfigExportService getGongGongShuJuBiaoConfigExportService() {
		return gongGongShuJuBiaoConfigExportService;
	}
	
	@Autowired
	public void setGongGongShuJuBiaoConfigExportService(
			GongGongShuJuBiaoConfigExportService gongGongShuJuBiaoConfigExportService) {
		BusConfigureHelper.gongGongShuJuBiaoConfigExportService = gongGongShuJuBiaoConfigExportService;
	}

	public static PingbiziConfigExportService getPingbiziConfigExportService() {
		return PingbiziConfigExportService;
	}

	@Autowired
	public void setPingbiziConfigExportService(
			PingbiziConfigExportService pingbiziConfigExportService) {
		PingbiziConfigExportService = pingbiziConfigExportService;
	}

	@Autowired
	public void setGoodsConfigExportService(GoodsConfigExportService goodsConfigExportService) {
		BusConfigureHelper.goodsConfigExportService = goodsConfigExportService;
	}
	
	@Autowired
	public void setEquipOrderConfigExportService(EquipOrderConfigExportService equipOrderConfigExportService) {
		BusConfigureHelper.equipOrderConfigExportService = equipOrderConfigExportService;
	}
	
	@Autowired
	public void setGoodsTypeOrderConfigExportService(GoodsTypeOrderConfigExportService goodsTypeOrderConfigExportService){
		BusConfigureHelper.goodsTypeOrderConfigExportService = goodsTypeOrderConfigExportService;
	}
//	
//	@Autowired
//	public void setTaoZhuangZhuShenConfigExportService(TaoZhuangZhuShenConfigExportService taoZhuangZhuShenConfigExportService){
//		BusConfigureHelper.taoZhuangZhuShenConfigExportService = taoZhuangZhuShenConfigExportService;
//	}
	
	public static GoodsConfigExportService getGoodsConfigExportService() {
		return goodsConfigExportService;
	}

	public static GoodsTypeOrderConfigExportService getGoodsTypeOrderConfigExportService() {
		return goodsTypeOrderConfigExportService;
	}

	public static EquipOrderConfigExportService getEquipOrderConfigExportService() {
		return equipOrderConfigExportService;
	}
//	
//	public static TaoZhuangZhuShenConfigExportService getTaoZhuangZhuShenConfigExportService() {
//		return taoZhuangZhuShenConfigExportService;
//	}
}
