package com.junyou.io.websocket;

import com.junyou.analysis.ServerInfoConfigManager;
import com.junyou.context.GameServerContext;

public class StartTest {
	public static void main(String[] args) {
		
		//加载配置
		ServerInfoConfigManager serverInfoConfigManager = ServerInfoConfigManager.getInstance();
		serverInfoConfigManager.load("game-base-config.xml");
//		ServerInfoConfig appConfig = serverInfoConfigManager.getServerInfoConfig();
		
		//设置游戏配置上下文
		GameServerContext.setGameAppConfig(serverInfoConfigManager.getGameAppConfig());
		GameServerContext.setServerInfoConfig(serverInfoConfigManager.getServerInfoConfig());
		
		
		WebSocketServer.start();
	}
}
