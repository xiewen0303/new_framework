package com.junyou.kuafumatch.util;

import com.junyou.kuafumatch.manager.KuafuMatchServerManager;
import com.junyou.log.GameLog;
import com.kernel.tunnel.kuafu.KuafuNetTunnel;

public class KuafuMatchServerUtil {
	public static boolean isMatchServerAvailable() {
		KuafuNetTunnel tunnel = null;
		boolean returned = false;
		try {
			tunnel = KuafuMatchServerManager.getKuafuMatchConnection();
			if (tunnel != null && tunnel.isConnected()) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			KuafuMatchServerManager.returnKuafuMatchBrokenConnection(tunnel);
			returned = true;
			GameLog.error("", e);
			return false;
		} finally {
			if (!returned) {
				KuafuMatchServerManager.returnKuafuMatchConnection(tunnel);
			}
		}
	}
}
