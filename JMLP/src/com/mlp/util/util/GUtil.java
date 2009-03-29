/*GUtil
 * 作者：Colin Doug
 * Version:1.0. 
 * 说明：
 * 
 * 修改：
 */
package com.mlp.util.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mlp.util.i18n.GI18n;

/**
 * @author Colin Doug
 *
 * 作者：Colin Doug
 * Version:1.0. 
 * 说明：
 * 
 * 修改：
 */
public class GUtil {

	
	public GUtil(){
		
	}
	
	public final static String IPADDRESS_REGEX     = "([0-9]|[0-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])\\.([0-9]|[0-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])\\.([0-9]|[0-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])\\.([0-9]|[0-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])";
	public final static String VLAN_REGEX          = "\\d{1,4}";    // 1 to 4 digits number
	public final static String PORT_REGEX          = "\\d{1,5}";    // 1 to 5 digits number
	public final static String INTEGER_REGEX       = "\\d+";        // 1 or more digits
	public final static String ALPHA_NUMERIC_REGEX = "[a-zA-Z0-9_]+"; // 1 or more alpha+numeric+"_" characters
	public final static String HEX_REGEX           = "[0-9a-fA-Fx]+"; // Hex digits + "x" character for "0x" at front

	static Logger logger = Logger.getLogger(GUtil.class.getName());
	public static boolean checkStrIPAddress(String strip){
		if(strip==null || strip.length()<7){
			return false;
		}
		boolean res =false;
		try {
			res= strip.matches(IPADDRESS_REGEX);
		} catch (Exception e) {
			logger.log(Level.SEVERE , "IPADDRESS_REGEX is error!");
			return false;
		}
		
		return res; 
	}
	public static boolean parseBoolean(String s,boolean b){
		boolean r = false;
		try {
			r= Boolean.parseBoolean(s);
		} catch (Exception e) {
			return b;
		}
		return r;
	}
	
 public static  int parseInt(String s,int defaultValue){
	
	 int n =0;
	 try {
		 n=Integer.parseInt(s);
	} catch (Exception e) {
		n=defaultValue;
	}
	return n;
 }
 public static  long parseLong(String s,long defaultValue){
		
	 long n =0;
	 try {
		 n=Long.parseLong(s);
	} catch (Exception e) {
//		n=defaultValue;
		return defaultValue;
	}
	return n;
 }
 public static  double parseDouble(String s,double defaultValue){
		
	 double n =0;
	 try {
		 n=Double.parseDouble(s);
	} catch (Exception e) {
		n=defaultValue;
	}
	return n;
 }
 
 public static String translateIPToLongStr(String input){
	Long n = translateIPToLong(input);
	if(n<0)return null;
	return n.toString();
 }
 /**
  * 
  * @param input
  * @return
  */
 public static long translateIPToLong(String input)
	{
	 if(input==null||input.trim().length()<7 || !input.trim().matches(IPADDRESS_REGEX)){
		  return -1;
	 }
		char[] ipstr = input.trim().toCharArray();
//		System.out.println(ipstr );
//		System.out.println("   "+ipstr.clone().length);
		long ip =0;
		int ip_part = 0;
		
		for( int index = 0; index<ipstr.length;  index++ )
		{
//			System.out.println("index="+index+"  "+ipstr[index]);
			ip_part = ip_part*10 + (ipstr[index] - 48);		
//			System.out.println("ip_part="+ip_part);
			if( (index + 1)<ipstr.length-1 &&( ipstr[index + 1] == '.' || ipstr[index + 1] == '\0')){
				ip <<= 8;
//				System.out.println("ip="+ip);
				ip |= ip_part;
//				System.out.println("ip|="+ip);
				ip_part = 0;
				if( ipstr[index + 1] == '\0' ){
					break;
				}
				else{
					++index;
				}
			}		
		};
		ip <<= 8;
//		System.out.println("ip="+ip);
		ip |= ip_part;
//		System.out.println("ip|="+ip);
		ip_part = 0;

		return ip;
		
	}
 
 public static String translateLongToIP(Long input){
	 if(input<0){
		 return null;
	 }
	 return translateLongStrToIP(input.toString());
 }
 
