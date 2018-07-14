package com.junyou.utils.md5;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.junyou.log.GameLog;

/**
 * @description MD5工具类
 * @author DaoZheng Yuan
 * @created 2010-12-17下午12:52:28
 */
public class Md5Utils {
	
	/**
	 * MD5加密返回32位
	 * @param passText
	 * @return
	 */
	public static String md5To32(String passText){
		StringBuffer buff = new StringBuffer("");
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(passText.getBytes());
			byte[] bt = md.digest();
			
			int i;
			for (int offset = 0; offset < bt.length; offset++) {
				i = bt[offset];
				if(i < 0) i += 256;
				if(i < 16) buff.append("0");
				buff.append(Integer.toHexString(i));
			}
			
			return buff.toString();
		} catch (NoSuchAlgorithmException e) {
			GameLog.error("", e);
			return null;
		}
	}
	
	/**
	 * MD5加密返回16
	 * @param passText
	 * @return
	 */
	public static String md5To16(String passText){
		StringBuffer buff = new StringBuffer("");
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(passText.getBytes());
			byte[] bt = md.digest();
			
			int i;
			for (int offset = 0; offset < bt.length; offset++) {
				i = bt[offset];
				if(i < 0) i += 256;
				if(i < 16) buff.append("0");
				buff.append(Integer.toHexString(i));
			}
			return buff.toString().substring(8,24);
		} catch (NoSuchAlgorithmException e) {
			GameLog.error("", e);
			return null;
		}
	}
	
	public static String md5Bytes(byte[] data){
		String result = null;
		try{
			MessageDigest md = MessageDigest.getInstance("MD5");
			
			md.update(data);
			result = byteArrayToHexString(md.digest());
		}catch (Exception e) {
			GameLog.error("", e);
		}
		
		return result;
	}
	public static String byteArrayToHexString(byte[] b) {
		StringBuffer resultSb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			resultSb.append(byteToHexString(b[i]));
		}
		return resultSb.toString();
	}
	private final static String[] hexDigits = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };
	private static String byteToHexString(byte b) {
		int n = b;
		if (n < 0)
			n = 256 + n;
		int d1 = n / 16;
		int d2 = n % 16;
		return hexDigits[d1] + hexDigits[d2];
	}
	
	public static void main(String[] args) {
		String Str16 = md5To16("test123");
		System.out.println(Str16);
		String Str32 = md5To32("test123");
		System.out.println(Str32);
	}
	
}
