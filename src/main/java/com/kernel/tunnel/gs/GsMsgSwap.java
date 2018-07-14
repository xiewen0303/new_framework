package com.kernel.tunnel.gs;

import org.springframework.stereotype.Component;

import com.kernel.tunnel.msgswap.NodeSwap;

/**
 * 管道中转调换器
 */
@Component
public class GsMsgSwap {
	
	/**
	 * 发送给单个客户端
	 * @param userid
	 * @param command
	 * @param result
	 */
	public void swap(Object[] msg){
		NodeSwap.swap(msg);
	}

}
