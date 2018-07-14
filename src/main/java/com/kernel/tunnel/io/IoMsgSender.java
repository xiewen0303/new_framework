package com.kernel.tunnel.io;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.game.easyexecutor.cmd.CmdGroupInfo;
import com.junyou.utils.MsgPrintUtil;
import com.kernel.tunnel.msgswap.NodeSwap;

@Component
public class IoMsgSender {
	
	private static IoMsgTunnel ioMsgTunnel;
	
	@Autowired
	public void setIoMsgTunnel(IoMsgTunnel ioMsgTunnel) {
		IoMsgSender.ioMsgTunnel = ioMsgTunnel;
	}
	
	public static void swap(Object[] msg){
		Short cmd = (Short) msg[0];
		if(CmdGroupInfo.isChatModule(cmd) || CmdGroupInfo.isPingModule(cmd) || CmdGroupInfo.isTsServerModule(cmd)){
			ioMsgTunnel.receive(msg);
		}else{
			NodeSwap.swap(msg);
		}
	}

	public static void send2IoInner(Long roleid, Short command,Object result) {
		Object[] msg = new Object[]{command,result,0,CmdGroupInfo.source_type_public,CmdGroupInfo.broadcast_type_1,null,null,roleid,null,1};
		MsgPrintUtil.printOutMsg(msg,MsgPrintUtil.IO_PRINT,MsgPrintUtil.IO_PREFIX);
		swap(msg);
	}
	
}
