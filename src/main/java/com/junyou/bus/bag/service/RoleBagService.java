package com.junyou.bus.bag.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.junyou.bus.account.service.RoleAccountService;
import com.junyou.bus.bag.BagOutputWrapper;
import com.junyou.bus.bag.BagSlots;
import com.junyou.bus.bag.ContainerType;
import com.junyou.bus.bag.GoodsSource;
import com.junyou.bus.bag.OutputType;
import com.junyou.bus.bag.configure.export.BagConfigExportService;
import com.junyou.bus.bag.dao.RoleBagDao;
import com.junyou.bus.bag.entity.RoleItem;
import com.junyou.bus.bag.export.RoleItemExport;
import com.junyou.bus.bag.export.RoleItemInput;
import com.junyou.bus.bag.utils.BagUtil;
import com.junyou.bus.bag.vo.RoleItemOperation;
import com.junyou.cmd.InnerCmdType;
import com.junyou.err.AppErrorCode;
import com.junyou.gameconfig.goods.configure.export.GoodsConfig;
import com.junyou.gameconfig.goods.configure.export.GoodsConfigExportService;
import com.junyou.gameconfig.utils.GoodsCategory;
import com.junyou.log.GameLog;
import com.junyou.log.LogPrintHandle;
import com.junyou.public_.share.export.PublicRoleStateExportService;
import com.junyou.utils.common.ObjectUtil;
import com.junyou.utils.number.LongUtils;
import com.kernel.tunnel.bus.BusMsgSender;

/**
 * 提供给BagAction使用的
 */
@Service
public class RoleBagService extends BagBaseService {

	@Autowired
	private RoleBagDao roleBagDao; 
//	@Autowired
//	private GoodsUseLimitService goodsUseLimitService;
	@Autowired
	private BagConfigExportService bagConfigExportService;
	@Autowired
	private RoleAccountService accountExportService;
	@Autowired
	private GoodsConfigExportService goodsConfigExportService;
	@Autowired
	private PublicRoleStateExportService publicRoleStateExportService;
	
	/**
	 * 获得背包物品信息
	 * @param userRoleId
	 * @return
	 */
	public void loginHandle(Long userRoleId) {
//		//处理格位信息
//		onloginHandleSlot(userRoleId);
		
		List<Object> data=new ArrayList<>();
		/**背包信息**/
		getDataByContainerType(userRoleId,ContainerType.BAGITEM,data);
		 
		/**身上装备信息**/
		getDataByContainerType(userRoleId,ContainerType.BODYTITEM,data);
		
//		BusMsgSender.send2One(userRoleId, ClientCmdType.GET_BAG_ITEMS, new Object[]{data.toArray(),limitUseCount,containerDesc.getBagOpeningSlot(),BagUtil.cover2Int(bagKyTimeLong),containerDesc.getStorageOpeningSlot(),BagUtil.cover2Int(storageKyTimeLong)});
 	}
	
	public void getDataByContainerType(long userRoleId,ContainerType type,List<Object> data){ 
		List<RoleItem> roleItems=getContainerItems(userRoleId, type.getType());
		if(roleItems!=null&&roleItems.size()>0){
			for (int i=0;i<roleItems.size();i++) {
					data.add(BagOutputWrapper.getOutWrapperData(OutputType.ITEMVO,roleItems.get(i))); 
			}
		}
	}
	
    /**
     * 获取宠物特有的附属装备vo数据
     * @param userRoleId
     * @param chongwuId
     */
    public List<Object> getChongwuEquipData(long userRoleId, Integer chongwuId) {
        if(null == chongwuId) return null;
        List<Object> data = null;
        List<RoleItem> roleItems = getContainerItems(userRoleId, ContainerType.CHONGWUITEM.getType());
        if (!ObjectUtil.isEmpty(roleItems)) {
            data = new ArrayList<Object>();
            for (RoleItem roleItem : roleItems) {
                if (null != roleItem.getChongwuId() && roleItem.getChongwuId().equals(chongwuId)) {
                    data.add(BagOutputWrapper.getOutWrapperData(OutputType.ITEMVO, roleItem));
                }
            }
        }
        return data;
    }
    
