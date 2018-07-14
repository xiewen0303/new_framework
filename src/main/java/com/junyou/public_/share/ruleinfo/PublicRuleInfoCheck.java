package com.junyou.public_.share.ruleinfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.game.easyexecutor.cmd.CmdGroupInfo;
import com.junyou.public_.share.service.PublicRoleStateService;
import com.kernel.pool.executor.IRuleInfoCheck;

@Service
public class PublicRuleInfoCheck implements IRuleInfoCheck {

	private String public_group = "public";
	
	private PublicRoleStateService publicRoleStateService;
	
	@Override
	public Boolean valid(Object ruleinfo) {
	
		if(CmdGroupInfo.isGroup((String)ruleinfo, public_group)){
			return false;
		}
		
		if(publicRoleStateService.isPublicOnline((Long)ruleinfo)){
			return false;
		}
		
		return true;
	}
	
	@Autowired
	public void setPublicRoleStateService(PublicRoleStateService publicRoleStateService) {
		this.publicRoleStateService = publicRoleStateService;
	}
}
