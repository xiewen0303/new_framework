/**
 *@Copyright:Copyright (c) 2008 - 2100
 *@Company:JunYou
 */
package com.junyou.bus.bag.service.useprop.rule;

import com.junyou.bus.bag.service.useprop.IUsePropCallBack;

/**
 *@Description
 *@Author Yang Gao
 *@Since 2016-8-1
 *@Version 1.1.0
 */
public class AddVipExpCheckRule implements IUsePropCheckRule {

    @Override
    public void propHandle(IUsePropCallBack callback) {
        callback.addVipExpHandle();
    }

}
