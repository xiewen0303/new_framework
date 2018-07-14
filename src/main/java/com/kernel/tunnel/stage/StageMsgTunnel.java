package com.kernel.tunnel.stage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kernel.tunnel.msgswap.ISwapTunnel;

@Service
public class StageMsgTunnel implements ISwapTunnel {

	private StageMsgDispatcher stageMsgDispatcher;
	
	@Autowired
	public void setStageMsgDispatcher(StageMsgDispatcher stageMsgDispatcher) {
		this.stageMsgDispatcher = stageMsgDispatcher;
	}

	@Override
	public void receive(Object msg) {
		stageMsgDispatcher.in(msg);
	}

}
