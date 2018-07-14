package com.game.start;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

import com.junyou.analysis.ServerInfoConfig;
import com.junyou.analysis.ServerInfoConfigManager;
import com.junyou.bus.serverinfo.entity.ServerInfo;
import com.junyou.bus.serverinfo.export.ServerInfoServiceManager;
import com.junyou.configure.ConfigDownloadManager;
import com.junyou.context.GameServerContext;
import com.junyou.http.GameHttpService;
import com.junyou.io.NetListener;
import com.junyou.io.global.GsCountChecker;
import com.junyou.io.modelfilter.MsgFilterManage;
import com.junyou.io.websocket.WebSocketServer;
import com.junyou.kuafu.manager.KuafuManager;
import com.junyou.kuafu.manager.KuafuServerManager;
import com.junyou.kuafuio.handle.KuafuNetListener;
import com.junyou.kuafumatch.manager.KuafuCompetitionMatchServerManager;
import com.junyou.kuafumatch.manager.KuafuMatchServerManager;
import com.junyou.log.GameLog;
import com.junyou.share.StringAppContextShare;
import com.junyou.utils.DownloadServerConfig;
import com.junyou.utils.GameStartConfigUtil;
import com.junyou.utils.KuafuConfigPropUtil;
import com.junyou.utils.exception.GameCustomException;
import com.kernel.cache.redis.Redis;
import com.kernel.check.db.CheckEntityTableTool;
import com.kernel.check.db.JdbcCheckBean;
import com.kernel.gen.id.IdFactory;
import com.kernel.memory.MemoryManager;
import com.kernel.spring.SpringApplicationContext;

public class GameBootService extends AbstractGameBootService {

	public GameBootService(String[] args) {
		super(args);
		DownloadServerConfig.replaceFiles(args);
	}

	@Override
	public String getServiceName() {
		return "logic-gameBoot-server";
	}

	@Override
	protected void onStart() throws GameCustomException {
		try{
			//加载配置
			ServerInfoConfigManager serverInfoConfigManager = ServerInfoConfigManager.getInstance();
			serverInfoConfigManager.load("game-base-config.xml");
			ServerInfoConfig appConfig = serverInfoConfigManager.getServerInfoConfig();
			
			//设置游戏配置上下文
			GameServerContext.setGameAppConfig(serverInfoConfigManager.getGameAppConfig());
			GameServerContext.setServerInfoConfig(serverInfoConfigManager.getServerInfoConfig());
			GameServerContext.setKuafuAppConfig(serverInfoConfigManager.getKuafuAppConfig());
			
			//游戏策划配置下载到本地
			ConfigDownloadManager.getInstance().downloadConfigs(appConfig);
			if(!ConfigDownloadManager.getInstance().isBoot()){
				GameLog.startLogError("====================================================");
				GameLog.startLogError("=         server boot failed.");
				GameLog.startLogError("====================================================");
				return;
			}

			//spring业务组件初始化
			StringAppContextShare.init();

			//通信组件初始化
			if(GameStartConfigUtil.isClientIoNode()){
				if(KuafuConfigPropUtil.isKuafuServer()){
					KuafuNetListener.start();
				}else{
					WebSocketServer.start();
//					NetListener.start();
				}
			}
			
			//验证数据库表和代码是否同步 配置里有可控制是否开启,验证跨服不开启表结构验证
			if(!GameServerContext.getKuafuAppConfig().isKfServer() && GameServerContext.getGameAppConfig().isCheckBootDb()){
				CheckEntityTableTool checkEntityTableTool = new CheckEntityTableTool();
				JdbcCheckBean jdbcBean = (JdbcCheckBean)SpringApplicationContext.getApplicationContext().getBean("jdbcCheckBean");
				checkEntityTableTool.check(jdbcBean);
			}
			
			
			if(GameStartConfigUtil.getGlobalRedisOn()){
				//连接redis
				String globalRedisIp = GameStartConfigUtil.getGlobalRedisIp();
				String globalRedisPort = GameStartConfigUtil.getGlobalRedisPort();
				int globalRedisDb = GameStartConfigUtil.getGlobalRedisDb();
				String globalRedisPwd = GameStartConfigUtil.getGlobalRedisPassword();
				
				Redis redis = new Redis(globalRedisIp, Integer.parseInt(globalRedisPort),globalRedisDb, globalRedisPwd);
				if(!redis.ping()){
					throw new GameCustomException("redis 启动失败");
				}
				GameServerContext.setRedis(redis);
			}
//			else{
//				if(!PlatformConstants.isQQ()){  TODO
//					throw new JunYouCustomException("redis 配置为不启动");
//				}
//			}

			if(KuafuConfigPropUtil.isKuafuServer()){
				if(!KuafuManager.isMatchServer()){
					KuafuServerManager.register();
					KuafuServerManager.handleKuafuBoss();
					KuafuServerManager.handleKuafuQunXianYan();
				}else{
					if(GameStartConfigUtil.getIsGlobalCompetitionMatchServer()){
						KuafuCompetitionMatchServerManager.register();
					}else{
						KuafuMatchServerManager.register();
					}
				}
			}
			
			//服务器信息初始化
			ServerInfo serverInfo = ServerInfoServiceManager.getInstance().init();

			//生成唯一id的服务
			IdFactory.getInstance().init(serverInfo);
			
			//http通信组件初始化
			GameHttpService.getInstence().start();
			
			//解析游戏最大人数和IP白名单属性配置
			GsCountChecker.getInstance().refresh();
			
			//启动内存监控
			MemoryManager.getInstance().initialize();
			
//			//热发布配置下载并解析
//			ReFaBuGxConfigExportService.getInstance().init();
			
			//游戏业务启动处理
			GameBusStartStopHandle.startHandle();
			
			//打开主端口消息入口
			MsgFilterManage.changeOpenMsg();
			
			//记录进程ID
			rememberPid();
			
			//输出成功信息
			GameLog.startLogDebug("====================================================");
			GameLog.startLogDebug("=         current server id:" + GameStartConfigUtil.getServerId());
			GameLog.startLogDebug("=         server started successfully.");
			GameLog.startLogDebug("====================================================");

		}catch (Exception e) {
			
			GameLog.startLogError("",e);
			GameLog.startLogError("====================================================");
			GameLog.startLogError("=         server started failed.");
			GameLog.startLogError("====================================================");
			e.printStackTrace();
			
			System.exit(0);
		}
	}
	