    /**
     * 获取宠物特有的附属装备vo数据
     * @param userRoleId
     * @param chongwuId
     */
    public Object[] getShenQiEquipData(long userRoleId, Integer shenQiId,Integer slot) {
        if(null == shenQiId) return null;
//        List<Object[]> data = null;
        List<RoleItem> roleItems = getContainerItems(userRoleId, ContainerType.SHENQIITEM.getType());
        if (!ObjectUtil.isEmpty(roleItems)) {
//            data = new ArrayList<>();
            for (RoleItem roleItem : roleItems) {
                if (null != roleItem.getShenqiId() && roleItem.getShenqiId().intValue()==shenQiId.intValue() && roleItem.getSlot().equals(slot)) {
                    return new Object[]{roleItem.getGoodsId(),roleItem.getId(),roleItem.getSlot()};
                }
            }
        }
        return null;
    }
	
	private Object[] getData(long userRoleId,ContainerType type){ 
		Object[] result=null;
		List<RoleItem> roleItems=getContainerItems(userRoleId, type.getType());
		if(roleItems!=null&&roleItems.size()>0){ 
			result=new Object[roleItems.size()];
			for (int i=0;i<roleItems.size();i++) { 
				result[i]=BagOutputWrapper.getOutWrapperData(OutputType.ITEMVO,roleItems.get(i)); 
			}
		}
		return result;
	}
	
	
	public Object[] clearUpBag(Long userRoleId) {
		BagSlots bagSlots=resetContainerSlot(ContainerType.BAGITEM, userRoleId);
		if(!bagSlots.isSuccee()){
			return null;
		}
		
		List<RoleItemOperation> roleItemVos=bagSlots.getRoleItemVos();
		
		Object[] result=null;
		if(roleItemVos!=null){
			result=new Object[roleItemVos.size()]; 
			for (int i=0;i<roleItemVos.size();i++) {
				result[i]=BagOutputWrapper.getOutWrapperData(OutputType.SLOTMODIFY, roleItemVos.get(i));
			}
		}
		
		return result;
	} 
	
	public Object[] clearUpStorage(Long userRoleId) {
		BagSlots bagSlots=resetContainerSlot(ContainerType.STORAGEITEM, userRoleId);
		if(!bagSlots.isSuccee()){
			return null;
		}
		
		List<RoleItemOperation> roleItemVos=bagSlots.getRoleItemVos();
		Object[] result=null;
		if(roleItemVos!=null){
			result=new Object[roleItemVos.size()];	
			for (int i=0;i<roleItemVos.size();i++) {
				result[i]=BagOutputWrapper.getOutWrapperData(OutputType.SLOTMODIFY, roleItemVos.get(i));
			}
		}
		return result;
	}
	
	
	public Object[] moveGoods2Slot(Long userRoleId, Integer targetSlot,long goodsPkId){
		RoleItem roleItem=roleBagDao.cacheLoad(goodsPkId, userRoleId);
		if(roleItem==null){
			return AppErrorCode.NOT_FOUND_GOOODS;
		}
		
		if(roleItem.getSlot().equals(targetSlot)){
			return AppErrorCode.NOT_MOVE;
		}
		int targetContainerType=getContainerTypeBySlot(targetSlot, userRoleId);
		
        BagSlots bagSolts = moveSlot(goodsPkId, targetSlot, targetContainerType, userRoleId, 0);
		if(!bagSolts.isSuccee()){
			return bagSolts.getErrorCode();
		}
		Object[] result=new Object[3];
		result[0]=1;
		List<RoleItemOperation> roleItemvos=bagSolts.getRoleItemVos();
		if(roleItemvos!=null){
			for (int i=0;i<roleItemvos.size();i++) { 
				result[i+1]=BagOutputWrapper.getOutWrapperData(OutputType.SLOTMODIFY,roleItemvos.get(i));
			}
		}
		return result;
	}
	
//	public Object[] moveGoods2Container(Long userRoleId, int containerType,long goodsPkId) {
//		BagSlots bagSlots=bagService.moveSlot(goodsPkId, null, containerType, userRoleId);
//		
//		if(!bagSlots.isSuccee()){
//			return bagSlots.getErrorCode();
//		}
//		Object[] result=new Object[3];
//		result[0]=1;
//		List<RoleItemVo> roleItemvos=bagSlots.getRoleItemVos();
//		for (int i=0;i<roleItemvos.size();i++) {
//			result[i+1]=BagOutputWrapper.formartModify(roleItemvos.get(i));
//		}
//		return result;
//	}
	 
	public Object[] useBagItem(Long userRoleId, long goodsPkId, int count) {
		BagSlots bagSlots=removeBagItemByGuid(goodsPkId, count, userRoleId, 0,true,true);
		if(!bagSlots.isSuccee()){
			return bagSlots.getErrorCode();
		}
		return null;
	}
	
