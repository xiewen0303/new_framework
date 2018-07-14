
package message;

import com.google.protobuf.GeneratedMessageV3;
import log.LogUtil;
import java.util.HashMap;
import java.util.Map;

/**
 * 自动生成
 */
public class MessageCode {

	public final static short CS_ChatMessage = 103;
	public final static short SC_ChatMessage = 104;
	public final static short CS_Login = 105;
	public final static short SC_Login = 106;
	public final static short SC_UpdateItemInfos = 109;
	public final static short CS_ChildCopyInfo = 112;
	public final static short SC_ChildCopyInfo = 113;
	public final static short CS_EnterCopy = 114;
	public final static short SC_EnterCopy = 115;
	public final static short CS_MergeItem = 116;
	public final static short SC_MergeItem = 117;
	public final static short CS_EnterFinish = 118;
	public final static short SC_EnterFinish = 119;
	public final static short CS_CreateItem = 121;
	public final static short SC_CreateItem = 122;
	public final static short SC_LoginFinish = 124;
	public final static short CS_GetParentRewards = 125;
	public final static short SC_GetParentRewards = 126;
	public final static short CS_CreateRole = 127;
	public final static short SC_CreateRole = 128;

	public static Map<String,Short> nameCodes = new HashMap<>();

	static {
	    nameCodes.put("message.Chat$CS_ChatMessage",CS_ChatMessage);
	    nameCodes.put("message.Chat$SC_ChatMessage",SC_ChatMessage);
	    nameCodes.put("message.Login$CS_Login",CS_Login);
	    nameCodes.put("message.Login$SC_Login",SC_Login);
	    nameCodes.put("message.Bag$SC_UpdateItemInfos",SC_UpdateItemInfos);
	    nameCodes.put("message.Copy$CS_ChildCopyInfo",CS_ChildCopyInfo);
	    nameCodes.put("message.Copy$SC_ChildCopyInfo",SC_ChildCopyInfo);
	    nameCodes.put("message.Copy$CS_EnterCopy",CS_EnterCopy);
	    nameCodes.put("message.Copy$SC_EnterCopy",SC_EnterCopy);
	    nameCodes.put("message.Bag$CS_MergeItem",CS_MergeItem);
	    nameCodes.put("message.Bag$SC_MergeItem",SC_MergeItem);
	    nameCodes.put("message.Copy$CS_EnterFinish",CS_EnterFinish);
	    nameCodes.put("message.Copy$SC_EnterFinish",SC_EnterFinish);
	    nameCodes.put("message.Bag$CS_CreateItem",CS_CreateItem);
	    nameCodes.put("message.Bag$SC_CreateItem",SC_CreateItem);
	    nameCodes.put("message.Login$SC_LoginFinish",SC_LoginFinish);
	    nameCodes.put("message.Copy$CS_GetParentRewards",CS_GetParentRewards);
	    nameCodes.put("message.Copy$SC_GetParentRewards",SC_GetParentRewards);
	    nameCodes.put("message.Login$CS_CreateRole",CS_CreateRole);
	    nameCodes.put("message.Login$SC_CreateRole",SC_CreateRole);
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
	public static GeneratedMessageV3 getGenMessage(short cmd, byte[] data) {
		try {
			switch (cmd) {
                case CS_ChatMessage:
                	return Chat.CS_ChatMessage.parseFrom(data);
                case CS_Login:
                	return Login.CS_Login.parseFrom(data);
                case CS_ChildCopyInfo:
                	return Copy.CS_ChildCopyInfo.parseFrom(data);
                case CS_EnterCopy:
                	return Copy.CS_EnterCopy.parseFrom(data);
                case CS_MergeItem:
                	return Bag.CS_MergeItem.parseFrom(data);
                case CS_EnterFinish:
                	return Copy.CS_EnterFinish.parseFrom(data);
                case CS_CreateItem:
                	return Bag.CS_CreateItem.parseFrom(data);
                case CS_GetParentRewards:
                	return Copy.CS_GetParentRewards.parseFrom(data);
                case CS_CreateRole:
                	return Login.CS_CreateRole.parseFrom(data);
				default:
					LogUtil.error("message cmd is not exist,cmd:"+cmd);
					break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}