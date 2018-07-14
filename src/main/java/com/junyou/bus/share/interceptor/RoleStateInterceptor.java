package com.junyou.bus.share.interceptor;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.game.easyexecutor.Interceptor.IInterceptor;
import com.junyou.cmd.InnerCmdType;
import com.junyou.gs.share.thread.RoleidThreadShare;
import com.junyou.public_.share.export.PublicRoleStateExportService;
import com.kernel.pool.executor.Message;

/**
 * 验证角色在线状态
 */
@Service
public class RoleStateInterceptor implements IInterceptor {
	
	/**
	 * 登录内部跳转的过滤掉
	 */
	private List<Short> exceptCmds = Arrays.asList(
			InnerCmdType.BUS_INIT_IN,
			InnerCmdType.BUS_INIT_OUT);
	
	
	private PublicRoleStateExportService publicRoleStateExportService;

	@Autowired
	public void setPublicRoleStateExportService(PublicRoleStateExportService publicRoleStateExportService) {
		this.publicRoleStateExportService = publicRoleStateExportService;
	}

	@Override
	public boolean after(Object message) {
		return true;
	}

	@Override
	public boolean before(Object message) {
		Long roleid = ((Message)message).getRoleId();
		 
		// 例外
		if(exceptCmds.contains(((Message)message).getCommand())) return true;
		RoleidThreadShare.setRoleId(roleid);
		return publicRoleStateExportService.isPublicOnline(roleid);
	}

}
