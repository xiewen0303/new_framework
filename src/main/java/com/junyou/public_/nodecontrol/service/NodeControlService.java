package com.junyou.public_.nodecontrol.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.junyou.bus.client.io.export.ClientIoExportService;
import com.junyou.constants.GameConstants;
import com.junyou.log.GameLog;
import com.junyou.public_.share.export.PublicRoleStateExportService;
import com.kernel.spring.container.DataContainer;

@Service
public class NodeControlService {
	
	private boolean usecache = true;

	@Autowired
	private PublicRoleStateExportService publicRoleStateExportService;
	@Autowired
	private ClientIoExportService clientIoExportService;
	@Autowired
	private DataContainer dataContainer;
	
//	@Autowired
//	@Qualifier("publicCacheManager")
//	private CacheManager publicCacheManager;
	
	/**
	 * 设置一个角色为在线状态
	 * @param roleid
	 */
	public void change2online(Long roleid) {
		publicRoleStateExportService.change2PublicOnline(roleid);
	}

	/**
	 * 设置一个角色为离线状态
	 * @param roleid
	 */
	public void change2offline(Long roleid) {
		publicRoleStateExportService.change2PublicOffline(roleid);
	}

	
	/**
	 * 记录微端登录状态
	 * @param userRoleId
	 */
	public void addRoleWeiDuan(Long userRoleId){
		dataContainer.putData(GameConstants.WEI_DUAN_VALUE, userRoleId.toString(), true);
	}
	
	/**
	 * 下线处理微端登录
	 * @param userId
	 */
	private void removeRoleWeiDuan(Long userRoleId){
		try {
			dataContainer.removeData(GameConstants.WEI_DUAN_VALUE, userRoleId.toString());
		} catch (Exception e) {
			GameLog.error("removeRoleWeiDuan error !");
		}
	}
	/**
	 * 获取微端在线总数
	 * @return
	 */
	public int getWeiDuanCount(){
		return dataContainer.getComponentDataSize(GameConstants.WEI_DUAN_VALUE);
	}
	
	/**
	 * 是否是微端登录
	 * @param userRoleId
	 * @return true:是
	 */
	public boolean isWeiDuanLogin(Long userRoleId){
		Boolean isWeiDuan = dataContainer.getData(GameConstants.WEI_DUAN_VALUE, userRoleId.toString());
		if(isWeiDuan == null){
			return false;
		}
		
		return isWeiDuan;
	}
	
	/**
	 * 处理角色节点上线业务
	 * @param roleid
	 */
	public void nodeLogin(Long roleid,String ip) {
		clientIoExportService.roleIn(roleid,ip);
	}


	public void nodeExit(Long roleid) {

		try{

			nodeExitHandle(roleid);
			
		}catch (Exception e) {
			GameLog.error("node exit error",e);
		}finally{

			try{
				clientIoExportService.roleOut(roleid);
			}catch (Exception e) {
				GameLog.error("",e);
			}
			
			// 在node swap 中注销node信息
//			nodeSwapExportService.unregNodeInfo(roleid);
		}
		
		
		

	}

	public void nodeExitHandle(Long roleid){
		//微端登录
		removeRoleWeiDuan(roleid);

	}
	
	/**
	 * 服务器关闭时处理角色下线业务
	 * @param roleid
	 */
	public void nodeExitOnServerClose(Long roleid) {
		nodeExitHandle(roleid);
	}

	
}
