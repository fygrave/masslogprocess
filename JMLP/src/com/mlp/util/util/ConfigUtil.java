/*ConfigUtil
 * 创建日期 2006-12-31
 * 
 * 作者：dengchuhua
 * Version:1.0. 
 * 说明：
 * 
 * 修改：
 */
package com.mlp.util.util;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

/**
 * @author Colin Doug
 * Version:1.0. 
 * 说明：两种用法
 * 1.用getInstance()获得固定的实例，该实例是创建后就一直存活的,要求存在配置文件：./conf/g_config.conf
 * 2.用 ConfigUtil config =new ConfigUtil(path); 该对象只能通过config访问，注意千万不要用getInstance()来访问，这是两个不同的实例
 * 
 * 修改：
 */
public class ConfigUtil implements Serializable {

	static final long serialVersionUID=478257843625L;
	/**
	 * 
	 */
	public ConfigUtil() {
		super();
		
	}
	public ConfigUtil(String path) {
		super();
		if(path!=null){
			filePath=path;
		}
		File f= new File(System.getProperty("user.dir")+"\\conf");
		
		if(!f.exists()){
			f.mkdir();
		}
		f = new File(filePath);
		if(!f.exists()){
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		f=null;
//		if(path!=null){
//			File f= new File(path);
//			if(!f.exists()){
//				if(f.isDirectory()){
//					
//				}
//			}
//		}
	}
	private static ConfigUtil configUtil=new ConfigUtil(System.getProperty("user.dir")+"\\conf\\g_config.conf");
	
	public static ConfigUtil getInstance(){
		if(null==configUtil){
			configUtil =new ConfigUtil(System.getProperty("user.dir")+"\\conf\\g_config.conf");
		}
		return configUtil;
	}

	private String filePath=System.getProperty("user.dir")+"\\conf\\g_config.conf";
	
	private PropertyUtil prop = null;
	
	
	
	/**
	 * @return 返回 prop。
	 */
	public PropertyUtil getPropertyUtil() {
		if(prop==null){
			prop =loadFile();
		}
		return prop;
	}
	
	public PropertyUtil loadFile(){
		prop =new PropertyUtil(getFilePath());

		return prop;
	}
	/**
	 * @return 返回 filePath。
	 */
	public String getFilePath() {
		if(filePath==null){
			filePath=System.getProperty("user.dir")+"\\conf\\globalConfig.conf";
			File f= new File(System.getProperty("user.dir")+"\\conf");
			
			if(!f.exists()){
				f.mkdir();
			}
			f = new File(filePath);
			if(!f.exists()){
				try {
					f.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			f=null;
		}
		return filePath;
	}
	/**
	 * @param filePath 要设置的 filePath。
	 */
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	
	public synchronized  String getProperties(String key){
		return getPropertyUtil().getProperty(key);
	}
	
	
}
