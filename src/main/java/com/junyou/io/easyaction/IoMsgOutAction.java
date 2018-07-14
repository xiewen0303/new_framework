package com.junyou.io.easyaction;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;

import com.game.easyexecutor.annotation.EasyMapping;
import com.game.easyexecutor.annotation.EasyWorker;
import com.game.easyexecutor.cmd.CmdGroupInfo;
import com.google.protobuf.GeneratedMessageV3;
import com.junyou.cmd.ClientCmdType;
import com.junyou.cmd.InnerCmdType;
import com.junyou.io.GameSession;
import com.junyou.io.Message.IoMessage;
import com.junyou.io.global.GameSessionManager;
import com.junyou.kuafu.manager.KuafuSessionManager;
import com.junyou.log.GameLog;
import com.junyou.module.GameModType;
import com.junyou.utils.KuafuConfigPropUtil;
import com.kernel.tunnel.kuafu.KuafuMsgSender;
import com.kernel.utils.SerializableHelper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

@Controller
@EasyWorker(moduleName = GameModType.NOTIFY_CLIENT)
public class IoMsgOutAction {

	private static final ThreadLocal<Object[]> messageCahce = new ThreadLocal<Object[]>();
	
	private Object[] initKuafuMessage(Long roleId,short cmd,Object data){
		Object[] msg = messageCahce.get();
		if(msg == null){
			msg = new Object[3];
			messageCahce.set(msg);
		}
		msg[0] = cmd;
		msg[1] = roleId;
		msg[2] = data;
		return msg;
	}
	
	private Map<Short, String> stageIOCmds = new HashMap<Short, String>();
	{
		stageIOCmds.put(ClientCmdType.CHANGE_STAGE, null);
	}
	
	/**
	 * 处理消息分发逻辑
	 * @param message
	 */
	@EasyMapping(mapping=InnerCmdType.IO_MSG_DISPATHCE_CMD)
	public void ioMsgOut(IoMessage message){
		
		//如果是跨服服务器则向源服务器转发
		if( KuafuConfigPropUtil.isKuafuServer() ){
			ioMsgOut2(message);
			return;
		}
		
		
		Object[] tmps = (Object[]) message.getMsgSource();
		
		short cmd = (short)tmps[0];
		GeneratedMessageV3 data = message.getData();
		int from_type = (Integer) tmps[3];
		int broadcast_type = (Integer) tmps[4];
		
		GameSession session = null;
		switch (broadcast_type) {
		case CmdGroupInfo.broadcast_type_1: // one
			
			int outSign = (Integer) tmps[9];
			if(outSign > 0){
				
				Long roleid = (Long) tmps[7];
				session = GameSessionManager.getInstance().getSession4RoleId(roleid);
				
				// check is in stage
				if(null != session){

					if(from_type == CmdGroupInfo.source_type_stage){
						
						if(stageIOCmds.containsKey(cmd)){
							synchronized (session) {
								session.setInStage(true);
							}
						}
						
						if(!session.isInStage()){
							return;
						}
					}
				}
			}else{
				session = GameSessionManager.getInstance().getSession4Id(message.getSessionid());
			}
			
			if(null != session){
//				if(cmd == null){
//					ChuanQiLog.error(JSON.toJSONString(data));
//				}
				sesesionWriteFuture(cmd, data, session);
			}
			
			break;
		case CmdGroupInfo.broadcast_type_2: // many

			Object[] roleids = (Object[]) tmps[7];
			if(null != roleids){
//				byte[] bytes = AmfUtil.convertMsg2Bytes(cmd, data);
				for(Object roleid : roleids){
					session = GameSessionManager.getInstance().getSession4RoleId(Long.parseLong(roleid.toString()));
					if(null != session){
						// check is in stage
						if(from_type == CmdGroupInfo.source_type_stage){
							if(stageIOCmds.containsKey(cmd)){
								synchronized (session) {
									session.setInStage(true);
								}
							}
							
							if(!session.isInStage()){
								break;
							}
						}
						
						sesesionWriteFuture(cmd,data, session);
					}
				}
			}
			
			break;
		case CmdGroupInfo.broadcast_type_3: // all
			for(GameSession session3 : GameSessionManager.getInstance().getRoleListSession()){
				sesesionWriteFuture(cmd,data, session3);
			}
			break;
		}
	}
	
	
	private void sesesionWriteFuture(short cmd,GeneratedMessageV3 data,GameSession session){
		byte[] ackData = data.toByteArray();
		ByteBuf bufferNew = PooledByteBufAllocator.DEFAULT.heapBuffer(ackData.length+4);
		bufferNew.writeShort(cmd);
		bufferNew.writeShort(ackData.length);
		bufferNew.writeBytes(ackData);
		
		WebSocketFrame sendDataFrame = new BinaryWebSocketFrame(bufferNew);
		session.sendMsg(sendDataFrame);
	}
	
//	private void sesesionWriteFuture(byte[] data,GameSession session){
//		session.sendMsg(data);
//	}
	
	/**
	 * 跨服服务器向源服务器转发
	 * @param message
	 */
	private void ioMsgOut2(IoMessage message){
		
		Object[] tmps = (Object[]) message.getMsgSource();
		
		Short cmd = (Short)tmps[0];
		
		Object data = tmps[1];
		int broadcast_type = (Integer) tmps[4];
		
//		if(KuafuCmdUtil.isKuafuNoSendCmd((Short)cmd)){
//			return;
//		}
		
		switch (broadcast_type) {
		case 1: // one
			
			int outSign = (Integer) tmps[9];
			Long roleId = null;
			if(outSign > 0){
				roleId = (Long) tmps[7];
				String serverId = KuafuSessionManager.getServerId(roleId);
				if(serverId!=null){
					KuafuSessionManager.writeMsg(serverId, SerializableHelper.serialize(initKuafuMessage(roleId, cmd,data)));
				}else{
					GameLog.error("send 2 session is null, cmd is {"+cmd+"} roleId:{"+roleId+"}");
				}
			}
			break;
		case 2: // many

			Object[] roleids = (Object[]) tmps[7];
			if(null != roleids){
				for(Object roleid : roleids){
					Long innerRoleId = Long.parseLong(roleid.toString());
					String serverId = KuafuSessionManager.getServerId(innerRoleId);
					if(serverId!=null){
						KuafuSessionManager.writeMsg(serverId, SerializableHelper.serialize(initKuafuMessage(innerRoleId, cmd,data)));
					}else{
						GameLog.error("send 2 many session is null, cmd is {"+cmd+"} roleId:{"+innerRoleId+"}");
					}
				}
			}
			break;
		case 3: // all
			KuafuMsgSender.send2All(cmd, data);
			break;
		}
	}
	
}
