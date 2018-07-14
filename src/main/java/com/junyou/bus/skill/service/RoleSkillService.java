package com.junyou.bus.skill.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.junyou.bus.account.service.RoleAccountService;
import com.junyou.bus.bag.BagSlots;
import com.junyou.bus.bag.GoodsSource;
import com.junyou.bus.bag.export.RoleBagExportService;
import com.junyou.bus.bag.export.RoleItemExport;
import com.junyou.bus.role.export.RoleWrapper;
import com.junyou.bus.role.service.UserRoleService;
import com.junyou.bus.skill.dao.RoleSkillDao;
import com.junyou.bus.skill.entity.RoleSkill;
import com.junyou.cmd.ClientCmdType;
import com.junyou.cmd.InnerCmdType;
import com.junyou.constants.GameConstants;
import com.junyou.err.AppErrorCode;
import com.junyou.event.SkillLogEvent;
import com.junyou.event.publish.GamePublishEvent;
import com.junyou.gameconfig.goods.configure.export.GoodsConfig;
import com.junyou.gameconfig.goods.configure.export.GoodsConfigExportService;
import com.junyou.gameconfig.utils.GoodsCategory;
import com.junyou.log.GameLog;
import com.junyou.log.LogPrintHandle;
import com.junyou.public_.share.export.PublicRoleStateExportService;
import com.junyou.stage.configure.SkillFireType;
import com.junyou.stage.configure.export.impl.BeiDongSkillConfig;
import com.junyou.stage.configure.export.impl.BeiDongSkillConfigExportService;
import com.junyou.stage.configure.export.impl.BeiDongSkillXueXiConfig;
import com.junyou.stage.configure.export.impl.GuildBeiDongSkillConfig;
import com.junyou.stage.configure.export.impl.GuildBeiDongSkillConfigExportService;
import com.junyou.stage.configure.export.impl.GuildBeiDongSkillXueXiConfig;
import com.junyou.stage.configure.export.impl.SkillConfig;
import com.junyou.stage.configure.export.impl.SkillConfigExportService;
import com.junyou.stage.configure.export.impl.SkillXueXiConfig;
import com.junyou.stage.configure.export.impl.TangBaoSkillConfig;
import com.junyou.stage.configure.export.impl.TangBaoSkillConfigExportService;
import com.junyou.stage.configure.export.impl.TangBaoSkillXueXiConfig;
import com.junyou.stage.model.core.stage.ElementType;
import com.junyou.stage.model.core.stage.IStage;
import com.junyou.stage.model.element.role.IRole;
import com.junyou.stage.model.skill.SkillManager;
import com.junyou.stage.model.stage.StageManager;
import com.junyou.utils.common.ObjectUtil;
import com.kernel.data.dao.IQueryFilter;
import com.kernel.gen.id.IdFactory;
import com.kernel.gen.id.ServerIdType;
import com.kernel.tunnel.bus.BusMsgSender;

@Service
public class RoleSkillService { 

	@Autowired
	private BeiDongSkillConfigExportService beiDongSkillXueXiExportService;
	@Autowired
	private RoleSkillDao roleSkillDao;
	@Autowired
	private UserRoleService roleExportService;
	@Autowired
	private SkillConfigExportService skillConfigExportService;
	@Autowired
	private BeiDongSkillConfigExportService beiDongSkillConfigExportService;
	@Autowired
	private TangBaoSkillConfigExportService tangBaoSkillConfigExportService ;
	@Autowired
	private RoleBagExportService roleBagExportService;
	@Autowired
	private GoodsConfigExportService goodsConfigExportService;
	@Autowired
	private RoleAccountService accountExportService;
	@Autowired
	private GuildBeiDongSkillConfigExportService guildBeiDongSkillConfigExportService;
	@Autowired
	private PublicRoleStateExportService publicRoleStateExportService;

	public List<RoleSkill> initRoleSkill(Long userRoleId) {
		return roleSkillDao.initRoleSkill(userRoleId);
	}
	
