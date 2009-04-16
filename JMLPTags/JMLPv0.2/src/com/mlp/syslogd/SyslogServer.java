
package com.mlp.syslogd;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.jppf.client.JPPFClient;
import org.jppf.server.protocol.JPPFTask;

import com.mlp.syslog.SyslogDefs;
/**
 * 
 *
 * @version $Revision: 1.1.1.1 $
 * @author dengch
 *   <a href="mailto:colindoug09@gmail.com">colindoug09@gmail.com</a>.
 **/

public class SyslogServer extends Thread{
	public static final String		RCS_ID = "$Id: SyslogServer.java,v 1.1.1.1 1998/02/22 05:47:54 time Exp $";
	public static final String		RCS_REV = "$Revision: 1.1.1.1 $";
	public static final String		RCS_NAME = "$Name:  $";

	private static final int	IN_BUF_SZ = (8 * 1024);
	private static final int	SYSLOG_PORT = 514;

	private boolean				socketOpen;

	private int					port;

	protected DatagramSocket		inSock;
	private DatagramPacket		inGram;

	private byte[]				inBuffer;
	
	protected boolean				debugPacketsReceived;
	private boolean				debugMessagesReceived;

	private SyslogConfig		configuration;
	protected ConfigEntryVector	configEntries;

	private FeedbackDisplayer	feedback;

	private Logger logger=Logger.getLogger(SyslogServer.class.getName()) ;

	public 	SyslogServer()
		{
		this( SyslogDefs.DEFAULT_PORT );
		}

	public 	SyslogServer( int port )
		{
		super();

		this.port = port;
		this.socketOpen = false;
		this.inBuffer = null;
		this.inGram = null;

		this.debugPacketsReceived = false;
		this.debugMessagesReceived = false;

		this.feedback = null;
		this.configuration = null;
		this.configEntries = null;
		}

	public SyslogConfig getConfiguration()
		{
		return this.configuration;
		}

	public void setConfiguration( SyslogConfig configuration )
		{
		this.configuration = configuration;
		this.configEntries = configuration.getConfigEntries();
		}

	public void startupServices()
		{
		try {
			this.inSock =
				new DatagramSocket( this.port );
			
			this.inBuffer =
				new byte[ SyslogServer.IN_BUF_SZ ];

			this.socketOpen = true;
			this.setPriority(Thread.MAX_PRIORITY);
			}
		catch ( SocketException ex )
			{
			System.err.println
				( "FATAL could not create input socket on port '"
					+ this.port + "'\n\t" + ex.getMessage() );
			this.stop();
			}
		}

	public void	shutdownServices()
		{
		if ( this.socketOpen )
			{
			this.inSock.close();
			this.socketOpen = false;
			getClient().close();
			}
		}

	public void	finalize()
		{
		this.shutdownServices();
		this.closeAllActions();
		}

	public void openAllActions()
		{
		for ( int eIdx = 0
				; eIdx < this.configEntries.size()
					; ++eIdx )
			{
			ConfigEntry entry =
				this.configEntries.entryAt( eIdx );

			if ( entry != null )
				{
				entry.openAction();
				}
			}
		}

	public void closeAllActions()
		{
		if(configEntries!=null){
		for ( int eIdx = 0
				; eIdx < this.configEntries.size()
					; ++eIdx )
			{
			ConfigEntry entry =
				this.configEntries.entryAt( eIdx );

			if ( entry != null )
				{
				entry.closeAction();
				}
			}
		}
		}

	public void registerActionDisplay( String name, SyslogDisplayInterface display )
		{
		for ( int eIdx = 0
				; eIdx < this.configEntries.size()
					; ++eIdx )
			{
			ConfigEntry entry =
				this.configEntries.entryAt( eIdx );

			if ( entry != null )
				{
				entry.registerActionDisplay( name, display );
				}
			}
		}

	public void setFeedbackDisplayer( FeedbackDisplayer feedback )
	{
		this.feedback = feedback;
	}

	public void displayFeedback( String message )
	{
		if ( this.feedback != null )
			{
			this.feedback.displayFeedback( message );
			}
	}
	private List<SyslogMessage> bufList = new ArrayList<SyslogMessage>();
	private List<JPPFTask> tasks = new ArrayList<JPPFTask>();
	
	public List<JPPFTask> getTasks(){
		if(tasks==null){
			tasks = new ArrayList<JPPFTask>();
		}
		return tasks;
	}
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
	
	private int availableProcessors =0;
	
