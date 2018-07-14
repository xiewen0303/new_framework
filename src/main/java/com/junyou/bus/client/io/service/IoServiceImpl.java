package com.junyou.bus.client.io.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.junyou.bus.account.entity.RoleAccountWrapper;
import com.junyou.bus.account.service.RoleAccountService;
import com.junyou.bus.bag.service.RoleBagService;
import com.junyou.bus.email.service.EmailRelationService;
import com.junyou.bus.jinfeng.export.JinFengExportService;
import com.junyou.bus.notice.export.NoticeExportService;
import com.junyou.bus.recharge.service.RechargeService;
import com.junyou.bus.role.entity.UserRole;
import com.junyou.bus.role.export.RoleWrapper;
import com.junyou.bus.role.service.UserRoleService;
import com.junyou.bus.rolebusiness.configure.export.RoleBusinessInfoWrapper;
import com.junyou.bus.rolebusiness.service.RoleBusinessInfoService;
import com.junyou.bus.setting.service.RoleSettingService;
import com.junyou.bus.skill.export.RoleSkillExportService;
import com.junyou.bus.stagecontroll.export.StageControllExportService;
import com.junyou.bus.vip.service.RoleVipInfoService;
import com.junyou.bus.vip.util.RoleVipWrapper;
import com.junyou.cmd.ClientCmdType;
import com.junyou.log.GameLog;
import com.junyou.public_.nodecontrol.export.NodeControlExportService;
import com.junyou.public_.service.MsgFilterService;
import com.junyou.public_.share.service.PublicRoleStateService;
import com.junyou.stage.configure.export.impl.RoleBehaviourExportService;
import com.junyou.stage.export.TeamExportService;
import com.junyou.utils.zip.CompressConfigUtil;
import com.kernel.data.cache.CacheManager;
import com.kernel.tunnel.bus.BusMsgSender;

@Service
public class IoServiceImpl{

	/**
	 * 是否使用缓存
	 */
	private final boolean usecache = true;

//	@Autowired
//	private FightPowerService fightPowerService;
	@Autowired
	private UserRoleService roleExportService;
	@Autowired
	private JinFengExportService jinFengExportService;
	@Autowired
	private StageControllExportService stageControllExportService;
	@Autowired
	private RoleBehaviourExportService roleBehaviourExportService;
//	@Autowired
//	private TaskExportService taskExportService;
	@Autowired
	private RechargeService rechargeExportService;
	@Autowired
	private EmailRelationService emailRelationExportService;
//	@Autowired
//	private GuildExportService guildExportService;
	@Autowired
	private TeamExportService teamExportService;
	@Autowired
	private RoleSettingService roleSettingExportService;
	@Autowired
	private RoleBusinessInfoService roleBusinessInfoExportService;
//	@Autowired
//	private FubenService fubenExportService;
	@Autowired
	private RoleSkillExportService roleSkillExportService;
	@Autowired
	private RoleAccountService accountExportService;
	@Autowired
	private RoleVipInfoService roleVipInfoExportService;
//	@Autowired
//	private FriendExportService friendExportService;
	@Autowired
	@Qualifier("roleCacheManager")
	private CacheManager roleCacheManager;
	@Autowired
	private RoleBagService roleBagService;
//	@Autowired
//	private ZuoQiExportService zuoQiExportService;
//	@Autowired
//	private ChiBangExportService chiBangExportService;
//	@Autowired
//	private YaoshenExportService yaoshenExportService;
//	@Autowired
//	private XianJianExportService xianJianExportService;
//	@Autowired
//	private ZhanJiaExportService zhanJiaExportService;
//	@Autowired 
//	private TradeExportService tradeExportService;
//	@Autowired
//	private RoleOnlineExportService roleOnlineExportService;
//	@Autowired
//	private OfflineExpExportService offlineExpExportService;
//	@Autowired
//	private TaskDayExportService taskDayExportService;
//	@Autowired
//	private MoreFubenExportService moreFubenExportService;
//	@Autowired
//	private BaguaExportService baguaExportService;
//	@Autowired
//	private MaiguExportService maiguExportService;
//	@Autowired
//	private ChiBangExportService chibangExportService;
//	@Autowired
//	private QiLingExportService qiLingExportService;
//	@Autowired
//	private TianYuExportService tianYuExportService;
	@Autowired
	private NoticeExportService noticeExportService;
	@Autowired
	private MsgFilterService msgFilterService;
	@Autowired
	private NodeControlExportService nodeControlExportService;
	@Autowired
	private PublicRoleStateService publicRoleStateService;
	@Autowired
    private RoleVipInfoService roleVipInfoService;

