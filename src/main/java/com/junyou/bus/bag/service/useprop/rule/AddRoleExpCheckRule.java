package com.junyou.bus.bag.service.useprop.rule;

import com.junyou.bus.bag.service.useprop.IUsePropCallBack;

public class AddRoleExpCheckRule implements IUsePropCheckRule {

	@Override
	public void propHandle(IUsePropCallBack callback) {
		callback.addRoleExpHandle();
	}

}
