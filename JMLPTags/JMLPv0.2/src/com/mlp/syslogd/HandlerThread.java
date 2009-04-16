package com.mlp.syslogd;

import java.io.Serializable;
import java.net.DatagramPacket;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mlp.syslog.SyslogDefs;
import com.mlp.util.Global;
import com.mlp.util.util.GUtil;
/**
 * @filename HandlerThread.java
 * @author ColinDoug
 * @Copyright 2009 ColinDoug
 * @datetime 2009-3-20
 * @description
 */
public class HandlerThread  implements Runnable,Serializable{

	 /**
	 * 
	 */
	private static final long serialVersionUID = -5734501708995798285L;
	private Logger logger= Logger.getLogger(HandlerThread.class.getName());
//		private String msgBuf;
//	    private String hostName;
//	    private    ConfigEntryVector	configEntries;
	    private DatagramPacket inGram=null;
	    private SyslogServer server=null;
	    String taskname= null;
	    public HandlerThread(String taskname,SyslogServer server,DatagramPacket inGram){    
//	    	this.msgBuf=msgBuf;
//	    	this.hostName= hostName;
//	    	this.configEntries=configEntries;   
	    	this.server=server;
	    	this.inGram=inGram;
	    	this.taskname= taskname;
//	        this.debugMessagesReceived= debugMessagesReceived;
	    }  
	
     private Charset ch =Charset.forName("UTF-8");
     public String getTaskName(){
    	 if(taskname==null){
    		 taskname = GUtil.getDateFormat2().format(new Date())+ Math.round(Math.random()*100000000);
    	 }
    	 return taskname;
     }
    
	public void run() {
//		logger.log(Level.INFO, " inGram start");
		if(Global.isDebug)
		System.out.println(getTaskName()+" start execute");
		String msgBuf =
			new String( this.inGram.getData(), 0,
						this.inGram.getLength(), ch);

		if (server.debugPacketsReceived )
			System.err.println
				( "[" + this.inGram.getLength()
					+ "] {" + msgBuf + "}" );
		
		server.displayFeedback("Packets received: " + SyslogServer.packetCount
				+ ".");

		String hostName = this.inGram.getAddress().getHostAddress();// if cann't get host name will impact the speed,so use getHostAddress instead of getHostName();

		if ( hostName == null )
			hostName = "localhost";
		SyslogMessage logMessage = processMessage(msgBuf,hostName);
	//	logger.log(Level.INFO, " for configEntries start");
	    for ( int eIdx = 0 ; eIdx < server.configEntries.size() ; ++eIdx )
	 	 {
	 		ConfigEntry entry =	server.configEntries.entryAt( eIdx );
	 		entry.processMessage( logMessage );
	 	}
//	    logger.log(Level.INFO," for configEntries end");
	}
	
	  public SyslogMessage processMessage( String message, String hostName )
		{
		int lbIdx = message.indexOf( '<' );
		int rbIdx = message.indexOf( '>' );

		if ( lbIdx < 0 || rbIdx < 0 
				|| lbIdx >= (rbIdx - 1) )
			{
//			System.err.println
//				( "BAD MSG {" + message + "}" );
			return null;
			}
		
		int priCode = 0;
		String priStr =
			message.substring( lbIdx + 1, rbIdx );

		try { priCode = Integer.parseInt( priStr ); }
		catch ( NumberFormatException ex )
			{
//			System.err.println( "ERROR Bad priority code '" + priStr + "'" );
			return null;
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

//		if ( this.debugMessagesReceived )
//			System.err.println
//				( "[" + facility + ":" + SyslogDefs.getFacilityName(facility)
//					+ "] ["
//					+ priority + ":" + SyslogDefs.getPriorityName(priority)
//					+ "] '"
//					+ processName + "' '" + processId + "' "
//					+ timestamp + " " + message );

		SyslogMessage logMessage =
			new SyslogMessage(
				facility, priority, timestamp, hostName,
					processName, processId, message );
		return logMessage;

		
		}

}
