package com.junyou.kuafumatch.manager;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import com.game.easyexecutor.cmd.CmdGroupInfo;
import com.junyou.log.GameLog;
import com.kernel.tunnel.kuafu.KuafuNetTunnel;
import com.kernel.tunnel.msgswap.NodeSwap;

public class KuafuMatchSourceNetHandler extends SimpleChannelInboundHandler<Object[]> {

	private KuafuNetTunnel kuafuNetTunnel;
	
	public KuafuMatchSourceNetHandler(KuafuNetTunnel kuafuNetTunnel) {
		super();
		GameLog.info("KuafuMatchSourceNetHandler 已创建");
		this.kuafuNetTunnel = kuafuNetTunnel;
	}

	/** 断开连接 原先的channelClosed */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		GameLog.info("kuafu match {} channelClosed", ctx.channel());
		if(kuafuNetTunnel!=null && !kuafuNetTunnel.isConnected()){
			if(kuafuNetTunnel.getServerInfo().isCompetitionMatchServer()){
				KuafuCompetitionMatchServerManager.returnKuafuMatchBrokenConnection(kuafuNetTunnel);
			}else{
				KuafuMatchServerManager.returnKuafuMatchBrokenConnection(kuafuNetTunnel);
			}
		}
	}

	/** messageReceived */
	@Override
	public void channelRead0(ChannelHandlerContext ctx, Object[] msg) {
		// 收到跨服返回的MSG(roleId,cmd,data)
		
		try {
			Short cmd = (Short) msg[0];
			Long roleOutId = (Long) msg[1];
			Object data = msg[2];
			
			Integer destType = CmdGroupInfo.getCmdTargetType(cmd);
			if(destType==null){
				GameLog.error("destType is null cmd={}",cmd);
				return ;
			}

			Object[] innerMsg = new Object[] { cmd, data, destType,
					CmdGroupInfo.source_type_kuafu_server,
					CmdGroupInfo.broadcast_type_1, 0, null, roleOutId,// roleId
					null, 1, null, null };

			NodeSwap.swap(innerMsg);
		} catch (Exception e) {
			GameLog.error("channelRead0 failed: " + e.getMessage(), e);
		}
	}

	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) {
		GameLog.error(e.getMessage(), e);
	}
}