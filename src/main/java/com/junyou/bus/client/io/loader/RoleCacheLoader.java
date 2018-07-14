package com.junyou.bus.client.io.loader;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.junyou.bus.account.entity.RoleAccount;
import com.junyou.bus.account.service.RoleAccountService;
import com.junyou.bus.bag.entity.RoleItem;
import com.junyou.bus.bag.export.RoleBagExportService;
import com.junyou.bus.email.entity.EmailRelation;
import com.junyou.bus.email.service.EmailRelationService;
import com.junyou.bus.role.entity.UserRole;
import com.junyou.bus.role.service.UserRoleService;
import com.junyou.bus.rolebusiness.entity.RoleBusinessInfo;
import com.junyou.bus.rolebusiness.service.RoleBusinessInfoService;
import com.junyou.bus.rolestage.entity.RoleStage;
import com.junyou.bus.rolestage.export.RoleStageExportService;
import com.junyou.bus.skill.entity.RoleSkill;
import com.junyou.bus.skill.export.RoleSkillExportService;
import com.kernel.data.cache.EntityCache;
import com.kernel.data.cache.IEntityCache;
import com.kernel.data.cache.IEntityCacheLoader;

@Component("roleCacheLoader")
public class RoleCacheLoader implements IEntityCacheLoader {
	@Autowired
	private RoleBagExportService roleBagExportService;
	@Autowired
	private UserRoleService userRoleExportService;
	@Autowired
	private RoleAccountService accountExportService;
	@Autowired
	private RoleStageExportService roleStageExportService; 
//	@Autowired
//	private GoodsUseLimitService goodsUseLimitService;
//	@Autowired
//	private TaskExportService taskExportService;
	@Autowired
	private EmailRelationService emailRelationExportService;
	@Autowired
	private RoleSkillExportService roleSkillExportService;
//	@Autowired
//	private FubenExportService fubenExportService;
//	@Autowired
//	private MoreFubenExportService moreFubenExportService;
//	@Autowired
//	private JingjiExportService jingjiExportService;
	@Autowired
	private RoleBusinessInfoService roleBusinessInfoExportService;
//	@Autowired
//	private ZuoQiExportService zuoQiExportService;
//	@Autowired
//	private TaskDayExportService taskDayExportService;
//	@Autowired
//	private ChiBangExportService chiBangExportService;
//	@Autowired
//	private QiLingExportService qilingExportService;
//	@Autowired
//	private TianYuExportService tianyuExportService;
//	@Autowired
//	private XianJianExportService xianJianExportService;
//	@Autowired
//	private TangbaoExportService tangbaoExportService;
//	@Autowired
//	private GiftCardExportService giftCardExportService;
//	@Autowired
//	private RefbRoleShouchongExportService refbRoleShouchongExportService;
//	@Autowired
//	private XuanTieDuiHuanExportService xuanTieDuiHuanExportService;
//	@Autowired
//	private KaiFuHuoDongExportService kaiFuHuoDongExportService;
//	@Autowired
//	private RfbXiuXianExportService rfbXiuXianExportService;
//	@Autowired
//	private ShenQiExportService shenQiExportService;
//	@Autowired
//	private HuajuanExportService huajuanExportService;
//	@Autowired
//	private HuanhuaExportService huanhuaExportService;
//	@Autowired
//	private KuafuArena1v1SourceExportService kuafuArena1v1ExportService;
//	@Autowired
//	private KuafuArena4v4SourceExportService kuafuArena4v4ExportService;
//	@Autowired
//	private BaguaExportService baguaExportService;
//	@Autowired
//	private MaiguExportService maiguExportService;
//	@Autowired
//	private ChongwuExportService chongwuExportService;
//	@Autowired
//	private YabiaoExportService yabiaoExportService;
//	@Autowired
//	private WenquanExportService wenquanExportService;
//	@Autowired
//	private YaoshenExportService yaoshenExportService;
//	@Autowired
//	private XinwenExportService xinwenExportService;
//	@Autowired
//	private TongtianRoadExportService tongtianLoadExportService;
//	@Autowired
//	private CuxiaoExportService cuxiaoExportService;
//	@Autowired
//	private ChengShenExportService chengShenExportService;
//	@Autowired
//	private YaoshenHunpoExportService yaoshenHunpoExportService;
//	@Autowired
//	private YaoshenMoYinExportService yaoshenMoYinExportService;
//	@Autowired
//	private YaoshenFumoExportService yaoshenFumoExportService;
//	@Autowired
//	private ChenghaoExportService chenghaoExportService;
//	@Autowired
//	private QqExportService qqExportService;
//	@Autowired
//	private QiPanExportService qiPanExportService;
//	@Autowired
//	private LeiChongExportService leiChongExportService;
//	@Autowired
//	private XkbzExportService xkbzExportService;
//	@Autowired
//	private LianChongExportService lianChongExportService;
//	@Autowired
//	private OnlineRewardsExportService onlineRewardsExportService;
//	@Autowired
//	private JewelExportService jewelExportService;
//	@Autowired
//	private FlowerSendExportService flowerSendExportService;
//	@Autowired
//	private FlowerCharmRankExportService flowerCharmRankExportService;
//	@Autowired
//	private ShenMiShangDianExportService shenMiShangDianExportService;
//	@Autowired
//	private RefabuXiaoFeiService refabuXiaoFeiService;
//	@Autowired
//	private RoleTouziExportService roleTouziExportService;
//	@Autowired
//	private ZhuanPanExportService zhuanPanExportService;
//	@Autowired
//	private LunPanExportService lunPanExportService;
//	@Autowired
//	private ZhanJiaExportService zhanJiaExportService;
//	@Autowired
//	private LingHuoExportService lingHuoExportService;
//	@Autowired
//	private TerritoryExportService territoryExportService;
//	@Autowired
//	private RoleChengJiuExportService roleChengJiuExportService;
//	@Autowired
//	private RenWuJiShiExportService renWuJiShiExportService;
//	@Autowired
//	private RefabuActivityExportService refabuActivityExportService;
//	@Autowired
//	private RfbSuperDuihuanExportService superDuihuanExportService;
//	@Autowired
//	private HappyCardExportService happyCardExportService;
//	@Autowired
//	private SuoYaoTaExportService suoYaoTaExportService;
//	@Autowired
//	private RoleShiZhuangExportService roleShiZhuangExportService;
//	@Autowired
//	private TencentLuoPanExportService tencentLuoPanExportService;
//	@Autowired
//	private TencentShangDianExportService tencentShangDianExportService;
//	@Autowired
//	private MarryExportService marryExportService;
//	@Autowired
//	private RechargeFanliExportService rechargeFanliExportService;
//	@Autowired
//	private CuilianExportService cuilianExportService;
//	@Autowired
//	private FushuSkillExportService fushuSkillExportService;
//	@Autowired
//	private LeiHaoExportService leiHaoExportService;
//	@Autowired
//	private RoleCaidanExportService roleCaidanExportService;
//	@Autowired
//	private TaiWanExportService taiWanExportService;
//	@Autowired
//	private RoleTaFangExportService roleTaFangExportService;
//	@Autowired
//	private KuafuJingjiExportService kuafuJingjiExportService;
//	@Autowired
//	private RefbXunBaoExportService refbXunBaoExportService;
//	@Autowired
//	private TanSuoBaoZangExportService tanSuoBaoZangExportService;
//	@Autowired
//	private ZhuanshengExportService zhuanshengExportService;
//	@Autowired
//	private LianyuBossExortService lianyuBossExortService;
//	@Autowired
//	private YuenanExportService yuenanExportService;
//	@Autowired
//	private TuanGouExportService tuanGouExportService;
//	@Autowired
//	private RefabuSevenLoginExportService refabuSevenLoginExportService;
//	@Autowired
//	private RefbMiaoshaExportService refbMiaoshaExportService;
//	@Autowired
//	private RefbLaowanjiaExportService refbLaowanjiaExportService;
//	@Autowired
//	private WuXingMoShenExportService wuXingMoShenExportService;
//	@Autowired
//	private RefabuFirstChargeRebateExportService refabuFirstChargeRebateExportService;
//	@Autowired
//	private XinmoExportService xinmoExportService;
//    @Autowired
//    private RefabuJuebanExportService refabuJuebanExportService;
//    @Autowired
//    private Huajuan2ExportService huaJuan2ExportService;
//    @Autowired
//    private RoleMoGongLieYanExportService roleMoGongLieYanExportService;
//    @Autowired
//    private XianqiServiceExport xianqiServiceExport;
//    @Autowired
//    private XianqiFubenServiceExport xianqiFubenServiceExport;
//    @Autowired
//    private XianYuanFeiHuaServiceExport xianYuanFeiHuaServiceExport;
//    @Autowired
//    private RfbExtremeRechargeExportService rfbExtremeRechargeExportService;
//    @Autowired
//    private ShenQiJinJieExportService shenQiJinJieExportService;
//    @Autowired
//    private ShenQiEquipExportService shenQiEquipExportService;
//    @Autowired
//	private RolePersonalBossExportService rolePersonalBossExportService;
//    @Autowired
//    private RoleXiangweiExportService roleXiangweiExportService;
//    @Autowired
//    private RoleBossJifenExportService bossJifenExportService;
//    @Autowired
//    private WuQiExportService wuQiExportService;
//    @Autowired
//    private OnceChongExportService onceChongExportService;
//    @Autowired
//    private TaskBranchService taskBranchService;
//    @Autowired
//    private XiuLianExportService xiuLianExportService;
//    @Autowired
//    private ShopLimitExportService shopLimitExportService;
//    @Autowired
//    private LJExportService lJExportService;
    
