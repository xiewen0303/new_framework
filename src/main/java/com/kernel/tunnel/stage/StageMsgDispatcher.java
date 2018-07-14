package com.kernel.tunnel.stage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.game.easyexecutor.front.IEasyFrontend;
import com.junyou.utils.MsgPrintUtil;
import com.kernel.pool.executor.EasyExecutorRunnablePool;
import com.kernel.pool.executor.IBusinessExecutor;
import com.kernel.pool.executor.IRunnablePool;
import com.kernel.pool.executor.Message;
import com.kernel.pool.executor.MsgStatistics;
import com.kernel.pool.executor.RouteInfo;
import com.kernel.tunnel.BusinessExexutorFactory;

/**
 * 客户端接入消息分发器
 */
@Component
public class StageMsgDispatcher {

	private ThreadLocal<IRunnablePool> runnableLocal = new ThreadLocal<IRunnablePool>();
	
	private StageRouteHelper routeHelper = new StageRouteHelper();
	
	private IBusinessExecutor businessExexutor;
	
	private IEasyFrontend easyFrontend;

	@Autowired
	private MsgStatistics msgStatistics;
	
	public StageMsgDispatcher(){
		this.businessExexutor = BusinessExexutorFactory.createStageBusinessExecutor();
		this.easyFrontend = BusinessExexutorFactory.createStageEasyFrontend();
	}

	public void in(Object message) {
		
		Message msg = new Message((Object[])message);
		
		
		Runnable runnable = getRunnablePool().getRunnable(msg);
		RouteInfo routeInfo = routeHelper.getRouteInfo(msg);
		businessExexutor.execute(runnable, routeInfo);

		MsgPrintUtil.printMsg(msg,MsgPrintUtil.STAGE_PRINT,MsgPrintUtil.STAGE_PREFIX,routeInfo.toString());
	}

	private IRunnablePool getRunnablePool(){
		IRunnablePool runnablePool = runnableLocal.get();
		if(null==runnablePool){
			runnablePool = new EasyExecutorRunnablePool(easyFrontend,msgStatistics);
			runnableLocal.set(runnablePool);
		}
		return runnablePool;
	}

}
