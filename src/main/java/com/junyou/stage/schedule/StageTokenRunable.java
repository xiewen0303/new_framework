package com.junyou.stage.schedule;

import com.kernel.token.ITokenRunnable;
import com.kernel.tunnel.stage.StageMsgSender;

/**
 * @description 
 * @author ShiJie Chi
 * @date 2012-3-27 下午6:07:55 
 */
public class StageTokenRunable implements ITokenRunnable {

	protected Object[] token;
	
	private Short command;
	
	private Object data;
	
	private Long roleId;
	private String stageId;
	
	public StageTokenRunable(Long roleId,String stageId,Short commond,Object data) {
		this.command = commond;
		this.data = data;
		this.roleId = roleId;
		this.stageId = stageId;
	}
	
	@Override
	public void run() {
		StageMsgSender.send2StageInnerToken(roleId,stageId,command, data, token);
	}

	@Override
	public void setToken(Object[] token) {
		this.token = token;
	}

	
	
}