	public Object[] bagDestroyItem(Long userRoleId, long goodsPkId) {
		BagSlots  bagSlots=destroyBagItemByGuid(goodsPkId, userRoleId, GoodsSource.GOODS_DROP_ITEMS, true,false);
		if(!bagSlots.isSuccee()){
			return bagSlots.getErrorCode();
		}
		return new Object[]{1,bagSlots.getRoleItemVos().get(0).getRoleItem().getId()};
	}
	
	public Object[] bagGiveUp(Long userRoleId, long guid) {
		RoleItem targetRoleItem = getItemByGuid(userRoleId, guid, ContainerType.BAGITEM);
		if(targetRoleItem == null){
			return AppErrorCode.NOT_FOUND_GOOODS;
		}
		
		//判定是否绑定物品
		GoodsConfig config = goodsConfigExportService.loadById(targetRoleItem.getGoodsId());
		if(config == null){
			GameLog.error("物品或装备配置表异常:goodsId="+targetRoleItem.getGoodsId());
			return AppErrorCode.NO_FIND_CONFIG;
		}
		
		if(config.isBind()){
			return AppErrorCode.NO_GIVE_BIND;
		}
		
		BagSlots  bagSlots=removeBagItemByGuid(guid, userRoleId, GoodsSource.GIVE_UP, true,false);
		if(!bagSlots.isSuccee()){
			return bagSlots.getErrorCode();
		}
		RoleItem roleItem=null;
		if(bagSlots.getRoleItemVos()!=null&&bagSlots.getRoleItemVos().size()>0){
			roleItem=bagSlots.getRoleItemVos().get(0).getRoleItem();
			if(roleItem == null){
				GameLog.error("丢弃物品异常,玩家背包没有找到对应的物品,userRoleId:"+userRoleId+"\t guid:"+guid);
				return AppErrorCode.BAG_GVIEUP_FAIL;
			}
			//丢入场景之中
			RoleItemExport roleItemExport = BagUtil.converRoleItemExport(roleItem); 
			Object[] dropGoods=new Object[]{ BagUtil.cover2Goods(roleItemExport) };
			Object[] data = new Object[]{userRoleId, null, null, null, dropGoods, 0, 0,null,userRoleId};
			roleBagDao.qzFlushCache(userRoleId);
			BusMsgSender.send2Stage(userRoleId, InnerCmdType.DROP_GOODS, data);
		}else{
			GameLog.error("丢弃物品异常,userRoleId:"+userRoleId+"\t guid:"+guid);
			return AppErrorCode.BAG_GVIEUP_FAIL;
		}
		
		return new Object[]{1,roleItem.getId()};
	}
	
	/**
	 * 拆分物品
	 * @param userRoleId
	 * @param goodsPkId
	 * @param targetCount
	 * @return
	 */
	public Object[] splitGoods(Long userRoleId, long guid, int targetCount,int slot) {
		 BagSlots bagSlots=super.splitGoods(userRoleId, guid, targetCount, slot,GoodsSource.SPLIT_GOODS);
		 if(!bagSlots.isSuccee()){
			return bagSlots.getErrorCode();
		 } 
		 Object[] result=new Object[3];
		 result[0]=1;
		 List<RoleItemOperation> roleItemVos=bagSlots.getRoleItemVos();
		 for(int i=0;i<bagSlots.getRoleItemVos().size();i++) { 
			 result[i+1]=BagOutputWrapper.getOutWrapperData(OutputType.CHAIFEN, roleItemVos.get(i));
		 }
		return result;
	}
	
	/**
	 * 出售物品给npc
	 * @param userRoleId
	 * @param guid
	 * @return
	 */
	public Object[] sellItem2Npc(Long userRoleId, Long guid) {
		BagSlots bagSlots=removeBagItemByGuid(guid, userRoleId, GoodsSource.NPCSELL, true, true);
		if(!bagSlots.isSuccee()){
			return bagSlots.getErrorCode();
		}
		RoleItem roleItem=bagSlots.getRoleItemVos().get(0).getRoleItem();
	 
		/**
		 * 物品出售的价格
		 */
		int price = BagUtil.getSellPriceByItem(roleItem.getGoodsId());
		if(price <= 0){
			return AppErrorCode.GOODS_NO_SELL;
		}
		
		if(price*roleItem.getCount() > 0){
			accountExportService.incrCurrencyWithNotify(GoodsCategory.MONEY, price*roleItem.getCount(), userRoleId, LogPrintHandle.GET_SELL_GOODS, LogPrintHandle.GBZ_SELL_GOODS);
		}
		
		return AppErrorCode.OK;
	}
	
