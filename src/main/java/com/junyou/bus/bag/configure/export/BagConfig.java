package com.junyou.bus.bag.configure.export;

import java.util.Map;

/**
 * 背包格位扩容表
 */
public class BagConfig  {

	private Integer id;
	private int time;
	private int needMoney;//元宝
	private Map<String,Long> attrs;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public int getTime() {
		return time;
	}
	public void setTime(int time) {
		this.time = time;
	}
	public int getNeedMoney() {
		return needMoney;
	}
	public void setNeedMoney(int needMoney) {
		this.needMoney = needMoney;
	}
	public Map<String, Long> getAttrs() {
		return attrs;
	}
	public void setAttrs(Map<String, Long> attrs) {
		this.attrs = attrs;
	}
}
