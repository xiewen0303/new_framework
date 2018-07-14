package com.kernel.pool.executor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import com.kernel.data.filedb.Filedb;
import com.kernel.tunnel.BusinessExexutorFactory;

/**
 * 协议统计执行统计
 */
public class MsgStatistics {

	private boolean needRecorded = BusinessExexutorFactory.isOpenStatistics;
	
	private String componentName = "msg_statistic";
	
	private Map<Short, MsgRecord> records = new HashMap<Short, MsgStatistics.MsgRecord>();
	
	public MsgStatistics() {
	}

	public boolean isNeedRecorded() {
		return needRecorded;
	}
	
	public synchronized void flush2File(){
		
		File msgStatisticFile = Filedb.getFile(componentName, componentName);
		if(null == msgStatisticFile) msgStatisticFile = Filedb.mkFile(componentName, componentName);
		
		PrintWriter writer = null;
		try {

			writer = new PrintWriter(new FileWriter(msgStatisticFile));
			for(MsgRecord record : records.values()){
				writer.println(record.toString());
			}
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}finally{
			if(null != writer){
				writer.close();
			}
		}

	}
	
	public synchronized void record(Short cmd,long executeTime){
		MsgRecord msgRecord = records.get(cmd);
		if(null == msgRecord){
			msgRecord = new MsgRecord(cmd);
			records.put(cmd, msgRecord);
		}
		msgRecord.record(cmd,executeTime);
	}
	
	private class MsgRecord{
		
		private Short cmd;
		
		private long maxExecuteTime = 0;
		private long minExecuteTime = Long.MAX_VALUE;
		private long sumExecuteTime = 0;
		private long executeCount = 0;

		public MsgRecord(Short cmd) {
			this.cmd = cmd;
		}

		public void record(Short cmd,long executeTime){
			executeCount++;
			maxExecuteTime = maxExecuteTime < executeTime ? executeTime : maxExecuteTime;
			minExecuteTime = minExecuteTime < executeTime ? minExecuteTime : executeTime;
			sumExecuteTime += executeTime;
		}
		
		public String toString(){
			return "cmd:"+cmd+"\tcount:"+executeCount+"\ttotal:"+sumExecuteTime+"\tmax:"+maxExecuteTime+"\tmin:"+minExecuteTime+"\tavg:"+(sumExecuteTime/executeCount);
		}
	}
}
