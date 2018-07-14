package com.junyou.stage.easyaction.interceptor;

import org.springframework.stereotype.Service;

import com.game.easyexecutor.Interceptor.IInterceptor;
import com.junyou.gs.share.thread.RoleidThreadShare;
import com.junyou.stage.share.StageidThreadShare;
import com.kernel.pool.executor.Message;

@Service
public class StageInterceptor implements IInterceptor {

	@Override
	public boolean before(Object message) {
		String stageid = ((Message)message).getStageId();
		StageidThreadShare.setStageId(stageid);
		RoleidThreadShare.setRoleId(((Message)message).getRoleId());
		return true;
	}

	@Override
	public boolean after(Object message) {
		return true;
	}

}
