package com.junyou.bus.account.entity;

import com.junyou.bus.account.entity.RoleAccount;

/**
 * 角色货币Wrapper
 */
public class RoleAccountWrapper {
	
	private RoleAccount roleAccount;
	
	public RoleAccountWrapper(RoleAccount roleAccount){
		this.roleAccount = roleAccount;
	}
	
	/**
	 * 绑定元宝（礼券）
	 * @return
	 */
	public long getBindYb(){
		return roleAccount.getBindYb();
	}
	
	/**
	 * 金币
	 * @return
	 */
	public long getJb(){
		return roleAccount.getJb();
	}
	
	/**
	 * 元宝
	 * @return
	 */
	public long getYb(){
		return roleAccount.getReYb() + roleAccount.getNoReYb();
	}
	
	public String getUserId(){
		return roleAccount.getUserId();
	}
	
	public String getServerId(){
		return roleAccount.getServerId();
	}
}
