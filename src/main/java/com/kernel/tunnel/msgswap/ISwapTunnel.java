package com.kernel.tunnel.msgswap;

/**
 * 协议线程指派工具类
 */
public interface ISwapTunnel {

	/**
	 *	收到的协议
	 */
	void receive(Object msg);
}
