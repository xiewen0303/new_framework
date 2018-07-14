/**
 * 
 */
package com.junyou.configure.loader;


/**
 * 配置加载器
 */
public interface IConfigureLoader {

	/**
	 * 加载文件
	 */
	byte[] load(String configureFileName);
	
	/**
	 * 读取标志
	 */
	Object[] loadSign(String filename,DirType dirType);

}
