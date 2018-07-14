package com.game.easyexecutor.manager;

public interface IEasyWorkerContainer {

	public Object getWorker(String workerName);
	
	public Object getWorker(Class<?> workerClass);
	
}
