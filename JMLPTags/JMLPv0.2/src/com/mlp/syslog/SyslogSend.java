/**
 * 
 */
package com.mlp.syslog;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.jppf.client.JPPFClient;
import org.jppf.server.protocol.JPPFTask;

import com.mlp.syslogd.Handler;
import com.mlp.syslogd.SyslogMessage;
import com.mlp.util.util.GUtil;

/**
 * @author Colin Doug
 *
 */
public class SyslogSend extends Thread {

	public static long max = 1000;
	public static int logspsec=100;
	public static String host="localhost";////192.168.30.3
	public static long count=0;
	public static boolean usejppf=false;
	public static  int port= SyslogDefs.DEFAULT_PORT;
	/**
	 * @param args
	 */
	public static void main(String[] argv) {
		try {
//			Charset charset = Charset.forName("UTF-8");
//			String s=new String("事件".getBytes(charset),charset);
//			System.out.println("事件.len="+"事件".length());
//			System.out.println("s.len="+s.length());
		
			
			for ( int iArg = 0 ; iArg < argv.length ; ++iArg )
			{
			if ( argv[iArg].equals( "-host" ) )
			{
				host =argv[++iArg];;
			}
			else if ( argv[iArg].equals( "-port" )
						&& (iArg + 1) < argv.length )
			{
				 port = GUtil.parseInt(argv[++iArg], SyslogDefs.DEFAULT_PORT);
			}else if ( argv[iArg].equals( "-max" )
						&& (iArg + 1) < argv.length )
			{
				 max = GUtil.parseLong(argv[++iArg], 1000);
			}else if ( argv[iArg].equals( "-jppf" )
						&& (iArg + 1) < argv.length )
			{
				 usejppf = GUtil.parseBoolean(argv[++iArg],false);
			}else if ( argv[iArg].equals( "-n" )
						&& (iArg + 1) < argv.length )
			{
				logspsec = GUtil.parseInt(argv[++iArg], 1000);
			}
				else if ( argv[iArg].equals( "-?" )
						|| argv[iArg].equals( "-usage" ) )
			{
				printUsage();
				return;
			}
			else
				{
				System.err.println
					( "ERROR unknown option '" + argv[iArg] + "'" );
				printUsage();
				return;
				}
			}
			boolean flag = false;//true表示不能整除,需要多开一个
			if(0!=logspsec%1000){
				flag=true;
			}
			int x = logspsec/1000 ;//每个线程的最大速度是1000条/秒  以此确定要多少个线程
			if(flag){
				x++;
			}
			long pmax=max/x;//每个线程得到相同多的任务,不能整除的情况下最后一个任务会轻
			
			SyslogSend ss = new SyslogSend();
			
			if(usejppf){//使用JPPF 
			for (int i = 0; i < x; i++) {
				String name="Thread0"+String.valueOf(i);
				long maxt = pmax;
				long logspsect= 1000;
				if(i==(x-1)){
					maxt = max-i*pmax;
					logspsect= logspsec-1000*i;
				}
				ss.getTasks().add(new SendTask(name,host,port,maxt,logspsect));
				System.out.println("CreatedTask"+name+" host="+host+" max="+maxt+"  speed="+logspsect+"logs/s");
			}
			List<JPPFTask> results = ss.getClient().submit(ss.getTasks(), null);
			
			   for (Iterator iterator = results.iterator(); iterator.hasNext();) {
					JPPFTask task = (JPPFTask) iterator.next();
					  if  (null == task.getException())
					     {
						  Object result = task.getResult();
						  System.out.println(result);
						  
					     }
			   }
			   ss.getClient().close();
			}else{//不使用JPPF
				ThreadPoolExecutor pool = new ThreadPoolExecutor(x, x+2, 2,TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(x*2),new ThreadPoolExecutor.DiscardOldestPolicy());
				
				for (int i = 0; i < x; i++) {
					String name="Th0"+String.valueOf(i);
					long maxt = pmax;
					long logspsect= 1000;
					if(i==(x-1)){
						maxt = max-i*pmax;
						logspsect= logspsec-1000*i;
					}
					pool.execute(new SendTask(name,host,port,maxt,logspsect));
					System.out.println("CreatedTask"+name+" host="+host+" max="+maxt+" speed="+logspsect+"logs/s");
				}
//				SendTask st = new SendTask("t01",host,max,logspsec);
//				System.out.println("创建了任务"+"t01"+" host="+host+" max="+max+"条 speed="+logspsec+"条/秒");
//				st.run();
				
			}
			
		}		
		catch ( Exception ex )
		{
			ex.printStackTrace();
			System.err.println
				( "FATAL creating Syslog instance: '"
					+  ex.getMessage() + "'" );
		}
		
	}
	
	private List<JPPFTask> tasks = new ArrayList<JPPFTask>();
	
	public  List<JPPFTask> getTasks(){
		if(tasks==null){
			tasks = new ArrayList<JPPFTask>();
		}
		return tasks;
	}
	private List<SyslogMessage> bufList = new ArrayList<SyslogMessage>();
	public List<SyslogMessage>  getBufList(){
		if(bufList==null){
			bufList = new ArrayList<SyslogMessage>();
		}
		return bufList;
	}
	private JPPFClient client = null;
	public JPPFClient getClient(){
		if(client==null){
			client = new JPPFClient();
		}
		return client;
	}
	
	
	public void run() {
		
		
		
		
		
		
		
//		try{
//		Syslog	syslog = new Syslog( "192.168.1.100", SyslogDefs.LOG_ALERT );
//		String msg = "发现ColinDoug越权访问";
//		InetAddress addr = InetAddress.getByName( host );
//		Thread.sleep(3000);
//		long stime=System.currentTimeMillis();
//		long etime=0;
//		System.out.println("开始:"+(new SimpleDateFormat("HH:mm:ss.sss").format(new Date(stime))));
//		
//		for (int i = 0; i < max; i++) {
//			if(logspsec<1000){
//				Thread.sleep(1000/logspsec);
//			}
//			syslog.syslog(addr, SyslogDefs.LOG_SYSLOG, SyslogDefs.LOG_ALERT,msg+String.valueOf(i) );
//			count++;
//		}
//		etime= System.currentTimeMillis();
//		System.out.println("结束:"+(new SimpleDateFormat("HH:mm:ss.sss").format(new Date(System.currentTimeMillis())))+"  总条数:"+count+" 用时:"+(etime-stime)/1000+"秒");
//	
//		}catch (Exception e) {
//			e.printStackTrace();
//		}
	}
	
	
	public static void 	printUsage()
	{
	System.err.println( "usage: SyslogSend [options...]" );
	System.err.println( "options:" );
	System.err.println
		( "   -host  ip     -- "
			+ "Syslog server IP address,default is localhost" );
	System.err.println
		( "   -max max      -- "
			+ "sets max syslogs to send,default is 1000" );
	System.err.println
		( "   -n n    -- "
			+ "sets send n syslogs per seconds,default is 100 " );
	System.err.println
	( "   -jppf true    -- "
		+ "sets if use jppf mode,default is false " );
	
	}

	
}