	/**
	 * 角色登入节点
	 * @param roleid
	 * @param ip 
	 */
	public void roleIn(Long userRoleId,String ip) {
		//拉取数据库缓存
		if(usecache){
			roleCacheManager.activateRoleCache(userRoleId);
		}
		
		/**
		 * 切换登录完成上线状态,内存都加载到服务器了 
		 */
		publicRoleStateService.change2PublicOnline(userRoleId);

		/**
		 * 加载上线业务
		 */
		roleInHandle(userRoleId, ip);

		/**
		 * 最后才能登录进入场景
		 */
		stageControllExportService.login(userRoleId);
	}
	
	/**
	 * 上线业务
	 * @param userRoleId
	 * @param ip
	 */
	private void roleInHandle(Long userRoleId,String ip){
		//玩家上线初始化（包括防沉迷，设置角色状态为在线）
		roleExportService.logLoginTime(userRoleId, ip);
		//充值计算
		rechargeExportService.onlineHandlefinishRecharge(userRoleId);
		/**
		 * 推送给客服端背包数据
		 */
		roleBagService.loginHandle(userRoleId);
		//处理禁言上线业务
		jinFengExportService.onlineHandle(userRoleId);
		//处理邮件上线业务
		emailRelationExportService.onlineHandle(userRoleId);
		//技能上线业务
		roleSkillExportService.onlineHandle(userRoleId);
		//VIP上线业务
		roleVipInfoExportService.onlineHandle(userRoleId);
		//角色业务上线处理
		roleBusinessInfoExportService.onlineHandle(userRoleId);
		
		//打包数据
		compressDataAndSend2Client(userRoleId);
	}
	
