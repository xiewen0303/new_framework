package com.game.start;

import com.junyou.constants.GameConstants;
import com.junyou.gameconfig.checker.GoodsConfigChecker;
import com.junyou.public_.email.export.EmailExportService;
import com.junyou.public_.share.service.PublicRoleStateService;
import com.junyou.share.StringAppContextShare;
import com.kernel.data.cache.CacheManager;

/**
 * 游戏业务启动、停止时任务处理
 * @author LiuYu
 * @date 2015-4-20 下午3:31:46
 */
public class GameBusStartStopHandle {
	private static CacheManager publicCacheManager;

	public GameBusStartStopHandle(CacheManager publicCacheManager) {
		GameBusStartStopHandle.publicCacheManager = publicCacheManager;
	}
	/**
	 * 启动处理  TODO  启动初始化数据,这里关掉了不用的相关模块
	 */
	public static void startHandle(){
		//初始化公共缓存
		publicCacheManager.activateRoleCache(GameConstants.DEFAULT_ROLE_ID);
		//系统用户上线
		StringAppContextShare.getSpringContext().getBean(PublicRoleStateService.class).change2PublicOnline(GameConstants.DEFAULT_ROLE_ID);
//		//定时刷怪初始化定时刷怪
//		StringAppContextShare.getSpringContext().getBean(ActivityBossExportService.class).init(); 
		//邮件初始化
		StringAppContextShare.getSpringContext().getBean(EmailExportService.class).init();
//		//竞技场初始化
//		StringAppContextShare.getSpringContext().getBean(JingjiExportService.class).init();
//		//公会初始化
//		StringAppContextShare.getSpringContext().getBean(GuildExportService.class).init();
//		//市场初始化
//		StringAppContextShare.getSpringContext().getBean(ShiChangExportService.class).init();
//		//排行榜排名初始化（优先级尽量放低）
//		StringAppContextShare.getSpringContext().getBean(RankExportService.class).init();
//		//爬塔初始化
//		StringAppContextShare.getSpringContext().getBean(PataService.class).init();
//		//跨服多人本启动业务
//		if(KuafuConfigPropUtil.isKuafuServer()){
//			StringAppContextShare.getSpringContext().getBean(MoreFubenTeamService.class).kfStartHandle();
//		}
//		//跨服多人本启动业务
//		if(KuafuConfigPropUtil.isKuafuServer()){
//			StringAppContextShare.getSpringContext().getBean(BaguaFubenTeamService.class).kfStartHandle();
//		}
		//消费排名初始化数据
		//StringAppContextShare.getSpringContext().getBean(XiaoFeiInit.class).init();
		
		// 跨服云宫之巅初始化
//		StringAppContextShare.getSpringContext().getBean(KuafuYunGongExportService.class).init();
		//定时活动必须在最下面，其他业务初始化，在此之前
		//定时活动初始化
//		StringAppContextShare.getSpringContext().getBean(DingShiActiveExportService.class).initDayActive();
		//物品配置检测
		GoodsConfigChecker.startCheck();
//		GoodsConfigChecker.startCheckZhuanHuan();
		//题库配置检测
//		TikuConfigChecker.startCheck();
		
//		StringAppContextShare.getSpringContext().getBean(KuafuArena1v1MatchExportService.class).init();
//		StringAppContextShare.getSpringContext().getBean(KuafuArena4v4MatchExportService.class).init();
//		StringAppContextShare.getSpringContext().getBean(KuafuArena1v1SourceExportService.class).startKuafuArenaCleanJob();
//		StringAppContextShare.getSpringContext().getBean(KuafuArena4v4SourceExportService.class).startKuafuArenaCleanJob();
//		StringAppContextShare.getSpringContext().getBean(MoreFubenExportService.class).initHandleOutTimeTeamSchedule();
//		StringAppContextShare.getSpringContext().getBean(BaguaExportService.class).initHandleOutTimeTeamSchedule();
//		StringAppContextShare.getSpringContext().getBean(MaiguExportService.class).initHandleOutTimeTeamSchedule();
		
	}
	
	/**
	 * 停机处理
	 */
	public static void stopHandle(){
		//冻结并回写公共缓存
		publicCacheManager.freezeRoleCache(GameConstants.DEFAULT_ROLE_ID);
		
//		//公会模块停机处理
//		GuildManager.getManager().stopHandle();
//		//爬塔停机处理
//		StringAppContextShare.getSpringContext().getBean(PataService.class).stopHandle();
	}
}
