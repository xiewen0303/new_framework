package com.game.easyexecutor.front;

import com.game.easyexecutor.manager.IEasyManager;
import com.game.easyexecutor.resolver.IEasyResolver;

public class DefaultFrontend implements IEasyFrontend {

	private IEasyManager manager;
	
	public DefaultFrontend(IEasyManager easyManager) {
		if(null == easyManager) throw new NullPointerException("easyManager is null.");
		this.manager = easyManager;
	}

	@Override
	public void execute(Short command, Object message) {

			IEasyResolver resolver = manager.getResolver(command); 
		if(null!=resolver){
			resolver.execute(message);
		}else{
			throw new NullPointerException("worker [" + command + "] is not existed.");
		}
		
	}
	
}