	public static String translateLongStrToIP(String input)
	{
		if(input==null){
			return null;
		}
		int mask = 0xFF;	
		long ip_uint = Long.parseLong(input.trim());
		
		StringBuilder ipstr = new StringBuilder(16);
		
		ipstr.append( ip_uint>>(3*8) );
		ipstr.append('.');
		ipstr.append( (ip_uint>>(2*8)) & mask );
		ipstr.append('.');
		ipstr.append( (ip_uint>>(1*8)) & mask );
		ipstr.append('.');
		ipstr.append( ip_uint & mask );
		
		return ipstr.toString();
	}
 
 /**
  * 把字符型数字IP转换为字符串IP ，网络
  * "2345678"->"206.202.35.0"
  * @param input
  * @return
  */
 public static String translateNumIPToStr(String input)	{
		
		long ip_uint = Long.parseLong(input);
		return translateNetLongIPToStr(ip_uint);
	}
 
 /**
  * 把数字IP转换为字符串IP，网络
  * 2345678->"206.202.35.0"
  * @param input
  * @return
  */
 public static String translateNetLongIPToStr(long input)	{
		int mask = 0xFF;	
		long ip_uint =input;
	
		StringBuilder ipstr = new StringBuilder(16);
		System.out.println("ip="+ip_uint);
		ipstr.append( ip_uint & mask);// & mask
		System.out.println(ipstr);
		ipstr.append('.');
		ipstr.append( (ip_uint>>(1*8))& mask );
		System.out.println(ipstr);
		ipstr.append('.');
		ipstr.append( (ip_uint>>(2*8)) & mask);
		System.out.println(ipstr);
		ipstr.append('.');
		ipstr.append( ip_uint>>(3*8)& mask);
		System.out.println(ipstr);
		return ipstr.toString();
	}
	
 /**
  * 把字符串IP转换为数字IP，网络
  * "206.202.35.0"->2345678
  * @param input
  * @return
  */
	public static long translateStrIPToNetLong(String input)	{
		int mask = 0xFF;	
		long ip_uint =0;
//		System.out.println(input);
		String ss[] = input.split("\\.");
//		System.out.println("长度="+ss.length);
		String t =ss[3].trim();
		ip_uint=(Long.parseLong(t)& mask);
		ip_uint = ip_uint << 8;
		t =ss[2].trim();
		ip_uint+=(Long.parseLong(t)& mask);
		ip_uint = ip_uint << 8;
		t =ss[1].trim();
		ip_uint+=(Long.parseLong(t)& mask);
		ip_uint = ip_uint << 8;
		t =ss[0].trim();
		ip_uint+=(Long.parseLong(t)& mask);
//		ip_uint = ip_uint << 8;
		
//		System.out.println(ip_uint);
		return ip_uint;
	}
		
	 public static String nextIPaddress(String ip){
	    	String nextip=null;
	    	nextip=GUtil.translateLongToIP(nextIPLong(ip));
	    	return nextip;
	    }
	    
	 public static long nextIPLong(String ip){
	    	
	    	long iplong =GUtil.translateIPToLong(ip);
	    	return	nextIPLong(iplong);
	    	
	    }
	 public static long nextIPLong(long ip){
	    	long iplong =ip;
	    	long maxlong=4294967295L;
	    	if(iplong>=maxlong){
	    		iplong = 0;
	    	}else{
	    		iplong++;
	    	}
	    	return iplong;
	    }
	
	public static SimpleDateFormat dateFormat;
	public static SimpleDateFormat dateFormat2;
	
	/**
	 * yyyy-M-dd HH:mm:ss
	 * @return 
	 */
	public static SimpleDateFormat getDateFormat(){
		if (dateFormat==null) {
			dateFormat = new SimpleDateFormat(FORMAT);
		}
		return dateFormat;
	}
	
	/**
	 * yyyy-M-dd HH:mm:ss.sss
	 * @return 
	 */
	public static SimpleDateFormat getDateFormat2(){
		if (dateFormat==null) {
			dateFormat = new SimpleDateFormat(FORMAT2);
		}
		return dateFormat;
	}
	public static SimpleDateFormat createDateFormat(String format){
		return  new SimpleDateFormat(format);
	}
	private static final String FORMAT="yyyy-M-dd HH:mm:ss";
	private static final String FORMAT2="yyyy-M-dd HH:mm:ss.sss";
	