	public List<RoleSkill> loadAllRoleSkill(Long userRoleId) {
		return roleSkillDao.cacheLoadAll(userRoleId);
	}
	/**
	 * 上线检测是否有该学的技能未学习
	 * @param userRoleId
	 */
	public void onlineHandle(Long userRoleId){
		RoleWrapper role = roleExportService.getLoginRole(userRoleId);
		if(role == null){
			return;//角色不存在
		}
		/**自动学习*/
		for (SkillConfig skillConfig : skillConfigExportService.getAutoConfig(role.getConfigId())) {
			RoleSkill skill = getSkill(userRoleId, skillConfig.getId());
			SkillXueXiConfig skillXueXiConfig = skillConfig.getXuexiConfig(1);
			if(skillXueXiConfig == null){
				GameLog.error("skillXueXiConfig is null,leve="+1);
				continue;
			}
			if(skill == null ){
				if((skillConfig.getSkillFireType() == SkillFireType.NORMAL && skillXueXiConfig.getMinLevel() <= role.getLevel())|| skillConfig.getSkillFireType() != SkillFireType.NORMAL){
					skill = createRoleSkill(userRoleId, skillConfig.getId(),GameConstants.SKILL_TYPE_ZHUDONG);
					roleSkillDao.cacheInsert(skill, userRoleId);
				}  
			}
		}
		
		/**设置最大熟练*/
		List<RoleSkill> skills = getSkillsByType(userRoleId, GameConstants.SKILL_TYPE_ZHUDONG);
		if(skills != null && skills.size() > 0){
			for (RoleSkill skill : skills) {
				SkillConfig config = skillConfigExportService.loadById(skill.getSkillId());
				if(config != null){
					SkillXueXiConfig xuexiConfig = config.getXuexiConfig(skill.getLevel() + 1);
					if(xuexiConfig != null){
						skill.setMaxShulian(xuexiConfig.getShulian());
					}
				}
			}
		}
		skills = getSkillsByType(userRoleId, GameConstants.SKILL_TYPE_YAOSHEN);
		if(skills != null && skills.size() > 0){
			for (RoleSkill skill : skills) {
				SkillConfig config = skillConfigExportService.loadById(skill.getSkillId());
				if(config != null){
					SkillXueXiConfig xuexiConfig = config.getXuexiConfig(skill.getLevel() + 1);
					if(xuexiConfig != null){
						skill.setMaxShulian(xuexiConfig.getShulian());
					}
				}
			}
		}
	}
	
	/**
	 * 学习主动技能
	 * @param userRoleId
	 * @param skillId
	 * @param guid
	 * @return
	 */
	public Object[] learnSkill(Long userRoleId,String skillId,Long guid){
		RoleWrapper role = roleExportService.getLoginRole(userRoleId);
		if(role == null){
			return AppErrorCode.ROLE_IS_NOT_EXIST;//角色不存在
		}
		SkillConfig skill = skillConfigExportService.loadById(skillId);
		if(skill == null){
			return AppErrorCode.SKILL_CONFIG_ERROR;//无配置
		}
		if(skill.getNeedJob() < GameConstants.SKILL_JOB_TYPE_TONGYONG && skill.getNeedJob() != role.getConfigId().intValue()){
			return AppErrorCode.ROLE_JOB_NOT_NEED;//职业不符
		}
		SkillXueXiConfig config = skill.getXuexiConfig(1);
		if(config == null){
			return AppErrorCode.SKILL_CONFIG_ERROR;//无学习配置
		}
		if(role.getLevel() < config.getMinLevel()){
			return AppErrorCode.ROLE_LEVEL_NOT_ENOUGH;//等级不足
		}
		String id1 = config.getNeedItem();
		if(id1 != null){
			if(guid == null || guid < 1){
				return AppErrorCode.GOODS_NOT_ENOUGH;//物品不存在
			}
			RoleItemExport roleItem = roleBagExportService.getBagItemByGuid(userRoleId, guid);
			if(roleItem == null || roleItem.getCount() < 1){
				return AppErrorCode.GOODS_NOT_ENOUGH;//物品不存在
			}
			GoodsConfig goodsConfig = goodsConfigExportService.loadById(roleItem.getGoodsId());
			if(goodsConfig == null){
				return AppErrorCode.GOODS_NOT_ENOUGH;//物品不存在
			}
			if(!goodsConfig.getId1().equals(id1)){
				return AppErrorCode.GOODS_NOT_ENOUGH;//物品不存在
			}
		}
		int gold = config.getGold();
		if(gold > 0){
			if(null!=accountExportService.decrCurrencyWithNotify(GoodsCategory.MONEY, gold, userRoleId, LogPrintHandle.CONSUME_LEARN_SKILL, false,LogPrintHandle.CBZ_LEARN_ZD_SKILL)){
				return AppErrorCode.GOLD_NOT_ENOUGH;//金币不足
			}
		}
		if(id1 != null){//需要消耗材料
			roleBagExportService.removeBagItemByGuid(guid, 1, userRoleId, GoodsSource.SKILL_LEARN, true, true);
		}
		
		RoleSkill roleSkill = createRoleSkill(userRoleId, skillId,GameConstants.SKILL_TYPE_ZHUDONG);
		config = skill.getXuexiConfig(1);
		roleSkill.setMaxShulian(config.getShulian());
		roleSkillDao.cacheInsert(roleSkill, userRoleId);
		//通知场景学习新技能
		BusMsgSender.send2Stage(userRoleId, InnerCmdType.SKILL_ADD_SKILL, new Object[]{skillId,getSkillZplus(userRoleId),1});
		
		//打印日志
		printLog(role, skillId, 1, gold, 0, LogPrintHandle.SKILL_TYPE_ZD_LEARN);
		return new Object[]{1,skillId};
	}
	
