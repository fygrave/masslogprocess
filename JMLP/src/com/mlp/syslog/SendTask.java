/**
 * 
 */
package com.mlp.syslog;

import java.io.Serializable;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jppf.server.protocol.JPPFTask;

/**
 * @filename SendTask.java
 * @author ColinDoug
 * @datetime 2009-3-20
 * @description
 */
public class SendTask  extends JPPFTask implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3533322504376088478L;
	
	public SendTask(String name,String host,int port,long max,long logspsec){
		this.name=name;
		this.max=max;
		this.logspsec = logspsec;
		this.host=host;
		this.port= port;
		
	}
	public SendTask(){
		
	}
	String name="Thread"+String.valueOf(Math.round(Math.random()*100000));
	String host ="localhost";
	int port =SyslogDefs.DEFAULT_PORT;;
	long max=0;
	long logspsec =1000;
	public static long total=0;
	 public void run(){   
			try{
				if(logspsec>1000){
					logspsec=1000;
				}
				long count=0;
				Syslog	syslog = new Syslog( "192.168.1."+String.valueOf(Math.round(Math.random()*10)), SyslogDefs.LOG_ALERT );
				syslog.setPort(port);
				String msg = name+String.valueOf(Math.round(Math.random()*1000000000));
				InetAddress addr = InetAddress.getByName( host );
				Thread.sleep(2000);
				long stime=System.currentTimeMillis();
				long etime=0;
				System.out.println(name+"Start:"+(new SimpleDateFormat("HH:mm:ss.sss").format(new Date(stime))));
				
				for (int i = 0; i < max; i++) {
					Thread.sleep(1000/logspsec);
					syslog.syslog(addr, SyslogDefs.LOG_SYSLOG, SyslogDefs.LOG_ALERT,msg+"发现"+total+"ColinDoug越权访问"+total+String.valueOf(i) );
					total=total+1;
					count++;
				}
				etime= System.currentTimeMillis();
				System.out.println(name+"End:"+(new SimpleDateFormat("HH:mm:ss.sss").format(new Date(System.currentTimeMillis())))+"  SendTotalCount:"+count+" SpendTime:"+(etime-stime)/1000+"seconds");
			
				}catch (Exception e) {
					e.printStackTrace();
				}
		
		 
	 }

	/**
	 * 
	 */
	public  void send() {
	
	}
	 


	 
	 
	

}
