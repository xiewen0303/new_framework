package com.junyou.public_.jindu.easyaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.game.easyexecutor.annotation.EasyMapping;
import com.game.easyexecutor.annotation.EasyWorker;
import com.game.easyexecutor.enumeration.EasyGroup;
import com.junyou.cmd.InnerCmdType;
import com.junyou.module.GameModType;
import com.junyou.public_.jindu.service.GameJinduService;
import com.kernel.pool.executor.Message;

@Controller
@EasyWorker(moduleName = GameModType.JINDU_MODULE,groupName = EasyGroup.PUBLIC)
public class GameJinduAction {
	@Autowired
	private GameJinduService gameJinduService;
	
	/**
	 * 进度变更
	 * @param inMsg
	 */
	@EasyMapping(mapping = InnerCmdType.GAME_JINDU)
	public void jinduChange(Message inMsg){
		Integer type = inMsg.getData();
		gameJinduService.changeJindu(type);
	}
}