	/**
	 * 主动技能升级
	 * @param userRoleId
	 * @param skillId
	 * @param guid
	 * @return
	 */
	public Object[] skillLevelUp(Long userRoleId,String skillId,Long guid){
		RoleWrapper role = roleExportService.getLoginRole(userRoleId);
		if(role == null){
			return AppErrorCode.ROLE_IS_NOT_EXIST;//角色不存在
		}
		RoleSkill skill = getSkill(userRoleId, skillId);
		if(skill == null || GameConstants.SKILL_TYPE_ZHUDONG != skill.getType()){
			return AppErrorCode.SKILL_NOT_LEARN;//技能尚未学习
		}
		SkillConfig skillConfig = skillConfigExportService.loadById(skillId);
		if(skillConfig == null){
			return AppErrorCode.SKILL_CONFIG_ERROR;//无配置
		}
		int next = skill.getLevel() + 1;
		SkillXueXiConfig config = skillConfig.getXuexiConfig(next);
		if(config == null){
			return AppErrorCode.SKILL_IS_MAX_LEVEL;//已满级
		}
		if(role.getLevel() < config.getMinLevel()){
			return AppErrorCode.ROLE_LEVEL_NOT_ENOUGH;//角色等级不足
		}
		String id1 = config.getNeedItem();
		if(id1 != null){
			if(guid == null || guid < 1){
				return AppErrorCode.GOODS_NOT_ENOUGH;//物品不存在
			}
			RoleItemExport roleItem = roleBagExportService.getBagItemByGuid(userRoleId, guid);
			if(roleItem == null || roleItem.getCount() < 1){
				return AppErrorCode.GOODS_NOT_ENOUGH;//物品不存在
			}
			GoodsConfig goodsConfig = goodsConfigExportService.loadById(roleItem.getGoodsId());
			if(goodsConfig == null){
				return AppErrorCode.GOODS_NOT_ENOUGH;//物品不存在
			}
			if(!goodsConfig.getId1().equals(id1)){
				return AppErrorCode.GOODS_NOT_ENOUGH;//物品不存在
			}
		}
		int shulian = skill.getShulian();
		int zhenqi = 0;
		if(shulian < config.getShulian()){
			zhenqi = config.getZhenqi();
		}
		//熟练度不足，需要消耗真气
		if(zhenqi > 0 && !roleExportService.isEnoughZhenqi(userRoleId, zhenqi)){
			return AppErrorCode.SKILL_NOT_ENOUGH_ZHENQI;
		}
		
		int gold = config.getGold();
		if(gold > 0){
			Object[] result = accountExportService.decrCurrencyWithNotify(GoodsCategory.MONEY, gold, userRoleId, LogPrintHandle.CONSUME_LEARN_SKILL, true, LogPrintHandle.CBZ_LEVEL_UP_ZD_SKILL);
			if(result != null){
				return result;
			}
		}
		
		if(zhenqi > 0){//消耗真气
			roleExportService.costZhenqi(userRoleId, zhenqi);
		}
		
		if(id1 != null){//需要消耗材料
			roleBagExportService.removeBagItemByGuid(guid, 1, userRoleId, GoodsSource.SKILL_LEARN, true, true);
		}
		skill.clearShulian();
		skill.setLevel(next);
		config = skillConfig.getXuexiConfig(next + 1);
		if(config != null){
			skill.setMaxShulian(config.getShulian());
		}
		roleSkillDao.cacheUpdate(skill, userRoleId);
		//通知场景学习新技能
		BusMsgSender.send2Stage(userRoleId, InnerCmdType.SKILL_ADD_SKILL, new Object[]{skillId,getSkillZplus(userRoleId),next});
		//打印日志
		printLog(role, skillId, next, gold, zhenqi, LogPrintHandle.SKILL_TYPE_ZD_LEVEL_UP);
		
		return new Object[]{1,skillId};
	}
	
