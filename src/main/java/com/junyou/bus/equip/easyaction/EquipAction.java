package com.junyou.bus.equip.easyaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.game.easyexecutor.annotation.EasyMapping;
import com.game.easyexecutor.annotation.EasyWorker;
import com.junyou.bus.equip.service.EquipService;
import com.junyou.cmd.ClientCmdType;
import com.junyou.module.GameModType;
import com.junyou.utils.common.CovertObjectUtil;
import com.junyou.utils.number.LongUtils;
import com.kernel.pool.executor.Message;
import com.kernel.tunnel.bus.BusMsgQueue;
import com.kernel.tunnel.bus.BusMsgSender;
 
/**
 * 装备
 */
@Controller
@EasyWorker(moduleName = GameModType.EQUIP_MODULE)
public class EquipAction{
	
	@Autowired
	private EquipService equipService;
	
//	/**
//	 * 换装
//	 * @param inMsg
//	 */
//	@EasyMapping(mapping=ClientCmdType.CHANGE_EQUIP)
//	public void changeEquip(Message inMsg){
//		Long userRoleId = inMsg.getRoleId();
//		Object[] data = inMsg.getData();
//		long guid = CovertObjectUtil.obj2long(data[0]); 
//		int targetSlot = (Integer)data[1];
//		
//		Object[] result = equipService.changeEquip(userRoleId,guid,targetSlot);
//		BusMsgSender.send2One(userRoleId, ClientCmdType.CHANGE_EQUIP, result);
//	}
// 
//	//装备回收
//	@EasyMapping(mapping=ClientCmdType.EQUIP_RECYCLE)
//	public void equipRecycle(Message inMsg){
//		Long userRoleId = inMsg.getRoleId(); 
//		
//		Object[] guids = inMsg.getData();
//		Object[] result=equipService.equipRecycle(userRoleId,guids);
//		
//		BusMsgSender.send2One(userRoleId, ClientCmdType.EQUIP_RECYCLE, result);
//	}
//	
//	//强化
//	@EasyMapping(mapping = ClientCmdType.EQUIP_QH)
//	public void equipQH(Message inMsg) {
//		Long userRoleId = inMsg.getRoleId();
//		Object[] datas = (Object[])inMsg.getData();
//		long guid=LongUtils.obj2long(datas[0]);
//		Boolean isAutoGM =(Boolean)datas[1];
//		
//		BusMsgQueue busMsgQueue=new BusMsgQueue(); 
//		Object[] result=equipService.equipQH(userRoleId,guid,busMsgQueue,isAutoGM);
//		BusMsgSender.send2One(userRoleId, ClientCmdType.EQUIP_QH, result);
//		busMsgQueue.flush();
//	}
//	 
//	//装备升级
//	@EasyMapping(mapping=ClientCmdType.ZHUANGBEI_SHENGJI)
//	public void zhuangbeiShengJi(Message inMsg){
//		Long userRoleId = inMsg.getRoleId(); 
//		Object[] datas = (Object[])inMsg.getData();
//		long guid=LongUtils.obj2long(datas[0]);
//		Boolean isAutoGM =(Boolean)datas[1];
//		
//		BusMsgQueue busMsgQueue=new BusMsgQueue(); 
//		
//		Object[] result=equipService.equipLevelUp(userRoleId, guid,busMsgQueue,isAutoGM);
//		
//		BusMsgSender.send2One(userRoleId, ClientCmdType.ZHUANGBEI_SHENGJI, result);
//		busMsgQueue.flush();
//	}
	 
}
