package com.kernel.tunnel.public_;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kernel.tunnel.msgswap.ISwapTunnel;

@Service
public class PublicMsgTunnel implements ISwapTunnel {

	private PublicMsgDispatcher publicMsgDispatcher;
	
	@Autowired
	public void setPublicMsgDispatcher(PublicMsgDispatcher publicMsgDispatcher) {
		this.publicMsgDispatcher = publicMsgDispatcher;
	}

	@Override
	public void receive(Object msg) {
		publicMsgDispatcher.in(msg);
	}

}
