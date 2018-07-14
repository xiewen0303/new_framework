package com.game.easyexecutor.cmd;

import java.util.HashMap;
import java.util.Map;

import com.game.easyexecutor.enumeration.EasyKuafuType;
import com.junyou.cmd.InnerCmdType;
import com.junyou.context.GameServerContext;
import com.junyou.module.GameModType;

/**
 * 消息路由常量
 */
public class CmdGroupInfo {
	
	public static final String LOGIN_GROUP = "login";
	public static final String PUBLIC_GROUP = "public";
	public static final String NODE_CONTROL_GROUP = "node-control";
	public static final String GUILD_GROUP = "guild";
	
	/**
	 * 业务
	 */
	public static final String BUS_CACHE_GROUP = "bus_cache";
	public static final String BUS_INIT_GROUP = "bus_init";
	public static final String STAGECONTROL_GROUP = "stage_control";
	public static final String STAGE_GROUP = "stage";
	public static final String KUAFU_GROUP = "kuafu";

	
	
	/**
	 *
 	 * @param Tunnel消息组件类型  dest_type 目标类型(swap Tunnel消息组件类型)
	 * @return
	 */
	public static final int target_type_client = 0; 
	
	/**
	 * 基础业务逻辑模块 	bus_cache
	 */
	public static final int target_type_bus = 1;
	
	/**
	 * 主要控制场景切换的线程	STAGECONTROL_GROUP	
	 */
	public static final int target_type_stage_control = 2; 
	
	/**
	 * 场景逻辑线程			STAGE_GROUP  
	 */
	public static final int target_type_stage = 3;

	/**
	 * 登录登出			LOGIN_GROUP
	 */
	public static final int target_type_inout = 4;
	
	/**
	 * 登录登出内部处理		BUS_INIT_GROUP
	 */
	public static final int target_type_bus_init = 5; 
	
	
	public static final int target_type_public = 6; 
	public static final int target_type_inner_system = 7; 
	/**
	 * 跨服发给逻辑服的协议
	 */
	public static final int target_type_kuafu_server = 8; 
	
	/**
	 * 需要通过协议来确定目标的,比如街搜到的客服端协议
	 */
	public static final int target_type_gs = -1; 

	// broadcast_type
	/**
	 * 客服端发送给服务端的消息  one
	 */
	public static final int broadcast_type_1 = 1; 
	/**
	 * many
	 */
	public static final int broadcast_type_2 = 2; 
	/**
	 * all
	 */
	public static final int broadcast_type_3 = 3;

	/**
	 * 消息来源  from_type
 	 */
	/**
	 * 接受到客服端发送的信息
	 */
//	public static final int from_type_client = 1;
//	public static final int from_type_bus = 2;
//	public static final int from_type_stage = 3;
//	public static final int from_type_stage_control = 4;

	/**
	 * node_from_type  消息来源
 	 */
	/**
	 * 客服端发出
	 */
	public static final int source_type_client = 1;
	/**
	 * 公共模块发出
	 */
	public static final int source_type_public = 2;
	/**
	 * 中间模块发出
	 */
	public static final int source_type_gs = 3;
	/**
	 * 跨服源服务器发出
	 */
	public static final int source_type_kuafu_source = 4;
	/**
	 * 跨服目标服务器发出
	 */
	public static final int source_type_kuafu_server = 5;
	/**
	 * bus模块发出
	 */
	public static final int source_type_bus = 6;
	/**
	 * 场景模块发出
	 */
	public static final int source_type_stage = 7;
	
	/**
	 * key:模块组,value:广播类型
	 */
	private static Map<String, Integer> groupDestMap = new HashMap<String, Integer>();
	static{
		groupDestMap.put(BUS_CACHE_GROUP, target_type_bus);
		groupDestMap.put(STAGECONTROL_GROUP, target_type_stage_control);
		groupDestMap.put(STAGE_GROUP, target_type_stage);
		groupDestMap.put(LOGIN_GROUP, target_type_inout);
		groupDestMap.put(BUS_INIT_GROUP, target_type_bus_init);
		groupDestMap.put(PUBLIC_GROUP, target_type_public);
		groupDestMap.put(KUAFU_GROUP, target_type_kuafu_server);
	}
	
	
	private static Map<String, String> moduleGroupMap = new HashMap<>();
	/**
	 * key:子模块,value:指令
	 */
	private static Map<Short, String> cmdGroupMap = new HashMap<>();
	private static Map<Short, String> cmdModuleMap = new HashMap<>();
	private static Map<Short, Integer> cmdDestMap = new HashMap<>();
	
	/**
	 * 注册指令
	 * @param cmd
	 * @param moduleName
	 * @param groupName
	 * @param kuafuType
	 * @throws Exception 
	 */
	public static void registerCmds(short cmd,String moduleName,String groupName,EasyKuafuType kuafuType) throws Exception{
		
		if(cmdGroupMap.containsKey(cmd)){
			throw new Exception((InnerCmdType.leaveStage_cmd == cmd) + "重复注册指令 ["+cmd+","+moduleName+","+groupName+"]["+cmdGroupMap.get(InnerCmdType.leaveStage_cmd)+","+cmdModuleMap.get(InnerCmdType.leaveStage_cmd)+"]");
		}
		
		//GM指令在gmOpen是关闭时，不注册指定
		if(GameModType.GM_MODULE.equals(moduleName) && !GameServerContext.getServerInfoConfig().isGmOpen()){
			//未开启GM功能
			return;
		}
		
		cmdGroupMap.put(cmd, groupName);
		moduleGroupMap.put(groupName, moduleName);
		cmdModuleMap.put(cmd, moduleName);
		
		cmdDestMap.put(cmd, groupDestMap.get(groupName));
		
		//跨服指令注册
		EasyKuafuCmdInfo.registerKuafuCmds(cmd, kuafuType);
	}
	
	/**
	 * 通过协议号获取目标
	 * @param cmd
	 * @return
	 */
	public static Integer getCmdTargetType(Short cmd){
		return cmdDestMap.get(cmd);
	}
	
	/**
	 * 获取指令所属执行分组
	 * @param cmd
	 * @return
	 */
	public static String getCmdGroup(Short cmd){
		return cmdGroupMap.get(cmd);
	}
	
	/**
	 * 获取指令所属模块名
	 * @param cmd
	 * @return
	 */
	public static String getCmdModule(Short cmd){
		return cmdModuleMap.get(cmd);
	}
	
	
	public static boolean isChatModule(Short cmd){
		String module = cmdModuleMap.get(cmd);
		if(null == module) return false;
		return module.equals(GameModType.CHAT_MODULE);
	}

	public static boolean isTsServerModule(Short cmd){
		String module = cmdModuleMap.get(cmd);
		if(null == module) return false;
		return module.equals(GameModType.TSSERVER_MODULE);
	}
	
	public static boolean isPingModule(Short cmd){
		String module = cmdModuleMap.get(cmd);
		if(null == module) return false;
		return module.equals(GameModType.PING_MODULE);
	}
	
	/**
	 * 判定一个指令是否属于某个模块
	 * @param cmd
	 * @param moduleName
	 * @return
	 */
	public static boolean isModule(Short cmd,String moduleName){
		String module = cmdModuleMap.get(cmd);
		if(null == module) return false;
		return module.equals(moduleName);
	}

	/**
	 * 判定一个模块是否属于某个执行分组
	 * @param module
	 * @param groupName
	 * @return
	 */
	public static boolean isGroup(String module,String groupName){
		String group = moduleGroupMap.get(module);
		if(null == group) return false;
		return group.equals(groupName);
	}
}
