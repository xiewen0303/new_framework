package com.kernel.tunnel.stage;

/**
 * 
 */
public class MsgBuffer {
	
	private String[] roleIds;
	
	private String command;
	
	private Object data;

	/**
	 * @param roleIds
	 * @param command
	 * @param data
	 */
	public MsgBuffer(String[] roleIds, String command, Object data) {
		// TODO Auto-generated constructor stub
		this.roleIds = roleIds;
		this.command = command;
		this.data = data;
	}

	public String[] getRoleIds() {
		return roleIds;
	}

	public void setRoleIds(String[] roleIds) {
		this.roleIds = roleIds;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
	
	

}
