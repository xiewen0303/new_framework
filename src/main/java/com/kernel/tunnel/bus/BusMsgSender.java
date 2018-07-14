package com.kernel.tunnel.bus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.game.easyexecutor.cmd.CmdGroupInfo;
import com.junyou.constants.GameConstants;
import com.junyou.utils.MsgPrintUtil;
import com.kernel.tunnel.gs.GsMsgDispatcher;
import com.kernel.tunnel.gs.GsMsgSwap;

@Component
public class BusMsgSender {
	
	private static GsMsgSwap gsMsgSender;
	
	private static BusMsgDispatcher busMsgDispatcher;

	private static GsMsgDispatcher gsMsgDispatcher;
	

	@Autowired
	public void setGsMsgSender(GsMsgSwap gsMsgSwap) {
		BusMsgSender.gsMsgSender = gsMsgSwap;
	}
	
	@Autowired
	public void setBusMsgDispatcher(BusMsgDispatcher busMsgDispatcher) {
		BusMsgSender.busMsgDispatcher = busMsgDispatcher;
	}
	
	@Autowired
	public void setGsMsgDispatcher(GsMsgDispatcher gsMsgDispatcher) {
		BusMsgSender.gsMsgDispatcher = gsMsgDispatcher;
	}

	/**
	 * 发送给单个客户端
	 * @param userid
	 * @param command
	 * @param result
	 */
	public static void send2OneBySessionId(long sessionid,String userId, String command,Object result){
		Object[] msg = new Object[]{command,result,CmdGroupInfo.target_type_client,CmdGroupInfo.source_type_bus,CmdGroupInfo.broadcast_type_1,sessionid,null,userId,null,0};
		MsgPrintUtil.printOutMsg(msg,MsgPrintUtil.BUS_PRINT,MsgPrintUtil.BUS_PREFIX);
		gsMsgSender.swap(msg);
	}
	
	/**
	 * 发送给单个客户端
	 * @param userid
	 * @param changeStage
	 * @param result
	 */
	public static void send2One(Long userid, Short command,Object result){
		Object[] msg = new Object[]{command,result,CmdGroupInfo.target_type_client,CmdGroupInfo.source_type_bus,CmdGroupInfo.broadcast_type_1,null,null,userid,null,1};
		MsgPrintUtil.printOutMsg(msg,MsgPrintUtil.BUS_PRINT,MsgPrintUtil.BUS_PREFIX);
		gsMsgSender.swap(msg);
	}
	
	
	
	/**
	 * 发送给所有客户端
	 * @param command
	 * @param data
	 */
	public static void send2All(Short command, Object data){
		Object[] msg = new Object[]{command,data,CmdGroupInfo.target_type_client,CmdGroupInfo.source_type_bus,CmdGroupInfo.broadcast_type_3,null,null,GameConstants.SEND_ALL_GUID,null,1};
		MsgPrintUtil.printOutMsg(msg,MsgPrintUtil.BUS_PRINT,MsgPrintUtil.BUS_PREFIX);
		gsMsgSender.swap(msg);
	}
	
	/**
	 * 发送给多个客户端
	 * @param userRoleIds
	 * @param command
	 * @param data
	 */
	public static void send2Many(Object[] userRoleIds, Short command,Object result){
		if(userRoleIds == null || userRoleIds.length == 0){
			return ;
		}
		if(command == null){
			throw new RuntimeException("bus cmd == null");
		}
		Object[] msg = new Object[]{command,result,CmdGroupInfo.target_type_client,CmdGroupInfo.source_type_bus,CmdGroupInfo.broadcast_type_2,null,null,userRoleIds,null,1};
		MsgPrintUtil.printOutMsg(msg,MsgPrintUtil.BUS_PRINT,MsgPrintUtil.BUS_PREFIX);
		gsMsgSender.swap(msg);
	}
	
	/**
	 * 发送给场景组件的单个目标
	 * @param userid
	 * @param command
	 * @param result
	 */
	public static void send2Stage(Long userid, Short command,Object result){
		Object[] msg = new Object[]{command,result,CmdGroupInfo.target_type_stage,CmdGroupInfo.source_type_bus,CmdGroupInfo.broadcast_type_1,null,null,userid,null,1};
		MsgPrintUtil.printOutMsg(msg,MsgPrintUtil.BUS_PRINT,MsgPrintUtil.BUS_PREFIX);
		gsMsgDispatcher.in(msg);                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    
	}

	/**
	 * bus组件内部消息发送
	 * @param userid
	 * @param command
	 * @param result
	 */
	public static void send2BusInner(Long userid, short command,Object result){
		busMsgDispatcher.in(new Object[]{command,result,CmdGroupInfo.target_type_bus,CmdGroupInfo.source_type_bus,null,null,null,userid,null,1});
	}

	/**
	 * bus组件内部消息发送(带token值)
	 * @param userid
	 * @param command
	 * @param result
	 * @param token [identity,token]
	 */
	public static void send2BusInnerToken(Long userid, Short command,Object result,Object[] token){
		busMsgDispatcher.in(new Object[]{command,result,CmdGroupInfo.target_type_bus,CmdGroupInfo.source_type_bus,null,null,null,userid,null,1,token});
	}

	/**
	 * 发送给角色业务初始化组件
	 * @param userid
	 * @param command
	 * @param result
	 */
	public static void send2BusInit(Long userid, Short command,Object result){
		busMsgDispatcher.in(new Object[]{command,result,CmdGroupInfo.target_type_bus_init,CmdGroupInfo.source_type_bus,null,null,null,userid,null,1});
	}

	/**
	 * 发送给公共组件的单个目标
	 * @param userid
	 * @param command
	 * @param result
	 */
	public static void send2Public(Short command,Object result){
		Object[] msg = new Object[]{command,result,CmdGroupInfo.target_type_public,CmdGroupInfo.source_type_gs,CmdGroupInfo.broadcast_type_1,null,null,GameConstants.DEFAULT_ROLE_ID,null,1};
		MsgPrintUtil.printOutMsg(msg,MsgPrintUtil.STAGE_PRINT,MsgPrintUtil.STAGE_PREFIX);
		gsMsgSender.swap(msg);
	}
	
}
