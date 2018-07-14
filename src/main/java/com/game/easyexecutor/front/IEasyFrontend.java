package com.game.easyexecutor.front;


/**
 * 前端控制,处理协议跳转到Action中执行
 */
public interface IEasyFrontend {

	public void execute(Short command,Object message);
	
}
