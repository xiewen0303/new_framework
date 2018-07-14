package com.kernel.tunnel.bus;

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
 *	bus模块消息分发器
 */
@Component
public class BusMsgDispatcher {
	
	private ThreadLocal<IRunnablePool> runnableLocal = new ThreadLocal<IRunnablePool>();
	
	private BusRouteHelper routeHelper = new BusRouteHelper();
	
	private IBusinessExecutor businessExexutor;
	
	private IEasyFrontend easyFrontend;
	
	@Autowired
	private MsgStatistics msgStatistics;
	
	public BusMsgDispatcher() {
		this.businessExexutor = BusinessExexutorFactory.createBusBusinessExecutor();
		this.easyFrontend = BusinessExexutorFactory.createBusEasyFrontend();
	}

	public void in(Object message) {
		Object[] sourceMsg = (Object[])message;
		Message msg = new Message(sourceMsg);
		
		
		Runnable runnable = getRunnablePool().getRunnable(msg);
		RouteInfo routeInfo = routeHelper.getRouteInfo(msg,(Integer) sourceMsg[2]);
		businessExexutor.execute(runnable, routeInfo);

		MsgPrintUtil.printMsg(msg,MsgPrintUtil.BUS_PRINT,MsgPrintUtil.BUS_PREFIX ,routeInfo.toString());
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
