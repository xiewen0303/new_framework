package com.junyou.bus.share.ruleinfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.junyou.bus.share.service.RoleStateService;
import com.kernel.pool.executor.IRuleInfoCheck;


@Service
public class BusRuleInfoCheck implements IRuleInfoCheck {
	
	private RoleStateService roleStateService;
	
	@Override
	public Boolean valid(Object ruleinfo) {
		return !roleStateService.isOnline((Long) ruleinfo);
	}
	
	@Autowired
	public void setRoleStateService(RoleStateService roleStateService) {
		this.roleStateService = roleStateService;
	}
}
