package com.junyou.event;

import com.alibaba.fastjson.JSONArray;
import com.junyou.log.LogPrintHandle;

/**
 * 套装铸神
 * @author wind
 * 
 */
public class EquipZhuShenLogEvent extends AbsGameLogEvent {

	private static final long serialVersionUID = 1L;

	private Long userRoleId;
	private Long guid;
	private String roleName;
	private String goodsId;
	private JSONArray consumeItem;
	private int needJBVal;//强化所需要的金币数量
	private int beginLevel;	//强化之前等级
	private int endLevel;	//强化之后等级
	
	public EquipZhuShenLogEvent(Long userRoleId, Long guid,
			String roleName, String goodsId, JSONArray consumeItem,
			int needJBVal,int beginLevel, int endLevel) {
		super(LogPrintHandle.EQUIP_TAO_ZHUANG_ZHUSHEN);
		this.userRoleId = userRoleId;
		this.guid = guid;
		this.roleName = roleName;
		this.goodsId = goodsId;
		this.consumeItem = consumeItem;
		this.needJBVal = needJBVal;
		this.beginLevel = beginLevel;
		this.endLevel = endLevel;
	}
	
	public int getBeginLevel() {
		return beginLevel;
	}
	public void setBeginLevel(int beginLevel) {
		this.beginLevel = beginLevel;
	}
	public int getEndLevel() {
		return endLevel;
	}
	public void setEndLevel(int endLevel) {
		this.endLevel = endLevel;
	}
	public Long getUserRoleId() {
		return userRoleId;
	}
	public void setUserRoleId(Long userRoleId) {
		this.userRoleId = userRoleId;
	}
	public Long getGuid() {
		return guid;
	}
	public void setGuid(Long guid) {
		this.guid = guid;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	public String getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(String goodsId) {
		this.goodsId = goodsId;
	}
	public JSONArray getConsumeItem() {
		return consumeItem;
	}
	public void setConsumeItem(JSONArray consumeItem) {
		this.consumeItem = consumeItem;
	}
	public int getNeedJBVal() {
		return needJBVal;
	}
	public void setNeedJBVal(int needJBVal) {
		this.needJBVal = needJBVal;
	}
}