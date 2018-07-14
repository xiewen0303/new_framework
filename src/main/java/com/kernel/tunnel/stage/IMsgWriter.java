package com.kernel.tunnel.stage;

/**
 * @description
 */
public interface IMsgWriter {

	/**
	 * 增加发往场景的消息
	 */
	public void writeMsg(Long roleId, Short command,Object result);
	
	/**
	 * 增加发往客户端的多发消息
	 */
	public void writeMsg(Long[] roleIds, Short command,Object result);
	
	/**
	 * 刷出所有消息
	 * @param
	 */
	public void flush();

	public void writePublicMsg(Long id, Short command,Object result);
	
}