	/**
	 * 学习被动技能
	 * @param userRoleId
	 * @param skillId
	 * @param guid
	 * @return
	 */
	public Object[] learnBeiDongSkill(Long userRoleId,String skillId,Long guid){
		RoleSkill roleSkill = getSkill(userRoleId, skillId);
		if(roleSkill != null){
			return AppErrorCode.SKILL_HAS_LEARN;//技能尚未学习
		}
		RoleWrapper role = roleExportService.getLoginRole(userRoleId);
		if(role == null){
			return AppErrorCode.ROLE_IS_NOT_EXIST;//角色不存在
		}
		BeiDongSkillConfig skill = beiDongSkillConfigExportService.loadById(skillId);
		if(skill == null){
			return AppErrorCode.SKILL_CONFIG_ERROR;//无配置
		}
		BeiDongSkillXueXiConfig config = skill.getConfig(1);
		if(config == null){
			return AppErrorCode.SKILL_CONFIG_ERROR;//无学习配置
		}
		if(role.getLevel() < config.getMinLevel()){
			return AppErrorCode.ROLE_LEVEL_NOT_ENOUGH;//等级不足
		}
		String id1 = config.getNeedItem();
		if(id1 != null){
			if(guid == null || guid < 1){
				return AppErrorCode.GOODS_NOT_ENOUGH;//物品不存在
			}
			RoleItemExport roleItem = roleBagExportService.getBagItemByGuid(userRoleId, guid);
			if(roleItem == null || roleItem.getCount() < 1){
				return AppErrorCode.GOODS_NOT_ENOUGH;//物品不存在
			}
			GoodsConfig goodsConfig = goodsConfigExportService.loadById(roleItem.getGoodsId());
			if(goodsConfig == null){
				return AppErrorCode.GOODS_NOT_ENOUGH;//物品不存在
			}
			if(!goodsConfig.getId1().equals(id1)){
				return AppErrorCode.GOODS_NOT_ENOUGH;//物品不存在
			}
		}
		int zhenqi = config.getZhenqi();
		if(zhenqi > 0 && !roleExportService.isEnoughZhenqi(userRoleId, zhenqi)){
			return AppErrorCode.ZHEN_ERROR;
		}
		
		int gold = config.getGold();
		if(gold > 0){
			Object[] result = accountExportService.decrCurrencyWithNotify(GoodsCategory.MONEY, gold, userRoleId, LogPrintHandle.CONSUME_LEARN_SKILL, false, LogPrintHandle.CBZ_LEARN_BD_SKILL);
			if(null != result){
				return result;
			}
		}
		if(id1 != null){//需要消耗材料
			roleBagExportService.removeBagItemByGuid(guid, 1, userRoleId, GoodsSource.SKILL_LEARN, true, true);
		}
		if(zhenqi > 0){//需要消耗真气
			roleExportService.costZhenqi(userRoleId, zhenqi);
		}
		
		roleSkill = createRoleSkill(userRoleId, skillId,GameConstants.SKILL_TYPE_BEIDONG);
		roleSkillDao.cacheInsert(roleSkill, userRoleId);
		//通知场景学习新技能
		noticeStageSkillAttributeChange(userRoleId, GameConstants.SKILL_TYPE_BEIDONG);
		//打印日志
		printLog(role, skillId, 1, gold, zhenqi, LogPrintHandle.SKILL_TYPE_BD_LEARN);
//		//修炼任务
//		BusMsgSender.send2BusInner(userRoleId, InnerCmdType.INNER_XIULIAN_TASK_CHARGE, new Object[] {XiuLianConstants.BEIDONG_JINNEG_LEVEL, null});
		
		return new Object[]{1,skillId};
	}
	
	/**
	 * 学习糖宝被动技能
	 * @param userRoleId
	 * @param skillId
	 * @param guid
	 * @return
	 */
	public Object[] learnTangBaoSkill(Long userRoleId,String skillId){
		RoleSkill roleSkill = getSkill(userRoleId, skillId);
		if(roleSkill != null){
			return AppErrorCode.SKILL_HAS_LEARN;//技能已经学习
		}
		RoleWrapper role = roleExportService.getLoginRole(userRoleId);
		if(role == null){
			return AppErrorCode.ROLE_IS_NOT_EXIST;//角色不存在
		}
		TangBaoSkillConfig skill = tangBaoSkillConfigExportService.loadById(skillId);
		if(skill == null){
			return AppErrorCode.SKILL_CONFIG_ERROR;//无配置
		}
		TangBaoSkillXueXiConfig config = skill.getConfig(1);
		if(config == null){
			return AppErrorCode.SKILL_CONFIG_ERROR;//无学习配置
		}
		if(role.getLevel() < config.getMinLevel()){
			return AppErrorCode.ROLE_LEVEL_NOT_ENOUGH;//等级不足
		}
		int gold = config.getGold();
		if(gold > 0){
			Object[] isOb = accountExportService.isEnoughtValue(GoodsCategory.MONEY, gold, userRoleId);
			if(isOb != null){
				return isOb;
			}
		}
		//检测道具是否足够
		if(config.getNeedItem() != null){
			BagSlots bagSlots = roleBagExportService.removeBagItemByGoodsType(config.getNeedItem(), config.getCount(), userRoleId, GoodsSource.SKILL_LEARN, true, true);
			if(!bagSlots.isSuccee()){
				return bagSlots.getErrorCode();
			}
		}
		if(gold > 0){
			accountExportService.decrCurrencyWithNotify(GoodsCategory.MONEY, gold, userRoleId, LogPrintHandle.CONSUME_LEARN_SKILL, false, LogPrintHandle.CBZ_LEARN_TB_SKILL);
		}
		
		roleSkill = createRoleSkill(userRoleId, skillId,GameConstants.SKILL_TYPE_TANGBAO);
		roleSkillDao.cacheInsert(roleSkill, userRoleId);
		//通知场景学习新技能
		//noticeStageSkillAttributeChange(userRoleId, GameConstants.SKILL_TYPE_TANGBAO);
		//打印日志
		printLog(role, skillId, 1, gold, 0, LogPrintHandle.SKILL_TYPE_TB_LEARN);
		return new Object[]{1,skillId};
	}
	
