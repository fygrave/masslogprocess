/**
 * 
 */
package com.mlp.syslogd;

import java.util.Date;

import org.jppf.server.protocol.JPPFTask;

import com.mlp.syslog.SyslogDefs;

/**
 * @filename Handler.java
 * @author ColinDoug
 * @Copyright secservice.net
 * @datetime 2009-3-16 
 * @description
 */
public class Handler extends  JPPFTask{    
    /**
	 * 
	 */
	private static final long serialVersionUID = -21859349298602218L;
//	private Socket socket;    
//    public Handler(Socket socket){    
//        this.socket=socket;    
//    }    
    private String msgBuf;
    private String hostName;
    public Handler(String msgBuf, String hostName){    
    	this.msgBuf=msgBuf;
    	this.hostName= hostName;
//    	this.configEntries=configEntries;   
//        this.debugMessagesReceived= debugMessagesReceived;
    }    
//	private ConfigEntryVector	configEntries;
//	private boolean				debugMessagesReceived;
   
    public void run(){    
//        try {    
        	
        	setResult(processMessage(msgBuf, hostName));

//            BufferedReader br=getReader(socket);    
//            PrintWriter pw=getWriter(socket);    
//            String msg=null;    
//            while((msg=br.readLine())!=null){    
//                System.out.println(msg);    
//                pw.println(echo(msg));    
//                if(msg.equals("bye"))    
//                    break;    
//            }    
//        } catch (IOException e) {    
//            e.printStackTrace();    
//        }finally{    
//            try {    
//                if(socket!=null)    
//                    socket.close();    
//            } catch (IOException e) {    
//                e.printStackTrace();    
//            }    
//        }    
    } 
    
    public SyslogMessage processMessage( String message, String hostName )
	{
	int lbIdx = message.indexOf( '<' );
	int rbIdx = message.indexOf( '>' );

	if ( lbIdx < 0 || rbIdx < 0 
			|| lbIdx >= (rbIdx - 1) )
		{
//		System.err.println
//			( "BAD MSG {" + message + "}" );
		return null;
		}
	
	int priCode = 0;
	String priStr =
		message.substring( lbIdx + 1, rbIdx );

	try { priCode = Integer.parseInt( priStr ); }
	catch ( NumberFormatException ex )
		{
//		System.err.println( "ERROR Bad priority code '" + priStr + "'" );
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

//	if ( this.debugMessagesReceived )
//		System.err.println
//			( "[" + facility + ":" + SyslogDefs.getFacilityName(facility)
//				+ "] ["
//				+ priority + ":" + SyslogDefs.getPriorityName(priority)
//				+ "] '"
//				+ processName + "' '" + processId + "' "
//				+ timestamp + " " + message );

	SyslogMessage logMessage =
		new SyslogMessage(
			facility, priority, timestamp, hostName,
				processName, processId, message );
	return logMessage;

	
	}

    
}   

