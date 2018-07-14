package com.junyou.bus.bag.manage;
  
import java.util.Map; 
import java.util.concurrent.ConcurrentHashMap;

/**
 *@Description: 容器管理类
 */
public class BagManage {
	/**
	 * roleId=bag
	 */
	private static Map<Long,Bag> bags=new ConcurrentHashMap<Long,Bag>();
	
	
	public static Bag getBag(Long roleId){
		return bags.get(roleId);
	}
	
	public static void addBag(Bag bag){
		bags.put(bag.getRoleId(), bag);
	}
	
	/**
	 * 下线清理玩家背包数据
	 */
	public static void clear(Long roleId){
		bags.remove(roleId);
	}
	 
}