	private static void rememberPid(){
		
		RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();  
        String name = runtime.getName(); // format: "pid@hostname"  
        try {  
            
        	String pid = name.substring(0, name.indexOf('@'));
        	File pidFile = new File(ClassLoader.getSystemResource("").getPath(),"pid");
        	if(!pidFile.exists()){
        		
        		boolean created = pidFile.createNewFile();
        		if(!created){
        			GameLog.startLogError("can't create pid file!");
        		}
        	}

        	Writer pidWriter = new FileWriter(pidFile);
        	pidWriter.write(pid);
        	pidWriter.flush();
        	pidWriter.close();
        	
        } catch (Exception e) {  
        	GameLog.startLogError("",e);
        }
	}

	@Override
	protected void onRun() throws GameCustomException {
		// 不需要实现业务
	}

	@Override
	protected void onStop() throws GameCustomException {
		GameLog.stopLogDebug("onStop stopping {}", this.getServiceName());

		try{
			/**
			 * 通信模块关闭
			 */
			//关闭主端口消息入口
			MsgFilterManage.changeCloseMsg();
			
			// http组件关闭
			GameHttpService.getInstence().stop();
			// 通信组件关闭
			NetListener.stop();
			
			GameLog.stopLogDebug("主端口和http端口关闭完成!");
			//从redis里注销掉
			if(KuafuConfigPropUtil.isKuafuServer()){
				KuafuServerManager.quit();
			}
			
			if(GameServerContext.getRedis()!=null){
				try{
					GameServerContext.getRedis().destory();
					GameLog.stopLogDebug("断开与redis的连接!");
				}catch (Exception e) {
					GameLog.error("断开redis的连接失败",e);
				}
			}
			
			/**
			 * 数据模块关闭
			 */
			
			// 必要业务数据保存
			// public 节点
			for(Long roleid : GameServerContext.getPublicRoleStateService().getAllOnlineRoleids() ){
				try{
					GameServerContext.getNodeControlService().nodeExitOnServerClose(roleid);
				}catch (Exception e) {
					GameLog.stopLogError("nodeExitOnServerClose ", e);
				}
			}
			
			GameLog.stopLogDebug("完成 stop前 public 业务!");
			
			// gs节点
			for(Long roleid : GameServerContext.getRoleStateService().getAllOnlineRoleids()){
				try{
					GameServerContext.getIoService().roleOutOnServerClose(roleid);
				}catch (Exception e) {
					GameLog.stopLogError("roleOutOnServerClose", e);
				}
			}
			
			GameLog.stopLogDebug("完成 stop前 gs节点 业务!");
			
			/**停服业务数据处理*/
			GameBusStartStopHandle.stopHandle();
			
		}catch (Exception e) {
			GameLog.stopLogError("onStop",e);
		}finally {
			// 数据回写中心关闭
			GameServerContext.getAsyncWriteManager().close();
			GameLog.stopLogError("完成 数据回写中心关闭 业务!");
		}
	}
}
