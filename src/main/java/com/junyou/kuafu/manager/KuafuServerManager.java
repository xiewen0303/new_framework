package com.junyou.kuafu.manager;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.junyou.context.GameServerContext;
import com.junyou.kuafu.share.util.KuafuConfigUtil;
import com.junyou.kuafu.share.util.KuafuServerInfo;
import com.junyou.log.GameLog;
import com.junyou.utils.GameStartConfigUtil;
import com.junyou.utils.common.ObjectUtil;
import com.junyou.utils.datetime.DatetimeUtil;
import com.junyou.utils.datetime.GameSystemTime;
import com.kernel.cache.redis.Redis;
import com.kernel.cache.redis.RedisKey;
import com.kernel.tunnel.kuafu.KuafuNetTunnel;
import com.kernel.tunnel.kuafu.KuafuNetTunnelPoolManager;

public class KuafuServerManager {
	private static KuafuServerManager instance = new KuafuServerManager();

	public static KuafuServerManager getInstance() {
		return instance;
	}

	public static void register() {
		Redis redis = GameServerContext.getRedis();
		if (redis == null) {
			return;
		}
		String serverId = GameStartConfigUtil.getServerId();
		redis.set(RedisKey.buildKuafuServerScoreKey(serverId), 0 + "_" + 0);
		redis.del(RedisKey.buildKuafuServerErrorKey(serverId));
		if (redis.exists(RedisKey.buildKuafuServerKey(serverId))) {
			redis.zadd(RedisKey.KUAFU_SERVER_LIST_KEY, 0, serverId);
		} else {
			GameLog.error("跨服服务器没有找到相关redis配置信息 serverId={}", serverId);
			throw new  RuntimeException("跨服服务器没有找到相关redis配置信息serverId="+serverId);
		}
	}
	
	public static void handleKuafuBoss(){
		Redis redis = GameServerContext.getRedis();
		if (redis == null) {
			return;
		}
		String serverId = GameStartConfigUtil.getServerId();
		redis.set(RedisKey.getKuafuBossStageRoleNumKey(serverId),"0");
	}
	public static void handleKuafuQunXianYan(){
		Redis redis = GameServerContext.getRedis();
		if (redis == null) {
			return;
		}
		String serverId = GameStartConfigUtil.getServerId();
		redis.set(RedisKey.getKuafuQunXianYanStageRoleNumKey(serverId),"0");
	}

	public static void quit() {
		Redis redis = GameServerContext.getRedis();
		if (redis == null) {
			return;
		}
		String serverId = GameStartConfigUtil.getServerId();
		if (!KuafuManager.isMatchServer()) {
			redis.zrem(RedisKey.KUAFU_SERVER_LIST_KEY, serverId);
			GameLog.info("kuafu server quit serverId={}", serverId);
		} else {
			redis.del(RedisKey.KUAFU_MATCH_SERVER);
			GameLog.info("match server quit serverId={}", serverId);
		}
	}

