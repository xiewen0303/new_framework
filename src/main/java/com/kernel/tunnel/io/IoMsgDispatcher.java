package com.kernel.tunnel.io;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.game.easyexecutor.front.IEasyFrontend;
import com.junyou.io.Message.IoMessage;
import com.kernel.pool.executor.EasyExecutorRunnablePool;
import com.kernel.pool.executor.IBusinessExecutor;
import com.kernel.pool.executor.IRunnablePool;
import com.kernel.pool.executor.Message;
import com.kernel.pool.executor.MsgStatistics;
import com.kernel.tunnel.BusinessExexutorFactory;

/**
 *	客户端接入消息分发器
 */
@Component
public class IoMsgDispatcher {

	private ThreadLocal<IRunnablePool> runnableLocal = new ThreadLocal<>();
	
	private IoRouteHelper routeHelper = new IoRouteHelper();
	
	private IBusinessExecutor businessExexutor;
	
	private IEasyFrontend easyFrontend;
	
	@Autowired
	private MsgStatistics msgStatistics;
	
	public IoMsgDispatcher(){
		this.businessExexutor = BusinessExexutorFactory.createIoBusinessExecutor();
		this.easyFrontend = BusinessExexutorFactory.createIoEasyFrontend();
	}

	public void in(Object message) {
		
		Object[] sourceMsg = (Object[])message;
		Message msg = new IoMessage(sourceMsg);
		
		Runnable runnable = getRunnablePool().getRunnable(msg);
		businessExexutor.execute(runnable, routeHelper.getRouteInfo(msg,(Integer) sourceMsg[3]));
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
