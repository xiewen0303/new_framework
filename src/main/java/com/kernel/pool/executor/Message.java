package com.kernel.pool.executor;

import com.alibaba.fastjson.JSONArray;

/**
 * 消息交换协议
 * 1、数据结构(object[] msg)
 * 2、索引注释
 * 		msg[0]:指令
 * 		msg[1]:数据

 * 		msg[5]:会话id (输入时由连接管理组件填充，输出时由swap填充)
 * 		msg[6]:连接ip 由连接管理组件填充（输出时不填充）

 * 		msg[2]:dest_type 消息输入组件类型 swap用 由组件发送时填充
 * 		msg[3]:from_type 消息来源 swap用 由组件发送时填充		node_from_type  类型
 * 		msg[9]:消息输出标记0:会话id，1：角色id swap用 由组件发送时填充
 * 		msg[4]:broadcast_type 消息广播类型1个目标 多个目标 全部目标 最终消息分发用 由组件发送时填充

 * 		msg[7]:目标角色id[s] 组件线程分发用 由swap填充
 * 		msg[8]:目标角色场景id 组件线程分发用 由swap填充
 * 		msg[10]:token值，适用内部定时消息
 */
public class Message {

	private Object[] msgSource;
	
	public Message(Object[] msg) {
		this.msgSource = msg;
	}
	
	public Short getCommand(){
		return (Short) msgSource[0];
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getData(){
		return (T)msgSource[1];
	}

	public Long getRoleId(){
		return (Long) msgSource[7];
	}
	
	public String getRoleIdInfo(){
		Object value = msgSource[7];
		return value.toString();
	}

	public String getStageId(){
		Object stageid = msgSource[8];
		if(null != stageid){
			return (String) stageid;
		}
		return "";
	}
	
	public String getSessionid() {
		Object sid = msgSource[5];
		if(null !=sid){
			if(sid instanceof String){
				return (String) sid;
			}
		}
		return "0";
	}

	public Object[] getToken(){
		if(msgSource.length == 11){
			Object tokenObject = msgSource[10];
			if(null != tokenObject){
				return (Object[]) tokenObject;
			}
		}
		return null;
	}
	
	public Object[] getMsgSource(){
		return this.msgSource;
	}
	
	@Override
	public String toString() {
		return JSONArray.toJSONString(msgSource);
	}
}