	/**
	 * 被动技能升级
	 * @param userRoleId
	 * @param skillId
	 * @param guid
	 * @return
	 */
	public Object[] beiDongSkillLevelUp(Long userRoleId,String skillId,Long guid){
		RoleWrapper role = roleExportService.getLoginRole(userRoleId);
		if(role == null){
			return AppErrorCode.ROLE_IS_NOT_EXIST;//角色不存在
		}
		RoleSkill skill = getSkill(userRoleId, skillId);
		if(skill == null){
			return AppErrorCode.SKILL_NOT_LEARN;//技能尚未学习
		}
		BeiDongSkillConfig skillConfig = beiDongSkillConfigExportService.loadById(skillId);
		if(skillConfig == null){
			return AppErrorCode.SKILL_CONFIG_ERROR;//无配置
		}
		int next = skill.getLevel() + 1;
		BeiDongSkillXueXiConfig config = skillConfig.getConfig(next);
		if(config == null){
			return AppErrorCode.SKILL_IS_MAX_LEVEL;//已满级
		}
		if(role.getLevel() < config.getMinLevel()){
			return AppErrorCode.ROLE_LEVEL_NOT_ENOUGH;//角色等级不足
		}
		String id1 = config.getNeedItem();
		if(id1 != null){
			if(guid == null || guid < 1){
				return AppErrorCode.GOODS_NOT_ENOUGH;//物品不存在
			}
			RoleItemExport roleItem = roleBagExportService.getBagItemByGuid(userRoleId, guid);
			if(roleItem == null || roleItem.getCount() < 1){
				return AppErrorCode.GOODS_NOT_ENOUGH;//物品不存在
			}
			GoodsConfig goodsConfig = goodsConfigExportService.loadById(roleItem.getGoodsId());
			if(goodsConfig == null){
				return AppErrorCode.GOODS_NOT_ENOUGH;//物品不存在
			}
			if(!goodsConfig.getId1().equals(id1)){
				return AppErrorCode.GOODS_NOT_ENOUGH;//物品不存在
			}
		}
		//验证真气
		int zq = config.getZhenqi();
		if(zq > 0 && !roleExportService.isEnoughZhenqi(userRoleId, zq)){
			return AppErrorCode.ZHEN_ERROR;
		}
		
		int gold = config.getGold();
		if(gold > 0){
			Object[] result = accountExportService.decrCurrencyWithNotify(GoodsCategory.MONEY, gold, userRoleId, LogPrintHandle.CONSUME_LEARN_SKILL, false, LogPrintHandle.CBZ_LEVEL_UP_BD_SKILL);
			if(null != result){
				return result;
			}
		}
		
		if(zq > 0){//消耗真气
			roleExportService.costZhenqi(userRoleId, zq);
		}
		if(id1 != null){//需要消耗材料
			roleBagExportService.removeBagItemByGuid(guid, 1, userRoleId, GoodsSource.SKILL_LEARN, true, true);
		}
		skill.setLevel(next);
		roleSkillDao.cacheUpdate(skill, userRoleId);
		//通知场景学习新技能
		noticeStageSkillAttributeChange(userRoleId, GameConstants.SKILL_TYPE_BEIDONG);

		//打印日志
		printLog(role, skillId, next, gold, zq, LogPrintHandle.SKILL_TYPE_BD_LEVEL_UP);
//		//修炼任务
//		BusMsgSender.send2BusInner(userRoleId, InnerCmdType.INNER_XIULIAN_TASK_CHARGE, new Object[] {XiuLianConstants.BEIDONG_JINNEG_LEVEL, null});
		return new Object[]{1,skillId};
	}
	/**
	 * 糖宝技能升级
	 * @param userRoleId
	 * @param skillId
	 * @param guid
	 * @return
	 */
	public Object[] tangbaoSkillLevelUp(Long userRoleId,String skillId){
		RoleWrapper role = roleExportService.getLoginRole(userRoleId);
		if(role == null){
			return AppErrorCode.ROLE_IS_NOT_EXIST;//角色不存在
		}
		RoleSkill skill = getSkill(userRoleId, skillId);
		if(skill == null){
			return AppErrorCode.SKILL_NOT_LEARN;//技能尚未学习
		}
		TangBaoSkillConfig skillConfig = tangBaoSkillConfigExportService.loadById(skillId);
		if(skillConfig == null){
			return AppErrorCode.SKILL_CONFIG_ERROR;//无配置
		}
		int next = skill.getLevel() + 1;
		TangBaoSkillXueXiConfig config = skillConfig.getConfig(next);
		if(config == null){
			return AppErrorCode.SKILL_IS_MAX_LEVEL;//已满级
		}
		if(role.getLevel() < config.getMinLevel()){
			return AppErrorCode.ROLE_LEVEL_NOT_ENOUGH;//角色等级不足
		}
		
		int gold = config.getGold();
		if(gold > 0){
			Object[] isOb = accountExportService.isEnoughtValue(GoodsCategory.MONEY, gold, userRoleId);
			if(isOb != null){
				return isOb;
			}
		}
		//检测道具是否足够
		if(config.getNeedItem() != null){
			BagSlots bagSlots = roleBagExportService.removeBagItemByGoodsType(config.getNeedItem(), config.getCount(), userRoleId, GoodsSource.SKILL_LEARN, true, true);
			if(!bagSlots.isSuccee()){
				return bagSlots.getErrorCode();
			}
		}
		if(gold > 0){
			accountExportService.decrCurrencyWithNotify(GoodsCategory.MONEY, gold, userRoleId, LogPrintHandle.CONSUME_LEARN_SKILL, false, LogPrintHandle.CBZ_LEVEL_UP_TB_SKILL);
		}
		skill.setLevel(next);
		roleSkillDao.cacheUpdate(skill, userRoleId);
		
		//打印日志
		printLog(role, skillId, next, gold, 0, LogPrintHandle.SKILL_TYPE_TB_LEVEL_UP);
		return new Object[]{1,skillId};
	} 
	
