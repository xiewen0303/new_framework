package com.message;

import java.util.HashMap;
import java.util.Map;
import com.google.protobuf.GeneratedMessageV3;
import com.junyou.log.GameLog;

/**
 * 自动生成
 */
public class MessageCode {

	public final static short Login_C = 0; 
	public final static short Login_S = 1; 

	public static Map<String,Short> nameCodes = new HashMap<>();

	static {
	    nameCodes.put("com.message.MessageInfo$Login_S",Login_S);
	}

	public static short getCode(String codeName) {
		return nameCodes.get(codeName);
	}

    /**
	 * 通过协议号解析成协议对象
	 * @param cmd   协议号
	 * @param data  协议二进制数据
     * @return      协议对象
     */
	public static GeneratedMessageV3 parseMessage(short cmd, byte[] data) {
		try {
			switch (cmd) {
                case Login_C:
                	return data==null?MessageInfo.Login_C.newBuilder().build():MessageInfo.Login_C.parseFrom(data);
				default:
					GameLog.error("message cmd is not exist,cmd:"+cmd);
					break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}