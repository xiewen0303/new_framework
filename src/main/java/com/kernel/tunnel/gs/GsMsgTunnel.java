package com.kernel.tunnel.gs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kernel.tunnel.msgswap.ISwapTunnel;

@Service
public class GsMsgTunnel implements ISwapTunnel {

	@Autowired
	private GsMsgDispatcher gsMsgDispatcher;

	@Override
	public void receive(Object msg) {
		gsMsgDispatcher.in(msg);
	}

}
