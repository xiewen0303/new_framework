package com.kernel.tunnel.public_;

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
 * public_model 消息分发器
 */
@Component
public class PublicMsgDispatcher {

	private ThreadLocal<IRunnablePool> runnableLocal = new ThreadLocal<IRunnablePool>();
	
	private PublicRouteHelper routeHelper = new PublicRouteHelper();
	
	private IBusinessExecutor businessExexutor;
	
	private IEasyFrontend easyFrontend;
	
	@Autowired
	private MsgStatistics msgStatistics;

	public PublicMsgDispatcher() {
		this.businessExexutor = BusinessExexutorFactory.createPublicBusinessExecutor();
		this.easyFrontend = BusinessExexutorFactory.createPublicEasyFrontend();
	}
	

	public void in(Object message) {
		Object[] sourceMsg = (Object[])message;
		Message msg = new Message(sourceMsg);
		
		
		Runnable runnable = getRunnablePool().getRunnable(msg);
		RouteInfo routeInfo = routeHelper.getRouteInfo(msg,(Integer) sourceMsg[2]);
		businessExexutor.execute(runnable, routeInfo);

		MsgPrintUtil.printMsg(msg,MsgPrintUtil.PUBLIC_PRINT,MsgPrintUtil.PUBLIC_PREFIX,routeInfo.toString());
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
