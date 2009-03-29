/*
* author:Colin Doug
* Version:1.0.1.1
* 修改：改变了模式，废除了原来的单一实例模式，
* 添加了重载的构造函数public PropertyUtil(String filepath) 
* 生成了一系列get,set方法，添加tostring()
*/ 
package com.mlp.util.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Enumeration;

/**
* 这个类用来读取或者设置配置文件的信息
* sample:
*       PropertyUtil myprop = PropertyUtil.getInstance();
*       myprop.setPropertyFileName("conf/sansm_config.conf");
*       String database = myprop.getProperty("database");
*       System.out.println (database);
*/
public class PropertyUtil{

  private String fileName = null;
  private String fileHeader ="Parameters for  software.";
  private Properties prop = new Properties();
  private File f = null;
  private String  filePath = null;

	//测试
/*	public static void main(String[] args) throws Exception {
    	PropertyUtil myprop = PropertyUtil.getInstance();
    	myprop.loadPropertyFile("conf/sansm_config.conf");
    	String database = myprop.getProperty("name");
    	System.out.println (database);
    }
	*/
    public PropertyUtil () {    	
    	
	}
    public PropertyUtil(String filepath) {
    	this.filePath = filepath;
    	try{
	         this.loadPropertyFile(filepath);
		}catch(Exception e){
			System.err.println(e.getMessage()+"  "+e.getStackTrace());		
			
		}
    	
    	
    }   	
    /**
     * 
     * @param skey 用来匹配属性键值的字符串
     * @return  如果有,则返回一个Properties对象，如果没有,则返回null;
     */
    public Properties getAGroupOfPropTheirKeyStartWith(String skey){
    	if(skey.trim()==null || skey.trim().length() <=0){
    		return prop;
    	}
    	Properties p = new Properties();
    	for(Enumeration e=prop.keys();e.hasMoreElements();){
    		String s = e.nextElement().toString();
    		if(s.startsWith(skey)){
    			p.setProperty(s,prop.getProperty(s));
    		}    		
    	} 	    
    	if(p.size()==0)
    		return null;
    	
    	return p;
    	
    }
    
   // private static PropertyUtil instance = new PropertyUtil(); 
	
//    public static PropertyUtil getProp() { 
//        return instance;
//    }    
       
    /**
     * 设置属性文件的名称
     * @param propertyFileName String 属性文件名
     * @return 无返回值
     * @exception 若文件不存在，产生异常
     */
    public void loadPropertyFile(String propertyFileName) throws Exception {
        fileName = propertyFileName;
        f = new File(fileName);
        String ppath = f.getAbsolutePath();
        if (!f.exists()) {
             throw new Exception("Error: File:\""+ppath+"\" not found!");
        }
        FileInputStream fileis = new FileInputStream(f);
        prop.load(fileis);
        fileis.close();        
    }
    
	/**
     * 读取属性
     * @param propName String 属性名称
     * @return 属性的值
     * @exception 没有异常
     */
	public String getProperty(String propName) {
		return prop.getProperty(propName,"");
	}
	
     /**
     * 设置属性
     * @param propName String 属性名称
     * @param propValue String 属性的值
     * @return 没有返回值
     * @exception 没有异常
     */
	public void setProperty(String propName,String propValue) {
		prop.setProperty(propName,propValue);
	}
	
     /**
     * 保存所有属性到文件中
     * @param propName String 属性名称
     * @param propValue String 属性的值
     * @return 没有返回值
     * @exception 没有异常
     */ 
	public void saveProperty() throws Exception {
		FileOutputStream fileos = new FileOutputStream(f);
		prop.store(fileos,fileHeader);
	
		fileos.close();
	}
	
	
	
	@Override
	public String toString() {
		return prop.toString()+"\tfilePath="+filePath;
	}
	///////////////////////////
	public String getFileHeader() {
		return fileHeader;
	}
	public void setFileHeader(String fileHeader) {
		this.fileHeader = fileHeader;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public Properties getProp() {
		return prop;
	}
	public void setProp(Properties prop) {
		this.prop = prop;
	}
	
	
	
	
}