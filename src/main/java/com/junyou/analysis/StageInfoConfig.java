package com.junyou.analysis;

import com.alibaba.fastjson.JSON;

/**
 * 场景基本启动配置
 */
public class StageInfoConfig {

	//aoiWidth:aoi宽;
	private int aoiWidth;
	
	//aoiHeight:aoi高;
	private int aoiHeight;
	
	//monsterDeadShow:怪物死亡是否还同步AOI
	private boolean monsterDeadShow;

	/**
	 * aoi宽
	 * @return
	 */
	public int getAoiWidth() {
		return aoiWidth;
	}

	public void setAoiWidth(int aoiWidth) {
		this.aoiWidth = aoiWidth;
	}

	/**
	 * aoi高
	 * @return
	 */
	public int getAoiHeight() {
		return aoiHeight;
	}

	public void setAoiHeight(int aoiHeight) {
		this.aoiHeight = aoiHeight;
	}

	/**
	 * 怪物死亡是否还同步AOI
	 * @return true:同步,false:怪物死亡AOI不同步
	 */
	public boolean isMonsterDeadShow() {
		return monsterDeadShow;
	}

	public void setMonsterDeadShow(boolean monsterDeadShow) {
		this.monsterDeadShow = monsterDeadShow;
	}
	
	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}
}
