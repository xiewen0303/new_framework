package com.junyou.configure.loader;

import java.net.URL;

import com.junyou.log.GameLog;
import com.junyou.utils.GameStartConfigUtil;
import com.junyou.utils.common.DownloadPathUtil;
import com.junyou.utils.md5.Md5Utils;

/**
 * 远程配置平台的配置
 */
public class RemoteConfigureLoaderByPlatform implements IConfigureLoader {
	
	public final static String SUFFIX = ".jat";
	public final static String PUBLIC_SUFFIX = "_public";
	
	private String gameconfigBaseUrl;
	
	public void setGameconfigBaseUrl(String gameconfigBaseUrl) {
		this.gameconfigBaseUrl = gameconfigBaseUrl.trim();
	}

	@Override
	public byte[] load(String fileName) {
		
		try {
			byte[] data = null;
			
			URL url = new URL(fileName);
			data = DownloadPathUtil.download(url);
			
			return data;
			
		} catch (Exception e) {
			GameLog.errorConfig("load config error :" +  fileName + e);
		}
		
		return null;
	}

	@Override
	public Object[] loadSign(String configureName,DirType dirType) {
		
		try {
			
			String requestUrl = getDirTypeRequestUrl(gameconfigBaseUrl,configureName);
			
			String path = requestUrl+"_"+GameStartConfigUtil.getPlatfromId()+SUFFIX;
			URL url = new URL(path);
			byte[] data = DownloadPathUtil.downloadFuDate(url);
			if(data == null){
				path = requestUrl+PUBLIC_SUFFIX+SUFFIX;
				url = new URL(path);
				data = DownloadPathUtil.download(url);
			}
			
			if(data == null){
				return null;
			}
			
			return new Object[]{Md5Utils.md5Bytes(data).trim(),path.trim()};
		} catch (Exception e) {
			GameLog.errorConfig("load url:"+gameconfigBaseUrl+" sign error :" + configureName + e);
		}
		
		return null;
	}

	
	private String getDirTypeRequestUrl(String server,String configureName) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(server);
		buffer.append("/").append(configureName);
		
		return buffer.toString();
	}

	

}