	/**
	 * 创建技能对象
	 * @param userRoleId
	 * @param skillId
	 * @param type
	 * @return
	 */
	private RoleSkill createRoleSkill(Long userRoleId,String skillId,int type){
		RoleSkill skill = new RoleSkill();
		skill.setUserRoleId(userRoleId);
		skill.setSkillId(skillId);
		skill.setShulian(0);
		skill.setLevel(1);
		skill.setId(IdFactory.getInstance().generateId(ServerIdType.COMMON));
		skill.setType(type);
		return skill;
	}
	/**
	 * 获取角色技能
	 * @param userRoleId
	 * @param skillId
	 * @return
	 */
	public RoleSkill getSkill(Long userRoleId,String skillId){
		final String sid = skillId;
		IQueryFilter<RoleSkill> filter = new IQueryFilter<RoleSkill>() {
			boolean stop = false;
			@Override
			public boolean check(RoleSkill skill) {
				if(skill.getSkillId().equals(sid)){
					stop = true;
					return true;
				}
				return false;
			}

			@Override
			public boolean stopped() {
				return stop;
			}
		};
		List<RoleSkill> list = roleSkillDao.cacheLoadAll(userRoleId, filter);
		if(list != null && list.size() > 0){
			return list.get(0);
		}
		return null;
	}
	/**
	 * 获取角色技能
	 * @param userRoleId
	 * @param skillType
	 * @return
	 */
	public List<RoleSkill> getSkillsByType(Long userRoleId,Integer skillType){
		final Integer type = skillType;
		IQueryFilter<RoleSkill> filter = new IQueryFilter<RoleSkill>() {
			@Override
			public boolean check(RoleSkill skill) {
				return type.equals(skill.getType());
			}
			
			@Override
			public boolean stopped() {
				return false;
			}
		};
		return roleSkillDao.cacheLoadAll(userRoleId, filter);
	}
	
	/**
	 * 技能使用增加熟练度
	 * @param userRoleId
	 * @param skillId
	 */
	public void skillUse(Long userRoleId,String skillId){
		RoleSkill skill = getSkill(userRoleId, skillId);
		if(skill == null){
			return;//技能不存在
		}
		if(skill.getShulian() >= skill.getMaxShulian()){
			return;//熟练度已满
		}
		skill.addShulian(1);//暂定每次使用增加一点熟练度
		roleSkillDao.cacheUpdate(skill, userRoleId);
		if(skill.getShulian() >= skill.getMaxShulian()){
			BusMsgSender.send2One(userRoleId, ClientCmdType.SKILL_SHULIAN_MAX, skillId);
			BusMsgSender.send2Stage(userRoleId, InnerCmdType.ROLE_CANCEL_NOTICESKILLS, skillId);
		}
	}
	/**
	 * 分页签获取技能
	 * @param userRoleId
	 * @param type
	 * @return
	 */
	public Object[] getSkillsInfo(Long userRoleId,Integer type){
		List<RoleSkill> list = getSkillsByType(userRoleId, type);
		Object[] skills = null;
		if(list != null && list.size() > 0){
			skills = new Object[list.size()];
			for (int i = 0; i < skills.length; i++) {
				skills[i] = list.get(i).getInfo();
			}
		}
		return new Object[]{1,type,skills};
	}
	/**
	 * 打印日志
	 * @param role
	 * @param skillId
	 * @param level
	 * @param money
	 * @param zhenqi
	 * @param type
	 */
	private void printLog(RoleWrapper role,String skillId,int level,int money,int zhenqi,int type){
		try{
			GamePublishEvent.publishEvent(new SkillLogEvent(role.getId(), role.getName(), skillId, money, zhenqi, type));
		}catch (Exception e) {
			GameLog.error("",e);
		}
	}
	