	/**
	 * 登陆或者创建后打包数据给客户端
	 * @param userRoleId
	 */
	private void compressDataAndSend2Client(Long userRoleId){
		List<Object[]> data = new ArrayList<>();
		
//		//背包
//		data.add(new Object[]{ClientCmdType.GET_BAG_ITEMS, roleBagService.loginHandle(userRoleId)});
//		
		//货币
		RoleAccountWrapper roleAccountWrapper = accountExportService.getAccountWrapper(userRoleId);
		if(roleAccountWrapper != null){
			data.add(new Object[]{ClientCmdType.MONEY_CHANGE, roleAccountWrapper.getJb()});
			data.add(new Object[]{ClientCmdType.YUANBAO_CHANGE, roleAccountWrapper.getYb()});
			data.add(new Object[]{ClientCmdType.BANG_YB_CHANGE, roleAccountWrapper.getBindYb()});
		}
		//VIP信息
		RoleVipWrapper vip = roleVipInfoExportService.getRoleVipInfo(userRoleId);
		if(vip != null){
			data.add(new Object[]{ClientCmdType.VIP_LEVEL_CHANGE, vip.getVipLevel()});
			data.add(new Object[]{ClientCmdType.VIP_EXP_CHANGE, vip.getVipExp()});
		}
		
		//角色快捷栏设置 
		Object[] qbInfoData = roleSettingExportService.getQuickbarInfo(userRoleId);
		if(qbInfoData != null){
			data.add(new Object[]{ClientCmdType.GET_SETTINGINFO, qbInfoData});
		}
		//角色挂机设置
		Object gjInfoData = roleSettingExportService.getGuajiInfo(userRoleId);
		if(gjInfoData != null){
			data.add(new Object[]{ClientCmdType.GET_GUAJI_SET, gjInfoData});
		}
		
		//经验
		RoleWrapper roleWrapper = roleExportService.getLoginRole(userRoleId);
		data.add(new Object[]{ClientCmdType.EXP_CHANGE, new Object[]{roleWrapper.getExp(),0}});
		
		RoleBusinessInfoWrapper roleBusinessInfoWrapper = new RoleBusinessInfoWrapper(roleBusinessInfoExportService.getRoleBusinessInfo(userRoleId));
		//荣誉
		data.add(new Object[]{ClientCmdType.RONGYU_CHANGE, roleBusinessInfoWrapper.getRoleyu()});
		
		
		//系统公告
		Object notice = noticeExportService.getNotice();
		if(null != notice){
			data.add(new Object[]{ClientCmdType.GET_NOTICE, notice});
		}  
		
		//静默禁言
		Object jfData = jinFengExportService.getJinYanData(userRoleId);
		if(jfData != null){
			data.add(new Object[]{ClientCmdType.JING_YAN_JM,jfData});
		}
		
		//500 服务器通知客户端临时关闭某个功能
		Collection<Integer>  modIds = msgFilterService.getCloseModelClientIds();
		if(modIds != null && modIds.size() > 0){
			for (Integer clientModId : modIds) {
				data.add(new Object[]{ClientCmdType.MODEL_CLOSE,clientModId});
			}
		}
		//是否领取微端登录奖励 true:领取  false:未领取
		boolean weiDuanJiangLiGet = roleExportService.isWeiDuanJL(userRoleId);
		data.add(new Object[]{ClientCmdType.ISLQ_WEIDUAN, weiDuanJiangLiGet});

		//是否领取微端2登录奖励 true:领取  false:未领取
		boolean weiDuanJiangLiGet2 = roleExportService.isWeiDuanJL2(userRoleId);
		data.add(new Object[]{ClientCmdType.ISLQ_WEIDUAN2, weiDuanJiangLiGet2});
		
		//创号时间
		UserRole userRole = roleExportService.getUserRole(userRoleId);
		if(userRole != null){
			data.add(new Object[]{ClientCmdType.CREATE_TIME,userRole.getCreateTime().getTime()});	
		}
		
		Object[] result = roleVipInfoService.getRoleVipGiftState(userRoleId);
		if(result != null){
			data.add(new Object[]{ClientCmdType.ASK_VIP_GIFT_STATE,result});	
		}
		
		//通知客户端服务器数据初始完成
		BusMsgSender.send2One(userRoleId, ClientCmdType.SERVER_INIT_OK,null);//, object);
		
		//数据打包
		Object[] obj = CompressConfigUtil.compressAddCheckObject(data.toArray());
		BusMsgSender.send2One(userRoleId, (short)obj[0], obj[1]);
	}
	
	/**
	 * 角色退出节点
	 * @param roleid
	 */
	public void roleOut(Long roleid) {
		if(!publicRoleStateService.isPublicOnline(roleid)){
			GameLog.error("roleOut roleid check");
			return;
		}
		try{
			//业务下线
			roleOutHandle(roleid);
			//标记正式下线
			publicRoleStateService.change2PublicOffline(roleid);
		}catch (Exception e) {
			GameLog.error("role out error",e);
		}

		// 冻结缓存对象
		if(usecache){
			roleCacheManager.freezeRoleCache(roleid);
		}

	}

    private void roleOutHandle(Long userRoleId) {
        try {
//			/** 处理下线业务 **/
//			fightPowerService.offOnline(userRoleId);
            /** 处理下线业务 **/
            roleBehaviourExportService.offOnline(userRoleId);
            // 下线业务处理（包括防沉迷，设置角色状态为离线）
            roleExportService.logOfflineTime(userRoleId);
            // 处理禁言离线业务
            jinFengExportService.offlineHandle(userRoleId);
            // 下线背包manage缓存数据处理
            roleBagService.clearBagData(userRoleId);
            // 组队下线业务
            teamExportService.offlineHandle(userRoleId);
            // 下线处理对rolebusiness缓存数据处理
            roleBusinessInfoExportService.offlineHandle(userRoleId);
        } catch (Exception e) {
            GameLog.error("", e);
        } finally {
            // 下线业务最后触发业务
            stageControllExportService.exit(userRoleId);
        }
    }
	
	/**
	 * 服务器关闭时角色退出节点
	 * @param roleid
	 */
	public void roleOutOnServerClose(Long roleid) {
		roleOut(roleid);
		
		otherOnServerClose();
	}
	
	private void otherOnServerClose(){
		
	}
}