	/**
	 * 绑定于非绑定合并
	 * @param userRoleId
	 * @param targetSlot
	 * @param goodsPkId
	 * @return
	 */
	public Object[] moveGoodsID1ToSlot(Long userRoleId, Integer targetSlot,long guid){
		BagSlots bagSlots=moveGoodsID1(userRoleId,targetSlot,guid);
		if(!bagSlots.isSuccee()){
			return bagSlots.getErrorCode();
		}
		Object[] result=new Object[3];
		result[0]=1;
		List<RoleItemOperation> itemVos=bagSlots.getRoleItemVos();
		for (int i=0;i<itemVos.size();i++){
			result[i+1]=BagOutputWrapper.getOutWrapperData(OutputType.SLOTMODIFY, itemVos.get(i));
		}
		return result;
	}
	
	public void clearBagData(long userRoleId){
		offOnline(userRoleId);
	}
	
	/**
	 * 变化属性通知场景
	 * @param openSlot	已经开启的格位
	 */
	private void notifyStage(long userRoleId,Map<String,Long> attrs){
		BusMsgSender.send2Stage(userRoleId, InnerCmdType.INNER_SLOT_ATTRS, attrs);
	}
	
	
	/**
	 * 拾取物品进背包
	 * @param userRoleId
	 * @param goods
	 * @return
	 */
	public Object[] takeGoods(Long userRoleId, RoleItemInput roleItemInput) {
		BagSlots bagSlots = putInBag(roleItemInput, userRoleId,GoodsSource.STAGE_TAKE_GOODS,true);
		
		if(bagSlots.getErrorCode() != null){
			//拾取一个物品失败,再次放回场景
//			Object[] dropGoods=new Object[]{BagUtil.cover2Goods(roleItemInput) };
//			Object[] data = new Object[]{userRoleId, null, null, null, dropGoods, 0, 0,null,userRoleId};
//			BusMsgSender.send2Stage(userRoleId, InnerCmdType.DROP_GOODS, data);
		}
		
		return bagSlots.getErrorCode();
	}
	
	/**
	 * 拾取数值类型进背包(来自跨服)
	 * @param userRoleId
	 * @param goods
	 * @return
	 */
	public void takeGoodsKF(Long userRoleId, Object[] goods) {
		String goodsId = (String)goods[0];
		GoodsConfig goodsConfig = goodsConfigExportService.loadById(goodsId);
		if(goodsConfig.getCategory() >= 0){
			return;
		}
		incrNumberWithNotify(goodsConfig.getCategory(), LongUtils.obj2long(goods[1]), userRoleId, LogPrintHandle.GET_GOODS_PICKUP, LogPrintHandle.GBZ_GOODS_PICKUP);
	}

	/**
	 * 客户端请求物品展示
	 * @param userRoleId
	 * @param id
	 * @return
	 */
	public Object[] goodsZhanshi(Long userRoleId, Long id) {
		if(!publicRoleStateExportService.isPublicOnline(userRoleId)){
			return AppErrorCode.LINK_IS_OUTTIME;
		}
		
		RoleItem roleItem = null;
		for (ContainerType type : ContainerType.values()) {
			roleItem = getItemByGuid(userRoleId, id, type);
			if(roleItem != null){
				break;
			}
		}
		if(roleItem == null){
			return AppErrorCode.LINK_IS_OUTTIME;
		}
		return new Object[]{AppErrorCode.SUCCESS, BagOutputWrapper.formart(roleItem)};
	}
	
	/**
	 * 是否是套装
	 * @param goodsId
	 * @return
	 */
	public boolean isTaoZhuang(String goodsId){
		GoodsConfig config = goodsConfigExportService.loadById(goodsId);
		if(config.getSuit() != 0){
			return true;
		}
		return false;
	}
	/**
	 * 是否是紫装以上
	 * @param goodsId
	 * @return
	 */
	public boolean isZiZhuang(String goodsId){
		GoodsConfig config = goodsConfigExportService.loadById(goodsId);
		if(config.getRareLevel() >= 3){
			return true;
		}
		return false;
	}
	/**
	 * 是否是橙装以上
	 * @param goodsId
	 * @return
	 */
	public boolean isChengZhuang(String goodsId){
		GoodsConfig config = goodsConfigExportService.loadById(goodsId);
		if(config.getRareLevel() >= 4){
			return true;
		}
		return false;
	}
}