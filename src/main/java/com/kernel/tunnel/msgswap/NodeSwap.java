package com.kernel.tunnel.msgswap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.game.easyexecutor.cmd.CmdGroupInfo;
import com.junyou.constants.GameConstants;
import com.junyou.kuafu.manager.KuafuRoleServerManager;
import com.junyou.kuafu.share.util.KuafuConfigUtil;
import com.junyou.kuafu.share.util.KuafuServerInfo;
import com.junyou.log.GameLog;
import com.junyou.utils.KuafuCmdUtil;
import com.kernel.tunnel.kuafu.KuafuNetTunnel;

/**
 * 负责节点间消息分发 
 */
@Component
public class NodeSwap {

	/**
	 * IoMsgTunnel
	 */
	private static ISwapTunnel ioMsgTunnel;

	/**
	 * PublicMsgTunnel
	 */
	private static ISwapTunnel publicMsgTunnel;

	/**
	 * GsMsgTunnel
	 */
	private static ISwapTunnel gsMsgTunnel;
	
	@Autowired
	public NodeSwap(@Qualifier("ioMsgTunnel") ISwapTunnel ioMsgTunnel,
			@Qualifier("publicMsgTunnel") ISwapTunnel publicMsgTunnel,
			@Qualifier("gsMsgTunnel")ISwapTunnel gsMsgTunnel) {
		NodeSwap.ioMsgTunnel = ioMsgTunnel;
		NodeSwap.publicMsgTunnel = publicMsgTunnel;
		NodeSwap.gsMsgTunnel = gsMsgTunnel;
	}
	/**
	 * 分发消息到合适的节点
	 * @param msg
	 */
	public static void swap(Object[] msg){
		try{
			int fromType = MsgUtil.getSourceType(msg);
			switch (fromType) {
			case CmdGroupInfo.source_type_client:
			case CmdGroupInfo.source_type_kuafu_source:
				clientMsgSwap(msg);
				break;
			case CmdGroupInfo.source_type_bus:
			case CmdGroupInfo.source_type_public:
				publicMsgSwap(msg);
				break;
				
			case CmdGroupInfo.source_type_stage:
			case CmdGroupInfo.source_type_gs:
			case CmdGroupInfo.source_type_kuafu_server:
				gsMsgSwap(msg);
				break;
			}

		}catch (Exception e) {
			GameLog.error("exception when swap "+MsgUtil.getCommand(msg),e);
		}
		
		
	}
	
	/**
	 * 分发来自客户端的消息
	 * @param msg
	 */
	public static void clientMsgSwap(Object[] msg){
		
		Short cmd = MsgUtil.getCommand(msg);
		
		//如果是跨服禁止的指令
		if( KuafuCmdUtil.isForbidCmds(cmd, msg) ){
			return;
		}
		
		if( KuafuCmdUtil.swapCmdNow(cmd, msg) ){
			swap2Kuafu(cmd, msg);
			return;
		}
		
		//指令未注册
		Integer targetType = CmdGroupInfo.getCmdTargetType(cmd);
		if( targetType == null ){
			GameLog.error("no dest node for client1 cmd:{"+cmd+"}"+msg[7]);
			return;
		}
		
		msg[2] = targetType;
		switch (targetType) {
		case CmdGroupInfo.target_type_public: // public
		case CmdGroupInfo.target_type_inout: // in-out(如登录login_group)
		case CmdGroupInfo.target_type_bus_init: // bus-init (登录后跳转到其他加载数据的线程  bus_init_group)
			swap2public(msg);
			break;
		case CmdGroupInfo.target_type_bus: // bus
		case CmdGroupInfo.target_type_stage: // stage
		case CmdGroupInfo.target_type_stage_control: // stage-control
		case CmdGroupInfo.target_type_kuafu_server: // kuafu server目标服，即跨服
			swap2gs(msg);
			break;
		default:
			GameLog.error("no dest node for client2 cmd:"+cmd);
			break;
		}
	}

	/**
	 * 分发来自游戏服务器节点的消息
	 */
	public static void gsMsgSwap(Object[] msg){
		componentMsgSwap(msg);
	}
	
	/**
	 * 分发来自公共服务节点的消息 
	 */
	public static void publicMsgSwap(Object[] msg){
		componentMsgSwap(msg);
	}
	
	private static void componentMsgSwap(Object[] msg){
		
		int destType = MsgUtil.getDestType(msg);
		switch (destType) {
		case CmdGroupInfo.target_type_client:
			swap2client(msg);
			break;
		case CmdGroupInfo.target_type_bus_init:
		case CmdGroupInfo.target_type_bus:
		case CmdGroupInfo.target_type_stage_control:
		case CmdGroupInfo.target_type_stage:
		case CmdGroupInfo.target_type_kuafu_server:
			swap2gs(msg);
			break;
		case CmdGroupInfo.target_type_public: // public
			swap2public(msg);
			break;
		default:
			GameLog.error("no dest for component cmd:"+MsgUtil.getCommand(msg));
		}

	}
	
	/**
	 * 分发消息到公共服务节点
	 * @param msg
	 */
	private static void swap2public(Object[] msg){
		publicMsgTunnel.receive(msg);
	}
	
	/**
	 * 分发消息到游戏服务器节点
	 * @param msg
	 */
	private static void swap2gs(Object[] msg){
		Short cmd = MsgUtil.getCommand(msg);
		
		// 是否为直接转发的指令
		if( KuafuCmdUtil.swapCmdNow(cmd, msg) ){
			swap2Kuafu(cmd, msg);
			return;
		}
		
		gsMsgTunnel.receive(msg);
	}
	
	/**
	 * 分发消息到跨服服务器
	 * @param msg
	 */
	public static void swap2Kuafu(Short cmd, Object[] msg){
//		if(LOG.isDebugEnabled()){
//			LOG.debug("kuafu-"+JSON.toJSONString(msg));
//		}
		
		KuafuNetTunnel tunnel = null;
		boolean returned = false;
		try{
			Long userRoleId = MsgUtil.getRoleId(msg);
			if(userRoleId.longValue()==GameConstants.DEFAULT_ROLE_ID.longValue()){
				GameLog.error("DEFAULT_ROLE_ID cmd = {}",cmd);
				return;
			}
			KuafuServerInfo serverInfo = null;
			if(userRoleId.longValue()!=GameConstants.DEFAULT_ROLE_ID.longValue()){
				serverInfo = KuafuRoleServerManager.getInstance().getBindServer(userRoleId);
			}
			tunnel = KuafuConfigUtil.getConnection(serverInfo);
			if(tunnel!=null && tunnel.isConnected()){
				tunnel.receive(new Object[]{ cmd,userRoleId, MsgUtil.getMsgData(msg)});
			}else{
				KuafuConfigUtil.returnBrokenConnection(tunnel);
				returned = true;
				GameLog.error("realUserRoleId ={} send2KuafuServer fail serverId={} cmd={}",userRoleId,serverInfo==null?"-":serverInfo.getServerId(),cmd);
			}
		}catch (Exception e) {
			KuafuConfigUtil.returnBrokenConnection(tunnel);
			returned = true;
			GameLog.error("", e);
		}finally{
			if(!returned){
				KuafuConfigUtil.returnConnection(tunnel);
			}
		}
	}
	/**
	 * 分发消息到客户端
	 * @param msg
	 */
	public static void swap2client(Object[] msg){
		ioMsgTunnel.receive(msg);
	}

}
