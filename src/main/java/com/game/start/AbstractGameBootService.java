package com.game.start;
 
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.junyou.log.GameLog;
import com.junyou.utils.datetime.GameSystemTime;
import com.junyou.utils.exception.GameCustomException;

/**
 * 游戏启动和停机服务抽象类
 */
public abstract class AbstractGameBootService {
	
	enum State {
        /**
         * 初始值
         */
		STATE_NEW,
        /**
         * 启动中
         */
        STATE_STARTING,
		/**
		 * 运行中
		 */
		STATE_RUNNING,
        /**
         * 停止中
          */
        STATE_STOPPING,
        /**
         * 终止
         */
        STATE_TERMINATED
	}
	
	static class ShutdownThread extends Thread {
		@SuppressWarnings("unused")
		private Logger logger = LogManager.getLogger("stop_logger");
		
		private final AbstractGameBootService service;

		public ShutdownThread(AbstractGameBootService service) {
			this.service = service;
		}

		@Override
		public void run() {
			GameLog.stopLogDebug("AbstractGameBootService test ShutdownThread");
			
			try {
				if (service != null && !service.isStoppingOrTerminated()) {
					service.stop();
				}
			} catch (Exception e) {
				GameLog.stopLogError(" AbstractGameBootService stop err!",e);
			}
		}
	}
	
	public abstract String getServiceName();
	private volatile State state = State.STATE_NEW;
	protected final String[] args;
	
	public AbstractGameBootService(String[] args) {
		this.args = args;

		// 设置shutdown hook
		Runtime.getRuntime().addShutdownHook(new ShutdownThread(this));
	}
	
	protected abstract void onStart() throws GameCustomException;

	protected abstract void onRun() throws GameCustomException;

	protected abstract void onStop() throws GameCustomException;
	
	public State getState() {
		return state;
	}

	public boolean isRunning() {
		return state == State.STATE_RUNNING;
	}

	public boolean isStoppingOrTerminated() {
		return state == State.STATE_STOPPING || state == State.STATE_TERMINATED;
	}

	public void start() {
		GameLog.startLogDebug("starting {} service",this.getServiceName());
		long startTime = GameSystemTime.getSystemMillTime();

		if (state != State.STATE_NEW) {
			GameLog.startLogDebug("invalid state: {}", state);
			return;
		}

		state = State.STATE_STARTING;
		try {
			onStart();
			state = State.STATE_RUNNING;
			GameLog.startLogDebug("{} is running, delta={} ms", getServiceName(), (GameSystemTime.getSystemMillTime() - startTime));
			onRun();
			
			//日志统计第三方服务器启动
			//StartOrStopLog2db.start(args);
		} catch (Exception e) {
			GameLog.startLogError("failed to starting service: " + getServiceName(), e);
			System.exit(1);
			return;
		}
	}
	
	public void stop() {
		GameLog.stopLogDebug("stopping service: {}", getServiceName());
		if (state == State.STATE_NEW || state == State.STATE_STOPPING || state == State.STATE_TERMINATED) {
			GameLog.stopLogError("invalid state: {}", state);
			return;
		}

		state = State.STATE_STOPPING;
		try {
			onStop();
			state = State.STATE_TERMINATED;
			GameLog.stopLogDebug("stoped {}", getServiceName());
			
//			//日志统计第三方服务器停止
//			StartOrStopLog2db.stop();
		} catch (Exception e) {
			GameLog.stopLogError("failed to stopping service: " + getServiceName(), e);
		}
	}
}
