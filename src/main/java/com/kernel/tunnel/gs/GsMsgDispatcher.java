package com.kernel.tunnel.gs;


import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.game.easyexecutor.cmd.CmdGroupInfo;
import com.junyou.cmd.InnerCmdType;
import com.junyou.constants.GameConstants;
import com.junyou.log.GameLog;
import com.junyou.utils.KuafuCmdUtil;
import com.kernel.tunnel.bus.BusMsgDispatcher;
import com.kernel.tunnel.msgswap.MsgUtil;
import com.kernel.tunnel.msgswap.NodeSwap;
import com.kernel.tunnel.stage.StageMsgDispatcher;

/**
 *	消息中间件分发器
 */

@Component
public class GsMsgDispatcher {
	
	private BusMsgDispatcher busMsgDispatcher;
	private StageMsgDispatcher stageMsgDispatcher;
	
	@Autowired
	public void setBusMsgDispatcher(BusMsgDispatcher busMsgDispatcher) {
		this.busMsgDispatcher = busMsgDispatcher;
	}
	@Autowired
	public void setStageMsgDispatcher(StageMsgDispatcher stageMsgDispatcher) {
		this.stageMsgDispatcher = stageMsgDispatcher;
	}

	public void in(Object message) {
		
		Object[] msg = (Object[])message;
		Short cmd = MsgUtil.getCommand(msg);
		
		Integer targetType = CmdGroupInfo.getCmdTargetType(cmd);
		//指令未注册
		if( targetType == null ){
			GameLog.error("swap2Target method no target node for cmd:{"+cmd+"}"+msg[7]);
			return;
		}
		msg[2] = targetType;
		switch (targetType) {
		case CmdGroupInfo.target_type_bus: // bus
		case CmdGroupInfo.target_type_stage_control: // stage-control
		case CmdGroupInfo.target_type_kuafu_server:
		case CmdGroupInfo.target_type_bus_init:
			busMsgDispatcher.in(msg);
			break;
		
		case CmdGroupInfo.target_type_stage: // stage
			swap2stage(msg);
			break;
		
		case CmdGroupInfo.target_type_client:
			NodeSwap.swap2client(msg);
			break;
			
		default:
			GameLog.errorFrame("no dest for client cmd:"+cmd);
			break;
		}
	}
	

	private ConcurrentMap<Long, String> roleStageInfos = new ConcurrentHashMap<Long, String>();
	private void swap2stage(Object[] msg){
		
		// 填充stageid
		Short cmd = MsgUtil.getCommand(msg);
		Long roleid = MsgUtil.getRoleId(msg);
		
		// 是否为直接转发的指令
		if( KuafuCmdUtil.swapCmdNow(cmd, msg) ){
			NodeSwap.swap2Kuafu(cmd, msg);
			return;
		}
		
		synchronized (getLock(roleid)) {

			if(InnerCmdType.S_Enter_Stage_cmd == cmd){
				Object[] data = (Object[]) MsgUtil.getMsgData(msg);
				roleStageInfos.put(roleid, (String) data[0]);
			}else if(InnerCmdType.S_LEAVE_AFTER_ENTER_CMD == cmd){
				Object[] data = (Object[]) MsgUtil.getMsgData(msg);
				roleStageInfos.put(roleid, (String) data[1]);
			}
			
			String stageid = null;
			if(roleid > 0){
				stageid = roleStageInfos.get(roleid);
				if(null != stageid){
					msg[8] = stageid;
				}else{
					GameLog.error("when in,role is not in stage: "+JSONArray.toJSONString(msg));
					return;
				}
			}else{
				//给系统用户赋值场景id，此场景id不存在对应场景，业务中自行处理
				stageid = GameConstants.DEFAULT_ROLE_STAGEID;
			}
			
			if(InnerCmdType.S_Leave_Stage_cmd == cmd){
				/**
				 * ================待处理  TODO  离开场景协议需要处理，不然场景模块会出问题========================
				 */
				NodeSwap.swap2client(new Object[]{InnerCmdType.leaveStage_cmd,null,CmdGroupInfo.target_type_client,CmdGroupInfo.source_type_gs,CmdGroupInfo.broadcast_type_1,null,null,roleid,stageid,1});
				/**
				 * ================待处理   TODO 离开场景协议需要处理，不然场景模块会出问题========================
				 */
				
				roleStageInfos.remove(roleid);
			}
			
		}
		
		stageMsgDispatcher.in(msg);
	}
	
	private static ConcurrentMap<Long, Object> roleLocks = new ConcurrentHashMap<Long, Object>();
	private static Object getLock(Long roleid){
		
		Object lock = roleLocks.get(roleid);
		if(null == lock){
			synchronized (roleLocks) {
				lock = roleLocks.get(roleid);
				if(null == lock){
					lock = new Object();
					roleLocks.put(roleid, lock);
				}
			}
		}
		return lock;
	}

}
