package com.junyou.bus.bag.service.useprop.rule;

import com.junyou.bus.bag.service.useprop.IUsePropCallBack;
/**
 * 资质丹
 * @author LiuYu
 * @date 2015-6-2 下午4:56:40
 */
public class ZZDCheckRule implements IUsePropCheckRule {

	@Override
	public void propHandle(IUsePropCallBack callback) {
		callback.tangbaoZizhiHandle();
	}

}