	private void noticeStageSkillAttributeChange(Long userRoleId,int type){
		List<RoleSkill> skills = getSkillsByType(userRoleId, type);
		if(skills != null && skills.size() > 0){
			Map<String,Long> attribute = new HashMap<>();
			for (RoleSkill roleSkill : skills) {
				if(type == GameConstants.SKILL_TYPE_BEIDONG){
					BeiDongSkillConfig config = beiDongSkillConfigExportService.loadById(roleSkill.getSkillId());
					if(config == null){
						continue;
					}
					BeiDongSkillXueXiConfig xuexiConfig = config.getConfig(roleSkill.getLevel());
					if(xuexiConfig == null){
						continue;
					}
					ObjectUtil.longMapAdd(attribute, xuexiConfig.getAttributes());
				}else if(type == GameConstants.SKILL_TYPE_GUILD){
					GuildBeiDongSkillConfig config = guildBeiDongSkillConfigExportService.loadById(roleSkill.getSkillId());
					if(config == null){
						continue;
					}
					GuildBeiDongSkillXueXiConfig xuexiConfig = config.getConfig(roleSkill.getLevel());
					if(xuexiConfig == null){
						continue;
					}
					ObjectUtil.longMapAdd(attribute, xuexiConfig.getAttributes());
				}else if(type == GameConstants.SKILL_TYPE_TANGBAO){
					TangBaoSkillConfig config = tangBaoSkillConfigExportService.loadById(roleSkill.getSkillId());
					if(config == null){
						continue;
					}
					//判断技能是否装备了
					TangBaoSkillXueXiConfig xuexiConfig = config.getConfig(roleSkill.getLevel());
					if(xuexiConfig == null){
						continue;
					}
					ObjectUtil.longMapAdd(attribute, xuexiConfig.getAttributes());
				}
			}
			if(type == GameConstants.SKILL_TYPE_BEIDONG){
				BusMsgSender.send2Stage(userRoleId, InnerCmdType.SKILL_ADD_BEIDONG_SKILL, attribute);
			}else if(type == GameConstants.SKILL_TYPE_GUILD){
				BusMsgSender.send2Stage(userRoleId, InnerCmdType.SKILL_ADD_GUILD_SKILL, attribute);
			}else if(type == GameConstants.SKILL_TYPE_TANGBAO){
				BusMsgSender.send2Stage(userRoleId, InnerCmdType.SKILL_ADD_TANGBAO_SKILL, attribute);
			}
		}
	}
	
	/**
	 * 初始化妖神技能
	 * @param userRoleId
	 */
	public void initYaoShenSkills(Long userRoleId){
		List<RoleSkill> skills = getSkillsByType(userRoleId, GameConstants.SKILL_TYPE_YAOSHEN);
		if(skills != null && skills.size() > 0){
			return;
		}
		List<String> skillIds = SkillConfigExportService.getYaoshenSkillIds();
		if(skillIds == null || skillIds.size() < 1){
			return;
		}
		Object[] skillOut = new Object[skillIds.size()];
		int index = 0;
		String stageId = publicRoleStateExportService.getRolePublicStageId(userRoleId);
		IStage istage = null;
		if(stageId != null){
			istage = StageManager.getStage(stageId);
		}
		IRole role = null;
		if(istage != null){
			role = istage.getElement(userRoleId, ElementType.ROLE);
		}
		for (String skillId : skillIds) {
			RoleSkill skill = createRoleSkill(userRoleId, skillId, GameConstants.SKILL_TYPE_YAOSHEN);
			roleSkillDao.cacheInsert(skill, userRoleId);
			skillOut[index++] = skill.getInfo();
			try{
				SkillConfig skillConfig = skillConfigExportService.loadById(skillId);
				SkillXueXiConfig config = skillConfig.getXuexiConfig(2);
				skill.setMaxShulian(config.getShulian());
				if(role != null){
					role.addSkillShulian(skill);
				}
			}catch (Exception e) {
				GameLog.error("",e);
			}
		}
		BusMsgSender.send2Stage(userRoleId, InnerCmdType.SKILL_ZPLUS_CHANGE, getSkillZplus(userRoleId));
		BusMsgSender.send2Stage(userRoleId, InnerCmdType.ROLE_ADD_NOTICESKILLS, skillIds);
		BusMsgSender.send2One(userRoleId, ClientCmdType.SKILL_TAB_INFO, new Object[]{1,GameConstants.SKILL_TYPE_YAOSHEN,skillOut});
	}
	
	public long getSkillZplus(Long userRoleId){
		long zplus = 0;
		List<RoleSkill> skills = getSkillsByType(userRoleId, GameConstants.SKILL_TYPE_ZHUDONG);
		if(skills != null && skills.size() > 0){
			for (RoleSkill roleSkill : skills) {
				SkillConfig skillConfig = skillConfigExportService.loadById(roleSkill.getSkillId());
				if(skillConfig != null){
					SkillXueXiConfig config = skillConfig.getXuexiConfig(roleSkill.getLevel());
					if(config != null){
						zplus += config.getZplus();
					}
				}
			}
		}
		skills = getSkillsByType(userRoleId, GameConstants.SKILL_TYPE_YAOSHEN);
		if(skills != null && skills.size() > 0){
			for (RoleSkill roleSkill : skills) {
				SkillConfig skillConfig = skillConfigExportService.loadById(roleSkill.getSkillId());
				if(skillConfig != null){
					SkillXueXiConfig config = skillConfig.getXuexiConfig(roleSkill.getLevel());
					if(config != null){
						zplus += config.getZplus();
					}
				}
			}
		}
		return zplus;
	}
	
