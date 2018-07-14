package com.kernel.tunnel.public_;

import com.game.easyexecutor.cmd.CmdGroupInfo;
import com.kernel.pool.executor.Message;
import com.kernel.pool.executor.RouteInfo;

public class PublicRouteHelper {

	public RouteInfo getRouteInfo(Message message,int destType){
		
		RouteInfo routeInfo = null;
		switch (destType) {
		case CmdGroupInfo.target_type_inout: // login
//			Object data = message.getData();
			
			//回话Id
			String info = message.getSessionid();
//			//账号ID
//			if(data instanceof Double){
//				Long roleId = LongUtils.obj2long(data);
//				info = roleId.toString();
//			}else{
//				info = (String) message.<Object[]>getData()[0];
//			}
			
			routeInfo = new RouteInfo(CmdGroupInfo.LOGIN_GROUP);
			routeInfo.setInfo(info);
			break;
		case CmdGroupInfo.target_type_public: // public
			Short cmd = message.getCommand();
			if(CmdGroupInfo.isModule(cmd, CmdGroupInfo.NODE_CONTROL_GROUP)){
				routeInfo = new RouteInfo(CmdGroupInfo.NODE_CONTROL_GROUP);
				routeInfo.setInfo(message.getRoleId().toString());
			}else if(CmdGroupInfo.isModule(cmd, CmdGroupInfo.GUILD_GROUP)){
				routeInfo = new RouteInfo(CmdGroupInfo.GUILD_GROUP);
				routeInfo.setInfo(message.getRoleId().toString());
			}else{
				routeInfo = new RouteInfo(CmdGroupInfo.PUBLIC_GROUP);
				routeInfo.setInfo(CmdGroupInfo.getCmdModule(cmd));
			}
			break;
		}
		
		return routeInfo;
	}
	
}