	public static long tranTimeToLong(String time,String format,long defvalue){
		getDateFormat().applyPattern(format);
		try {
			long n= getDateFormat().parse(time).getTime();
			getDateFormat().applyPattern(FORMAT);
			return n;
		} catch (ParseException e) {
			getDateFormat().applyPattern(FORMAT);
			e.printStackTrace();
			return defvalue;
		}
	}
	
	/**
	 * 把型如"yyyy-M-dd HH:mm:ss"的字符串时间解析成long型值
	 * @param time
	 * @param defvalue 设置的解析失败返回的默认值
	 * @return
	 */
	public static long tranTimeToLong(String time,long defvalue){
		
		try {
			return getDateFormat().parse(time).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
			return defvalue;
		}
	}
	
	public static String formatTime(Timestamp t) {
		//
		String res = null;
		if (t != null) {
			res = getDateFormat().format(t);
		} else {
			return null;
		}
		return res;
	}
	
	public static Date parserTimeStrToDate(String time,String reg){
		String s = getDateFormat().toPattern();
		if(reg==null || reg.length()<1){
			reg="yyyy-MM-dd HH:mm:ss.sss";
		}
		getDateFormat().applyPattern(reg);//"yyyy-MM-dd HH:mm:ss.sss"
			
			Date d =null;
		try {
			 d = getDateFormat().parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		getDateFormat().applyPattern(s);
		return d;
	}
	
	public static long parserTimeStrToLong(String time,String reg){
		
		return parserTimeStrToDate(time,reg).getTime();
	}

	public static FileOutputStream getFileOutputStream(String filePath) {
		
		File f = new File(filePath);
		if(f.isDirectory() || !f.exists()){
			return null;
		}
		FileOutputStream fout;
		try {
			fout = new FileOutputStream(f);
		} catch (FileNotFoundException e) {
		 
			e.printStackTrace();
			return null;
		}
		
		return fout;
	}
	public static FileOutputStream getFileOutputStream(File f){
		
		FileOutputStream fout=null;
		try {
			fout = new FileOutputStream(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		return fout;
	}
	
	public static FileInputStream getFileInputStream(String filePath) {
		
		File f = new File(filePath);
		if(f.isDirectory() || !f.exists()){
			return null;
		}
		FileInputStream fin;
		try {
			fin = new FileInputStream(f);
		} catch (FileNotFoundException e) {
		 
			e.printStackTrace();
			return null;
		}
		
		return fin;
	}
	public static FileInputStream getFileInputStream(File f){
		FileInputStream fin=null;
		try {
			fin = new FileInputStream(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		return fin;
//		BufferedOutputStream bout=new BufferedOutputStream(outs,20480);
//		DataOutputStream dout =new DataOutputStream(bout);
	}
	
	/**
	 * 产生0到max之间的随机数
	 * @param max
	 * @return
	 */
	public static long random(long max){
		double r =Math.random();
		r = r*max;	
		return Math.round(r);
	}
	
	
	 public static String byteToUString(byte abyte0[], int i)
	    {
	        StringBuffer stringbuffer = new StringBuffer();
//	        int j = 0;
//	        byte byte0 = 20;
	        if(abyte0.length < i)
	            i = abyte0.length;
	        for(int k = 0; k < i; k++)
	        {
//	            if(j++ > 19){
//	                j = 1;
//	                stringbuffer.append("\n");
//	            }
	        	int n= byteToInt(abyte0[k]);
//	            String s = Integer.toString(n);
//	            if(s.length() < 2)
//	                s = "0" + s;
//	            if (s.compareToIgnoreCase("00") != 0) {
				char ss = (char) ((0x00 << 8) | n);
				stringbuffer.append(ss);
//	            }
			
	        }

	        return stringbuffer.toString();
	    }

	 public static int byteToInt(byte byte0)
	    {
	        return byte0 & 0xff;
	    }
	
	 /////////
	 //国际化支持
	private static GI18n sani18n = new GI18n();
	public synchronized static GI18n getGI18n(){
		if(sani18n==null){
			sani18n =new GI18n();
		}
		return sani18n;
	}
	public static String getString(String key){
		return getGI18n().getString(key);
	}
	
	public  synchronized static List<String> splitToList(String s,String denotation){
		
		if(s!=null && s.length()>0){
			List<String> list = new ArrayList<String>();
			String ss[] =s.split(denotation);
			for (int i = 0; i < ss.length; i++) {
				String str = ss[i].trim();
				list.add(str);
			}
			return list;
		}
		return null;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