	/**
	 * 切换技能
	 * @param userRoleId
	 * @param yaoshen	切换后是否是妖神状态
	 */
	public void changeSkill(Long userRoleId,boolean yaoshen){
		int type = GameConstants.SKILL_TYPE_ZHUDONG;
		if(yaoshen){
			type = GameConstants.SKILL_TYPE_YAOSHEN;
		}
		List<RoleSkill> skills = getSkillsByType(userRoleId, type);
		if(skills != null && skills.size() > 0){
			List<String> skillIds = new ArrayList<>();
			for (RoleSkill skill : skills) {
				skillIds.add(SkillManager.getSkillId(skill.getSkillId(), skill.getLevel()));
			}
			//通知场景切换技能
			BusMsgSender.send2Stage(userRoleId, InnerCmdType.ROLE_CHANGE_SKILL, skillIds.toArray());
		}
	}
	/**
	 * 妖神技能升级
	 * @param userRoleId
	 * @param skillId
	 * @param guid
	 * @return
	 */
	public Object[] yaoshenSkillLevelUp(Long userRoleId,String skillId){
		RoleWrapper role = roleExportService.getLoginRole(userRoleId);
		if(role == null){
			return AppErrorCode.ROLE_IS_NOT_EXIST;//角色不存在
		}
		RoleSkill skill = getSkill(userRoleId, skillId);
		if(skill == null || GameConstants.SKILL_TYPE_YAOSHEN != skill.getType()){
			return AppErrorCode.SKILL_NOT_LEARN;//技能尚未学习
		}
		SkillConfig skillConfig = skillConfigExportService.loadById(skillId);
		if(skillConfig == null){
			return AppErrorCode.SKILL_CONFIG_ERROR;//无配置
		}
		int next = skill.getLevel() + 1;
		SkillXueXiConfig config = skillConfig.getXuexiConfig(next);
		if(config == null){
			return AppErrorCode.SKILL_IS_MAX_LEVEL;//已满级
		}
		int shulian = skill.getShulian();
		int zhenqi = 0;
		if(shulian < config.getShulian()){
			zhenqi = config.getZhenqi();
		}
		//熟练度不足，需要消耗真气
		if(zhenqi > 0 && !roleExportService.isEnoughZhenqi(userRoleId, zhenqi)){
			return AppErrorCode.SKILL_NOT_ENOUGH_ZHENQI;
		}
		
		int gold = config.getGold();
		if(gold > 0){
			Object[] result = accountExportService.decrCurrencyWithNotify(GoodsCategory.MONEY, gold, userRoleId, LogPrintHandle.CONSUME_LEARN_SKILL, true, LogPrintHandle.CBZ_LEVEL_UP_ZD_SKILL);
			if(result != null){
				return result;
			}
		}
		
		if(zhenqi > 0){//消耗真气
			roleExportService.costZhenqi(userRoleId, zhenqi);
		}
		skill.clearShulian();
		skill.setLevel(next);
		config = skillConfig.getXuexiConfig(next + 1);
		if(config != null){
			skill.setMaxShulian(config.getShulian());
		}
		roleSkillDao.cacheUpdate(skill, userRoleId);
		//通知场景学习新技能
		BusMsgSender.send2Stage(userRoleId, InnerCmdType.SKILL_ADD_SKILL, new Object[]{skillId,getSkillZplus(userRoleId),next});
		//打印日志
		printLog(role, skillId, next, gold, zhenqi, LogPrintHandle.SKILL_TYPE_YS_LEVEL_UP);
		
		return new Object[]{1,skillId};
	}
	
	public void saveSkill(RoleSkill roleSkill){
		roleSkillDao.cacheUpdate(roleSkill, roleSkill.getUserRoleId());
	}
	 
	/**
	 * 自动学习
	 * @param userRoleId
	 * @param level
	 * @return
	 */
	public Object[] autoLearnSkill(Long userRoleId,int roleConfig, int level) {
		for (SkillConfig skillConfig : skillConfigExportService.getAutoConfig(roleConfig)) {
			RoleSkill skill = getSkill(userRoleId, skillConfig.getId());
			SkillXueXiConfig skillXueXiConfig = skillConfig.getXuexiConfig(1);
			if(skillXueXiConfig == null){
				GameLog.error("skillXueXiConfig is null,leve="+1);
				continue;
			}
			if(skill == null ){
				if((skillConfig.getSkillFireType() == SkillFireType.NORMAL && skillXueXiConfig.getMinLevel() <= level)|| skillConfig.getSkillFireType() != SkillFireType.NORMAL){
					skill = createRoleSkill(userRoleId, skillConfig.getId(),GameConstants.SKILL_TYPE_ZHUDONG);
					roleSkillDao.cacheInsert(skill, userRoleId);
					//通知场景学习新技能
					BusMsgSender.send2Stage(userRoleId, InnerCmdType.SKILL_ADD_SKILL, new Object[]{skillConfig.getId(),getSkillZplus(userRoleId),1});
				}
			}
		}
		return null;
	}
}
