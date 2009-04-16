/**
 * 
 */
package com.mlp.util.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

/**
 *@author Colin Doug
 *版本：1.0
 *说明：
 * 必须在根路径下创建文件夹conf，并且有一个文件g_logconf.conf，该文件用来定义需要打印debug信息的模块，可以为空，格式如下：
 * 
 * com.xx.yyy=true
 * com.xxx.zz=true
 * 如果不需要打印某模块的debug信息，则只需把true改为false,或者直接删除整行
 * 
 * 用法：在模块中添加：
 * 
 * private static  GLog log = GLog.getInstance("com.yourClassName");
 * 
 * 或：
 * private GLog log = GLog.getInstance(getClass().getName());
 * 
 */
public class GLog {

	/**
	 * 
	 */
	private GLog(String classPathName) {
		super();
	
		this.classPathName=classPathName;
		String s =getLogConf().getProperty(classPathName);
		if(s==null || s.compareToIgnoreCase("true")!=0){
			setIsDebug(false);
		}else{
			setIsDebug(true);
		}
		
	}
	
	private static HashMap<String,GLog> logKeyObjMap=null;
	
	public static  HashMap<String,GLog> getLogKeyObjMap(){
		if(logKeyObjMap==null){
			logKeyObjMap=new HashMap<String,GLog>(50);
		}
		return logKeyObjMap;
	}
	
	public synchronized static GLog getInstance(String classPathName){
		GLog log =getLogKeyObjMap().get(classPathName); 
		if(log!=null){
			return log;
		}else{
			log = new GLog(classPathName);
			getLogKeyObjMap().put(classPathName,log);
			System.out.println("GLog队列长度="+getLogKeyObjMap().size());
		}
		return log;
	}
	
	private String classPathName=null;
	
	private  static Properties logConf=null;
	
	public static Properties getLogConf(){
		if (logConf==null) {
			logConf = new Properties();
			
			try {
				String dp =System.getProperty("user.dir")+"\\conf";
				String fp = dp+"\\g_logconf.conf";
				File f =new File(dp);
				if(!f.exists()){
					f.mkdir();
				}
				f = new File(fp);
				if(!f.exists()){
					try {
						f.createNewFile();
					} catch (IOException e) {
						
						e.printStackTrace();
					}
				}
				logConf.load(GUtil.getFileInputStream("conf\\g_logconf.conf"));
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println(e.getMessage());
			}
		}
		return logConf;
	}
	
	private boolean isDebug=false;
	private boolean isDebug(){
		return isDebug;
	}
	public void setIsDebug(boolean debug){
		this.isDebug=debug;
	}
	
	public void out(Object msg){
		if(isDebug())
		System.out.println(classPathName+":"+msg);
	}
	public void out(String msg){
		if(isDebug())
		System.out.println(classPathName+":"+msg);
	}
	
	public void err(Object msg){
		
		System.err.println(classPathName+":"+msg);
	}
	public void err(String msg){
		
		System.err.println(classPathName+":"+msg);
	}

}
