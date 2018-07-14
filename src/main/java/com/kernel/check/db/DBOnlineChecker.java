package com.kernel.check.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.game.stop.ChuanQiStopHelper;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.junyou.log.GameLog;

public class DBOnlineChecker {

	private SqlMapClient sqlMapClient;

	private int dbExceptionCount=0;
	
	public void setSqlMapClient(SqlMapClient sqlMapClient) {
		this.sqlMapClient = sqlMapClient;
	}

	public void check(){
		
		Connection con = null;
		try {
			con = sqlMapClient.getDataSource().getConnection();
			PreparedStatement stmt = con.prepareStatement("show tables");
			stmt.executeQuery();
			stmt.close();
		} catch (Exception e) {
			GameLog.error("db check error", e);
			++dbExceptionCount;
			if(dbExceptionCount > 1){
				GameLog.error("close server,triggered by db communication error!");
				ChuanQiStopHelper.stop(" DataBase break connect ");
			}
		}finally{
			if(null != con){
				try {
					con.close();
				} catch (SQLException e) {
				}
			}
		}
		
	}
	
}
