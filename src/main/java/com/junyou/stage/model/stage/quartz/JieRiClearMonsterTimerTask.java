package com.junyou.stage.model.stage.quartz;

import java.util.TimerTask;

/**
 * 节日清理怪任务
 *@author  DaoZheng Yuan
 *@created 2013-7-1下午3:09:54
 */
public class JieRiClearMonsterTimerTask extends TimerTask{

	private Object[] data;
	
	private String stageId;
	
	
	
	public JieRiClearMonsterTimerTask(Object[] data, String stageId) {
		super();
		this.data = data;
		this.stageId = stageId;
	}



	@Override
	public void run() {
//		StageMsgSender.send2StageInner(null, stageId, StageCommands.INNER_CLEAR_JIERI_MONSTER, data);
	}
	
}
