package com.kernel.tunnel.io;

import com.game.easyexecutor.cmd.CmdGroupInfo;
import com.kernel.pool.executor.Message;
import com.kernel.pool.executor.RouteInfo;

public class IoRouteHelper {

	private static final String IO_ALL_GROUP = "io_all";
	private static final String IO_BUS_GROUP = "io_bus";
	private static final String IO_STAGE_GROUP = "io_stage";
	
	public RouteInfo getRouteInfo(Message message,int fromType){
		
		RouteInfo routeInfo = null;
		switch (fromType) {
		case CmdGroupInfo.source_type_public:
		case CmdGroupInfo.source_type_bus: // bus
			int broadcast_type = (Integer) message.getMsgSource()[4];
			routeInfo = new RouteInfo(IO_BUS_GROUP);
			if(broadcast_type == CmdGroupInfo.broadcast_type_1){
				routeInfo.setInfo(message.getRoleIdInfo());
			}else if(broadcast_type == CmdGroupInfo.broadcast_type_2){
				routeInfo.setInfo(((Object[])message.getMsgSource()[7])[0].toString());
			}else{
				routeInfo = new RouteInfo(IO_ALL_GROUP);
				routeInfo.setInfo(message.getRoleId().toString());
			}
			break;
		case CmdGroupInfo.source_type_gs:
		case CmdGroupInfo.source_type_stage: // stage
			routeInfo = new RouteInfo(IO_STAGE_GROUP);
			routeInfo.setInfo(message.getStageId());
			break;
		case CmdGroupInfo.source_type_client: // 直接转发
			routeInfo = new RouteInfo(IO_ALL_GROUP);
			routeInfo.setInfo(message.getRoleId().toString());
			break;
		}
		
		return routeInfo;
	}
	
}