	@Override
	public IEntityCache loadEntityCache(Long userRoleId) {
		IEntityCache entityCache = new EntityCache(userRoleId);
		
		//玩家角色
		UserRole userRole = userRoleExportService.initUserRole(userRoleId);
		entityCache.addModelData(userRole, UserRole.class);
		//玩家角色货币信息
		RoleAccount roleAccount = accountExportService.initRoleAccountFromDb(userRoleId);
		entityCache.addModelData(roleAccount, RoleAccount.class);
		//角色场景信息
		RoleStage roleStage = roleStageExportService.getRoleStageFromDb(userRoleId);
		entityCache.addModelData(roleStage, RoleStage.class);
		//加载背包数据
		List<RoleItem> roleItems=roleBagExportService.initAll(userRoleId);
		if(roleItems.size() > 0){
			entityCache.addModelData(roleItems, RoleItem.class);
		}
		//加载个人业务数据
		RoleBusinessInfo roleBusinessInfo = roleBusinessInfoExportService.getRoleBusinessInfoForDB(userRoleId);
		entityCache.addModelData(roleBusinessInfo, RoleBusinessInfo.class);
//		//加载背包描述信息
// 		RoleItemDesc  roleItemDesc = roleBagExportService.initDescAll(userRoleId);
//		if(roleItemDesc!=null){
//			entityCache.addModelData(roleItemDesc, RoleItemDesc.class);
//		}
//		//加载每日使用次数限制
//		List<RoleItemUseCsxz> roleItemUseCsxzs=goodsUseLimitService.initAll(userRoleId);
//		if(roleItemUseCsxzs!=null){
//			entityCache.addModelData(roleItemUseCsxzs, RoleItemUseCsxz.class);
//		}
//		//加载主线任务
//		List<Task> task = taskExportService.initTask(userRoleId);
//		if(task != null){
//			entityCache.addModelData(task, Task.class);
//		}
		//加载邮件
		List<EmailRelation> email = emailRelationExportService.initEmailRelation(userRoleId);
		if(email != null){
			entityCache.addModelData(email, EmailRelation.class);
		}
		//技能
		List<RoleSkill> skill = roleSkillExportService.initRoleSkill(userRoleId);
		if(skill != null){
			entityCache.addModelData(skill, RoleSkill.class);
		}
		//糖宝技能格位
		return entityCache;
	}

}
