package com.junyou.bus.bag.service.useprop.rule;

import com.junyou.bus.bag.service.useprop.IUsePropCallBack;

public class AddZuoQiQNDCheckRule implements IUsePropCheckRule {

	@Override
	public void propHandle(IUsePropCallBack callback) {
		callback.addZuoQiQNDHandle();
	}
}
