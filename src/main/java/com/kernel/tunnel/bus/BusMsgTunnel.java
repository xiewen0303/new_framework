package com.kernel.tunnel.bus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.kernel.tunnel.msgswap.ISwapTunnel;

@Service
public class BusMsgTunnel implements ISwapTunnel {

	private BusMsgDispatcher busMsgDispatcher;
	
	@Autowired
	public void setBusMsgDispatcher(BusMsgDispatcher busMsgDispatcher) {
		this.busMsgDispatcher = busMsgDispatcher;
	}

	@Override
	public void receive(Object msg) {
		busMsgDispatcher.in(msg);
	}

}