	public int getAvailableProcessors(){
		if(availableProcessors<=0){
		return Runtime.getRuntime().availableProcessors();
		}
		return availableProcessors;
	}
	
	private ThreadPoolExecutor pool = null;
	private ArrayBlockingQueue<Runnable> queue= null;
	public ArrayBlockingQueue<Runnable> getQueue(){
		if(queue==null && getAvailableProcessors()<=4){
			 queue= new ArrayBlockingQueue<Runnable>(1000000);
		}else if(queue==null ){
			queue= new ArrayBlockingQueue<Runnable>(2000000);
		}
		return queue;
	}
	public ThreadPoolExecutor getThreadPool(){
		if(pool==null){
			if(getAvailableProcessors()<=2){
				pool = new ThreadPoolExecutor(4, 8, 2000,TimeUnit.MILLISECONDS, getQueue(),new ThreadPoolExecutor.DiscardOldestPolicy());
			}else if(getAvailableProcessors()>4){
				pool = new ThreadPoolExecutor(getAvailableProcessors(), getAvailableProcessors()*2, 1,TimeUnit.SECONDS, getQueue(),new ThreadPoolExecutor.DiscardOldestPolicy());
			}
//			pool.getThreadFactory().
			
			System.out.println("AvailableProcessors="+getAvailableProcessors()+" CorePoolSize="+pool.getCorePoolSize());
		}
		return pool;
	}
	public   void execute(Runnable thread){
		getThreadPool().execute(thread);
	}
	public static long packetCount=0;
	public void run()
	{
		packetCount = 0;

		this.displayFeedback
			( "Establishing communications..." );

		this.startupServices();

		this.displayFeedback
			( "Opening all configuration entries..." );

		this.openAllActions();

		this.displayFeedback
			( "Listening for incoming packets..." );

//		boolean ff=false;
//		long timef=System.currentTimeMillis();
//		long timen=System.currentTimeMillis();
		getThreadPool();
		SyslogServerThread sst =  new SyslogServerThread(this);
		sst.setPriority(Thread.MAX_PRIORITY);
		sst.start();

		
//		while (true)
//			{
//			try {
//				this.inGram =
//					new DatagramPacket
//						( this.inBuffer, this.inBuffer.length );
//				
//				this.inSock.receive( this.inGram );
//				}
//			catch ( IOException ex )
//				{
//				System.err.println
//					( "ERROR reading input socket:\n\t"
//						+ ex.getMessage() );
//				break;
//				}
//
//			Charset ch =Charset.forName("UTF-8");
//			String msgBuf =
//				new String( this.inGram.getData(), 0,
//							this.inGram.getLength(), ch);
//
//			if ( this.debugPacketsReceived )
//				System.err.println
//					( "[" + this.inGram.getLength()
//						+ "] {" + msgBuf + "}" );
//
//			++packetCount;
//			this.displayFeedback
//				( "Packets received: " + packetCount + "." );
//
//			String hostName =
//				this.inGram.getAddress().getHostName();
//
//			if ( hostName == null )
//				hostName = "localhost";
//
////			this.processMessage( msgBuf, hostName );
//			if(Global.isDebug && packetCount%10==0)
//			logger.log(Level.INFO,"Received syslogs:"+packetCount);
//			
//			getThreadPool().execute(new HandlerThread(this.configEntries,msgBuf, hostName));
//			if(Global.isDebug && packetCount%1000==0){
//				logger.log( Level.INFO,"Queue().size="+getQueue().size()
//						+" TaskCount()="+getThreadPool().getTaskCount()
//						+" ActiveCount="+getThreadPool().getActiveCount()
//						+" LargestPoolSize="+getThreadPool().getLargestPoolSize()
//						+" CompletedTaskCount="+getThreadPool().getCompletedTaskCount());
//			}
//			/*
//			getTasks().add(new Handler(msgBuf, hostName));
//			   try
//			   {
//			     // execute the tasks
//				  int size = getTasks().size();
//				  timen=System.currentTimeMillis();
//				 if(size>0 && timen-timef>=3000){
//					 timef = timen;
//			     List<JPPFTask> results = getClient().submit(getTasks(), null);
//			     
//			     for (Iterator iterator = results.iterator(); iterator.hasNext();) {
//					JPPFTask task = (JPPFTask) iterator.next();
//					  if  (null == task.getException())
//					     {
//					       Object result = task.getResult();
//					       SyslogMessage logMessage=null;
//					       // use the result ...
//					       if(result==null){
//					    	   continue;
//					       }
//					       logMessage =( SyslogMessage)result;
//					       for ( int eIdx = 0 ; eIdx < this.configEntries.size() ; ++eIdx )
//						 	 {
//						 		ConfigEntry entry =
//						 			this.configEntries.entryAt( eIdx );
//						 		entry.processMessage( logMessage );
//						 	}
//					     }
//					     else
//					     {
//					       Exception ex = task.getException();
//					       ex.printStackTrace();
//					       // handle the exception ...
//					     }
//				}
//			     getTasks().clear();
//			    }
//			    
//			   }
//			   catch (Exception e)
//			   {
//			     e.printStackTrace();
//			   }
//			   */
//			
//			}//while
//
//		this.displayFeedback
//			( "Closing all configuration entries..." );
//
//		this.closeAllActions();
//
//		this.displayFeedback
//			( "Shutting down communications..." );
//
//		this.shutdownServices();
		}

