package com.junyou.bus.share.schedule;

import com.kernel.token.ITokenRunnable;
import com.kernel.tunnel.bus.BusMsgSender;

/**
 * 提供给定时器使用
 */
public class TaskBusRunable implements ITokenRunnable {

	private Object[] token;

	private Long roleId;
	
	private Short command;
	
	private Object data;
	
	public TaskBusRunable(Long roleId,Short commond,Object data) {
		this.roleId = roleId;
		this.command = commond;
		this.data = data;
	}
	
	@Override
	public void run() {
		BusMsgSender.send2BusInnerToken(roleId, command, data, token);
	}

	@Override
	public void setToken(Object[] token) {
		this.token = token;
	}
}
