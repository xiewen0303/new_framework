package com.kernel.tunnel;

import java.util.HashMap;
import java.util.Map;

import com.game.easyexecutor.config.EasyexecutorConfig;
import com.game.easyexecutor.config.EasyexecutorConfigManager;
import com.game.easyexecutor.config.IEasyexecutorMeta;
import com.game.easyexecutor.front.DefaultFrontend;
import com.game.easyexecutor.front.IEasyFrontend;
import com.game.easyexecutor.manager.DefaultManager;
import com.game.easyexecutor.manager.IEasyManager;
import com.game.easyexecutor.manager.IEasyWorkerContainer;
import com.game.easyexecutor.manager.SpringWorkerContainer;
import com.junyou.bus.share.interceptor.RoleStateInterceptor;
import com.junyou.bus.share.ruleinfo.BusRuleInfoCheck;
import com.junyou.public_.share.ruleinfo.PublicRuleInfoCheck;
import com.junyou.stage.easyaction.interceptor.StageInterceptor;
import com.junyou.stage.ruleinfo.StageRuleInfoCheck;
import com.kernel.pool.executor.BalanceBusinessExecutor;
import com.kernel.pool.executor.DefaultBusinessExecutor;
import com.kernel.pool.executor.IBusinessExecutor;
import com.kernel.pool.executor.IRuleInfoCheck;
import com.kernel.spring.SpringApplicationContext;

public class BusinessExexutorFactory {
	/**
	 * TODO可以走配置开关  是否开启统计
	 */
	public static boolean isOpenStatistics = false;
	
	/**
	 * bus 业务消息线程调用器配置
	 * @return
	 */
	public static IBusinessExecutor createBusBusinessExecutor(){
		int size = 11;
		int gap = 1;
		int cleanGap = 11;
		String name = "bus_balance_executor";
		
		Map<String, Integer> groupConfigMap = new HashMap<>();
		groupConfigMap.put("bus_init", 6);
		groupConfigMap.put("bus_cache", 6);
		groupConfigMap.put("stage_control", 6);
		
		IRuleInfoCheck ruleInfoCheck = SpringApplicationContext.getApplicationContext().getBean(BusRuleInfoCheck.class);
		
		return new BalanceBusinessExecutor(size, gap, cleanGap, name, groupConfigMap, ruleInfoCheck);
	}
	
	/**
	 * 创建bus协议跳转
	 * @return
	 */
	public static IEasyFrontend createBusEasyFrontend() {
		IEasyWorkerContainer workerContainer = new SpringWorkerContainer(SpringApplicationContext.getApplicationContext());
		
		EasyexecutorConfig easyexecutorConfig = new EasyexecutorConfig();
		easyexecutorConfig.addScanPackage("com.junyou.bus");
		RoleStateInterceptor roleStateInterceptor = SpringApplicationContext.getApplicationContext().getBean(RoleStateInterceptor.class);
		easyexecutorConfig.addGlobalInterceptor(roleStateInterceptor);
		IEasyexecutorMeta easyexecutorMeta = new EasyexecutorConfigManager(easyexecutorConfig);
		
		IEasyManager easyManager = new DefaultManager(easyexecutorMeta, workerContainer);
		
		return new DefaultFrontend(easyManager);
	}
	
	/**
	 *	Io 业务消息线程调用器配置
	 * @return
	 */
	public static IBusinessExecutor createIoBusinessExecutor() {
		Map<String, Integer> groupConfigMap = new HashMap<>();
		groupConfigMap.put("io_all", 2);
		groupConfigMap.put("io_bus", 1);
		groupConfigMap.put("io_stage", 8);
		
		return new DefaultBusinessExecutor(groupConfigMap);
	}

	/**
	 * 创建Io协议跳转
	 * @return
	 */
	public static IEasyFrontend createIoEasyFrontend() {
		IEasyWorkerContainer workerContainer = new SpringWorkerContainer(SpringApplicationContext.getApplicationContext());
		
		EasyexecutorConfig easyexecutorConfig = new EasyexecutorConfig();
		easyexecutorConfig.addScanPackage("com.junyou.io");
//		easyexecutorConfig.addGlobalInterceptor(new RoleStateInterceptor());
		IEasyexecutorMeta easyexecutorMeta = new EasyexecutorConfigManager(easyexecutorConfig);
		
		IEasyManager easyManager = new DefaultManager(easyexecutorMeta, workerContainer);
		return new DefaultFrontend(easyManager);
	}

	
	public static IBusinessExecutor createPublicBusinessExecutor() {
		int size = 11;
		int gap = 1;
		int cleanGap = 11;
		String name = "public_balance_executor";
		
		Map<String, Integer> groupConfigMap = new HashMap<>();
		groupConfigMap.put("login", 6);
		groupConfigMap.put("node-control", 6);
		groupConfigMap.put("guild", 6);
		groupConfigMap.put("public", 6);
		
		IRuleInfoCheck ruleInfoCheck = SpringApplicationContext.getApplicationContext().getBean(PublicRuleInfoCheck.class);
		return new BalanceBusinessExecutor(size, gap, cleanGap, name, groupConfigMap, ruleInfoCheck);
	}
	
	public static IEasyFrontend createPublicEasyFrontend() {
		IEasyWorkerContainer workerContainer = new SpringWorkerContainer(SpringApplicationContext.getApplicationContext());
		
		EasyexecutorConfig easyexecutorConfig = new EasyexecutorConfig();
		easyexecutorConfig.addScanPackage("com.junyou.login");
		easyexecutorConfig.addScanPackage("com.junyou.public_");
		IEasyexecutorMeta easyexecutorMeta = new EasyexecutorConfigManager(easyexecutorConfig);
		
		IEasyManager easyManager = new DefaultManager(easyexecutorMeta, workerContainer);
		return new DefaultFrontend(easyManager);
	}

	public static IBusinessExecutor createStageBusinessExecutor() {
		int size = 11;
		int gap = 1;
		int cleanGap = 11;
		String name = "stage_balance_executor";
		Map<String, Integer> groupConfigMap = new HashMap<>();
		groupConfigMap.put("stage",17);
		IRuleInfoCheck ruleInfoCheck = SpringApplicationContext.getApplicationContext().getBean(StageRuleInfoCheck.class);
		return new BalanceBusinessExecutor(size, gap, cleanGap, name, groupConfigMap, ruleInfoCheck);
	}
	
	public static IEasyFrontend createStageEasyFrontend() {
		IEasyWorkerContainer workerContainer = new SpringWorkerContainer(SpringApplicationContext.getApplicationContext());
		
		EasyexecutorConfig easyexecutorConfig = new EasyexecutorConfig();
		easyexecutorConfig.addScanPackage("com.junyou.stage");
		easyexecutorConfig.addGlobalInterceptor(SpringApplicationContext.getApplicationContext().getBean(StageInterceptor.class));
		IEasyexecutorMeta easyexecutorMeta = new EasyexecutorConfigManager(easyexecutorConfig);
		
		IEasyManager easyManager = new DefaultManager(easyexecutorMeta, workerContainer);
		return new DefaultFrontend(easyManager);
	}
}
