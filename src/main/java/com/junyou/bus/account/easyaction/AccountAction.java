package com.junyou.bus.account.easyaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.game.easyexecutor.annotation.EasyMapping;
import com.game.easyexecutor.annotation.EasyWorker;
import com.junyou.bus.recharge.service.RechargeService;
import com.junyou.cmd.InnerCmdType;
import com.junyou.module.GameModType;
import com.kernel.pool.executor.Message;

/**
 * 充值
 */
@Controller
@EasyWorker(moduleName = GameModType.ROLE_BUS_MODULE)
public class AccountAction {
	@Autowired
	private RechargeService rechargeService;
	
	@EasyMapping(mapping = InnerCmdType.REV_RECHARGE)
	public void revRecharge(Message inMsg){
		Long userRoleId = inMsg.getRoleId();
		rechargeService.finishRecharge(userRoleId);
	}
}