	public void processMessage( String message, String hostName )
		{
		int lbIdx = message.indexOf( '<' );
		int rbIdx = message.indexOf( '>' );

		if ( lbIdx < 0 || rbIdx < 0 
				|| lbIdx >= (rbIdx - 1) )
			{
			System.err.println
				( "BAD MSG {" + message + "}" );
			return;
			}
		
		int priCode = 0;
		String priStr =
			message.substring( lbIdx + 1, rbIdx );

		try { priCode = Integer.parseInt( priStr ); }
		catch ( NumberFormatException ex )
			{
			System.err.println
				( "ERROR Bad priority code '" + priStr + "'" );
			return;
			}

		int facility = SyslogDefs.extractFacility( priCode );
		int priority = SyslogDefs.extractPriority( priCode );

		message =
			message.substring
				( rbIdx + 1, (message.length() - 1) );

		//
		// Check to see if msg looks non-standard.
		// In this case, it means that there is not a standard
		// date in the front of the message text.
		//
		boolean stdMsg = true;

		if ( message.length() < 16 )
			{
			stdMsg = false;
			}
		else if (	   message.charAt(3)	!= ' '
					|| message.charAt(6)	!= ' '
					|| message.charAt(9)	!= ':'
					|| message.charAt(12)	!= ':'
					|| message.charAt(15)	!= ' ' )
			{
			stdMsg = false;
			}

		String timestamp;

		if ( ! stdMsg )
			{
			try {
				timestamp =
					TimestampFormat.getInstance().format
						( new Date() );
				}
			catch ( IllegalArgumentException ex )
				{
				System.err.println( "ERROR INTERNAL DATE ERROR!" );
				timestamp = "";
				}
			}
		else
			{
			timestamp = message.substring( 0, 15 );
			message = message.substring( 16 );
			}

		lbIdx = message.indexOf( '[' );
		rbIdx = message.indexOf( ']' );
		int colonIdx = message.indexOf( ':' );
		int spaceIdx = message.indexOf( ' ' );
		
		int		processId = 0;
		String	processName = "";
		String	processIdStr = "";

		if ( lbIdx < (rbIdx - 1)
				&& colonIdx == (rbIdx + 1)
				&& spaceIdx == (colonIdx + 1) )
			{
			processName = message.substring( 0, lbIdx );
			processIdStr = message.substring( lbIdx + 1, rbIdx );
			message = message.substring( colonIdx + 2 );

			try { processId = Integer.parseInt( processIdStr ); }
			catch ( NumberFormatException ex )
				{
				System.err.println
					( "ERROR Bad process id '" + processIdStr + "'" );
				processId = 0;
				}
			}
		else if ( lbIdx < 0 && rbIdx < 0
					&& colonIdx > 0 && spaceIdx == (colonIdx + 1) )
			{
			processName = message.substring( 0, colonIdx );
			message = message.substring( colonIdx + 2 );
			}

		if ( this.debugMessagesReceived )
			System.err.println
				( "[" + facility + ":" + SyslogDefs.getFacilityName(facility)
					+ "] ["
					+ priority + ":" + SyslogDefs.getPriorityName(priority)
					+ "] '"
					+ processName + "' '" + processId + "' "
					+ timestamp + " " + message );

		SyslogMessage logMessage =
			new SyslogMessage(
				facility, priority, timestamp, hostName,
					processName, processId, message );

		for ( int eIdx = 0 ; eIdx < this.configEntries.size() ; ++eIdx )
			{
			ConfigEntry entry =
				this.configEntries.entryAt( eIdx );

			entry.processMessage( logMessage );
			}
		}
	
	}

