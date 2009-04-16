/**
 * 
 */
package com.mlp.syslogd;

import java.io.IOException;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mlp.util.Global;

/**
 * 
 * @filename com.mlp.syslogd.SyslogServerThread.java
 * @author Colin Doug
 * @Copyright 2009 secservice.net All Rights Reserved
 * @datetime 2009-3-24
 * @description
 */
public class SyslogServerThread extends Thread implements Serializable {

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7708576253543023808L;
	public SyslogServerThread(SyslogServer server){		
		this.inBuffer = new byte[ IN_BUF_SZ ];
		this.server=server;
//		this.setPriority(Thread.MAX_PRIORITY);
	}
	private byte[]				inBuffer;
	private static final int	IN_BUF_SZ = (8 * 1024);
	DatagramPacket inGram=null;
	SyslogServer server =null;
//	private boolean	 debugPacketsReceived = false;
	private Logger logger=Logger.getLogger(SyslogServerThread.class.getName()) ;
//	private String name="CoreThread"+Math.round(Math.random()*10);
	
//	public void setName(String name){
//		this.name=name;
//	}
	
	@Override
	public void run() {
		
		while (true)
		{
			try{
			this.inGram =
				new DatagramPacket
					( this.inBuffer, this.inBuffer.length );
			server.inSock.receive( this.inGram );
			}
		catch ( IOException ex )
			{
			System.err.println
				( "ERROR reading input socket:\n\t"
					+ ex.getMessage() );
			break;
			}
		SyslogServer.packetCount = SyslogServer.packetCount+1;
//		Charset ch =Charset.forName("UTF-8");
//		String msgBuf =
//			new String( this.inGram.getData(), 0,
//						this.inGram.getLength(), ch);
//
//		if (debugPacketsReceived )
//			System.err.println
//				( "[" + this.inGram.getLength()
//					+ "] {" + msgBuf + "}" );
//
//		++SyslogServer.packetCount;
//		server.displayFeedback
//			( "Packets received: " + SyslogServer.packetCount + "." );
//
//		String hostName =
//			this.inGram.getAddress().getHostName();
//
//		if ( hostName == null )
//			hostName = "localhost";
		
	
		 server.getThreadPool().execute(new HandlerThread("Task"+SyslogServer.packetCount,server,inGram));
			
		}//while
		
		server.displayFeedback
		( "Closing all configuration entries..." );

		server.closeAllActions();

		server.displayFeedback
		( "Shutting down communications..." );

		server.shutdownServices();
		
		
	}
	
	
	

}
