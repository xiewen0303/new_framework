package com.kernel.tunnel.io;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kernel.tunnel.msgswap.ISwapTunnel;

@Service
public class IoMsgTunnel implements ISwapTunnel {
	
	
	private IoMsgDispatcher ioMsgDispatcher;
	
	@Autowired
	public void setIoMsgDispatcher(IoMsgDispatcher ioMsgDispatcher) {
		this.ioMsgDispatcher = ioMsgDispatcher;
	}

	@Override
	public void receive(Object msg) {
		ioMsgDispatcher.in(msg);
	}

}