	public KuafuServerInfo getMostIdleKuafuServer(int count) {
		if (count > 100) {
			return null;
		}
		Redis redis = GameServerContext.getRedis();
		if (redis == null) {
			return null;
		}
		Set<String> serverIdSets = redis.zrange(RedisKey.KUAFU_SERVER_LIST_KEY,
				count, count);
		if (serverIdSets == null || serverIdSets.size() == 0) {
			GameLog.error("没有任何跨服配置信息");
			return null;
		}
		String serverId = null;
		for (String e : serverIdSets) {
			serverId = e;
		}
		KuafuServerInfo ret = KuafuServerInfoManager.getInstance()
				.getKuafuServerInfo(serverId);
		if (ret != null) {
			KuafuNetTunnel tunnel = KuafuConfigUtil.getConnection(ret);
			if (tunnel != null && tunnel.isConnected()) {
				KuafuServerInfoManager.getInstance().addKuafuServerInfo(ret);
				KuafuConfigUtil.returnConnection(tunnel);
				redis.del(RedisKey.buildKuafuServerErrorKey(serverId));
				return ret;
			} else {
				KuafuConfigUtil.returnBrokenConnection(tunnel);
				KuafuNetTunnelPoolManager.getInstance().destroyPool(ret);
				String key = RedisKey.buildKuafuServerErrorKey(serverId);
				redis.hset(key, GameStartConfigUtil.getServerId(), String.valueOf(1));
				Map<String, String> timesMap = redis.hgetAll(key);
				if (timesMap != null
						&& timesMap.size() >= KuafuConfigUtil.CAN_NOT_CONNECT_CLEAN_TIMES) {
					redis.zrem(RedisKey.KUAFU_SERVER_LIST_KEY, serverId);
					redis.hset(RedisKey.KUAFU_DELETE_SERVER_LIST, serverId, DatetimeUtil.formatTime(GameSystemTime.getSystemMillTime(), DatetimeUtil.FORMART3));
					GameLog.error("跨服服务器与源服连接不通超过{}个，将被从跨服候选列表中删除，id:{}",
							KuafuConfigUtil.CAN_NOT_CONNECT_CLEAN_TIMES, serverId);
				}
				return getMostIdleKuafuServer(++count);
			}
		}
		Map<String, String> serverInfo = redis.hgetAll(RedisKey
				.buildKuafuServerKey(serverId));
		if (serverInfo == null || serverInfo.isEmpty()) {
			GameLog.error("跨服服务器配置不存在1，将被从跨服候选列表中删除，id:{}", serverId);
			redis.zrem(RedisKey.KUAFU_SERVER_LIST_KEY, serverId);
			redis.hset(RedisKey.KUAFU_DELETE_SERVER_LIST, serverId, DatetimeUtil.formatTime(GameSystemTime.getSystemMillTime(), DatetimeUtil.FORMART3));
			return getMostIdleKuafuServer(++count);
		}
		String inIp = serverInfo.get(KuafuServerInfo.IN_IP_FIELD_KEY);
		String outIp = serverInfo.get(KuafuServerInfo.OUT_IP_FIELD_KEY);
		String netSegment = serverInfo.get(KuafuServerInfo.NET_SEGMENT_FIELD_KEY);
		String port = serverInfo.get(KuafuServerInfo.PORT_FIELD_KEY);
		if(ObjectUtil.strIsEmpty(inIp) || ObjectUtil.strIsEmpty(outIp) || ObjectUtil.strIsEmpty(netSegment) || ObjectUtil.strIsEmpty(port)){
			GameLog.error("跨服服务器配置不存在2，将被从跨服候选列表中删除，id:{}", serverId);
			redis.zrem(RedisKey.KUAFU_SERVER_LIST_KEY, serverId);
			redis.hset(RedisKey.KUAFU_DELETE_SERVER_LIST, serverId, DatetimeUtil.formatTime(GameSystemTime.getSystemMillTime(), DatetimeUtil.FORMART3));
			return getMostIdleKuafuServer(++count);
		}

		String myNetSegment = redis.hget(
				RedisKey.buildKuafuServerKey(GameStartConfigUtil.getServerId()),
				KuafuServerInfo.NET_SEGMENT_FIELD_KEY);
		if(myNetSegment == null){
			GameLog.error("source server can not found in redis, serverId={}",GameStartConfigUtil.getServerId());
			return null;
		}
		String ip = null;
		if (myNetSegment.equals(netSegment)) {
			ip = inIp;
		} else {
			ip = outIp;
		}
		ret = new KuafuServerInfo();
		ret.setIp(ip);
		ret.setPort(Integer.parseInt(port));
		ret.setServerId(serverId);
		KuafuNetTunnel tunnel = KuafuConfigUtil.getConnection(ret);
		if (tunnel != null && tunnel.isConnected()) {
			KuafuServerInfoManager.getInstance().addKuafuServerInfo(ret);
			KuafuConfigUtil.returnConnection(tunnel);
			redis.del(RedisKey.buildKuafuServerErrorKey(serverId));
			return ret;
		} else {
			KuafuConfigUtil.returnBrokenConnection(tunnel);
			KuafuNetTunnelPoolManager.getInstance().destroyPool(ret);
			String key = RedisKey.buildKuafuServerErrorKey(serverId);
			redis.hset(key, GameStartConfigUtil.getServerId(), String.valueOf(1));
			Map<String, String> timesMap = redis.hgetAll(key);
			if (timesMap != null
					&& timesMap.size() >= KuafuConfigUtil.CAN_NOT_CONNECT_CLEAN_TIMES) {
				redis.zrem(RedisKey.KUAFU_SERVER_LIST_KEY, serverId);
				redis.hset(RedisKey.KUAFU_DELETE_SERVER_LIST, serverId, DatetimeUtil.formatTime(GameSystemTime.getSystemMillTime(), DatetimeUtil.FORMART3));
				GameLog.error("跨服服务器与源服连接不通超过{}个，将被从跨服候选列表中删除，id:{}",
						KuafuConfigUtil.CAN_NOT_CONNECT_CLEAN_TIMES, serverId);
			}
			return getMostIdleKuafuServer(++count);
		}
	}
	
	private Map<String,Integer> serverChannelsNum = new ConcurrentHashMap<String, Integer>();
	
	public void addChannelNum(String serverId){
		synchronized(serverChannelsNum){
			Integer num = serverChannelsNum.get(serverId);
			if(num==null){
				serverChannelsNum.put(serverId, 1);
			}else{
				serverChannelsNum.put(serverId, num+1);
			}
		}
	}
	public void removeChannelNum(String serverId){
		synchronized(serverChannelsNum){
			Integer num = serverChannelsNum.get(serverId);
			if(num!=null){
				if(num==1){
					serverChannelsNum.remove(serverId);
				}else{
					serverChannelsNum.put(serverId, num-1);
				}
			}
		}
	}
	
	public int getChannelNum(String serverId){
		Integer num = serverChannelsNum.get(serverId);
		if(num == null){
			return 0;
		}else{
			return num;
		}
	}
}
