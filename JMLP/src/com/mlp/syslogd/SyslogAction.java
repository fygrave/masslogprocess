

package com.mlp.syslogd;

import com.mlp.net.SimpleSMTP;
import com.mlp.syslog.SyslogDefs;
import com.mlp.util.ClassUtilities;
import com.mlp.util.StringUtilities;


import java.io.*;
import java.net.*;
import java.util.*;


public class
SyslogAction
		implements SyslogActionInterface,Serializable
	{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4243270214043224601L;
	public static final String		RCS_ID = "$Id: SyslogAction.java,v 1.1.1.1 1998/02/22 05:47:54 time Exp $";
	public static final String		RCS_REV = "$Revision: 1.1.1.1 $";
	public static final String		RCS_NAME = "$Name:  $";

	// TYPES
	public static final int			FILE = 1;
	public static final int			DISPLAY = 2;
	public static final int			FORWARD = 3;
	public static final int			SOUND = 4;
	public static final int			SYSTEM = 5;
	public static final int			CLASS = 6;
	public static final int			EMAIL = 7;

	private int				type;
	private boolean			isOpen;
	private boolean			useThread;
	private String[]		parameters;

	private PrintWriter		writer;

	private int				forwardPort;
	private InetAddress		forwardAddr;
	private DatagramSocket	forwardSocket;

	private SyslogActionInterface	action;
	private SyslogDisplayInterface	display;


	public
	SyslogAction( String typeStr, String[] parameters )
		{
		this( SyslogAction.getActionType( typeStr ), parameters );
		}

	public
	SyslogAction( int type, String[] parameters )
		{
		this.type = type;
		this.isOpen = false;
		this.useThread = false;
		this.parameters = parameters;

		this.writer = null;
		this.forwardAddr = null;
		}

	public boolean
	isOpen()
		{
		return this.isOpen;
		}

	public boolean
	isThreaded()
		{
		return this.useThread;
		}

	public void
	setParameters( String[] parameters )
		{
		this.parameters = parameters;
		}

	public void
	openAction()
		{
		this.isOpen = true;

		switch ( this.type )
			{
			case SyslogAction.FILE:
				this.useThread = false;
				try {
					this.writer =
						new PrintWriter(
							new FileWriter(
								this.parameters[0], true ) );
					}
				catch ( IOException ex )
					{
					this.isOpen = false;
					this.writer = null;
					System.err.println
						( "ERROR opening file writer for '"
							+ this.parameters[0] + "'\n\t"
							+ ex.getMessage() );
					}
				break;

			case SyslogAction.FORWARD:
				this.useThread = false;
				this.openForwardAction();
				break;
			
			case SyslogAction.EMAIL:
				this.useThread = true;
				break;

			case SyslogAction.CLASS:
				this.useThread = true;
				this.openClassAction();
				break;

			case SyslogAction.SYSTEM:
				this.useThread = true;
				if ( false )
				for ( int i = 0 ; i < this.parameters.length ; ++i )
					System.err.println
						( "SYSTEM argv[" + i + "] '" +
							this.parameters[i] + "'" );
				break;

			case SyslogAction.SOUND:
				this.useThread = true;
				SyslogMedia.loadAudioClip
					( this.parameters[0], this.parameters[1] );
				break;
			}
		}

	public void
	closeAction()
		{
		this.isOpen = false;
		
		switch ( this.type )
			{
			case SyslogAction.FILE:
				if ( this.writer != null )
					{
					this.writer.close();
					this.writer = null;
					}
				break;

			case SyslogAction.FORWARD:
				this.isOpen = false;
				if ( this.forwardSocket != null )
					{
					this.forwardSocket.close();
					this.forwardSocket = null;
					}
				break;

			case SyslogAction.CLASS:
				this.isOpen = false;
				this.closeClassAction();
				break;
			}
		}

	public void
	registerActionDisplay( String name, SyslogDisplayInterface display )
		{
		if ( this.type == SyslogAction.DISPLAY )
			{
			if ( name.equalsIgnoreCase( this.parameters[0] ) )
				{
				this.display = display;
				}
			}
		}

	public void
	finalize()
		{
		this.closeAction();
		}

	public void
	restart()
		{
		}

	public void
	processMessage( SyslogMessage logMsg )
		{
		if ( ! this.isOpen )
			return;

		switch ( this.type )
			{
			case SyslogAction.FILE:
				if ( this.writer != null )
					{
					this.writer.print( logMsg.timestamp );
					this.writer.print( " " );
					this.writer.print( logMsg.hostName );
					this.writer.print( " " );
					this.writer.print( logMsg.processName );
					this.writer.print( "[" );
					this.writer.print( logMsg.processId );
					this.writer.print( "]: " );
					this.writer.print( logMsg.message );
					this.writer.println();
					this.writer.flush();
					}
				break;
			
			case SyslogAction.FORWARD:
				this.forwardMessage( logMsg );
				break;
			
			case SyslogAction.EMAIL:
				this.emailMessage( logMsg );
				break;
			
			case SyslogAction.CLASS:
				if ( this.action != null )
					{
					this.action.processMessage( logMsg );
					}
				break;
			
			case SyslogAction.SOUND:
				SyslogMedia.playAudioClip
					( this.parameters[0], this.parameters[1] );
				break;
			
			case SyslogAction.SYSTEM:
				if ( this.parameters != null 
						&& this.parameters.length > 0 )
					{
					try {
						Process proc =
							Runtime.getRuntime().exec( this.parameters );
						}
					catch ( Exception ex )
						{
						this.parameters = null;
						System.err.println
							( "ERROR exec-ing '" + this.parameters[0]
								+ "'\n\t" + "   " + ex.getMessage() );
						}
					}
				break;
			
			case SyslogAction.DISPLAY:
				if ( this.display != null )
					{
					if ( ! logMsg.isRepeat
							&& this.parameters.length > 1 )
						{
						SyslogMessage cMsg =
							(SyslogMessage)logMsg.clone();

						cMsg.message =
							this.formatMessage
								( this.parameters[1], logMsg );

						logMsg = cMsg;
						}

					this.display.processMessage( logMsg );
					}
				break;
			}
		}

	public String
	formatMessage( String format, SyslogMessage logMsg )
		{
		Hashtable vars = new Hashtable();

		if ( logMsg.matchVars != null )
			{
			for ( int i = 0 ; i < logMsg.matchVars.length ; ++i )
				{
				vars.put( ("" + i), logMsg.matchVars[i] );
				}
			}

		vars.put( "facility",
					SyslogDefs.getFacilityName( logMsg.facility ) );
		vars.put( "priority",
					SyslogDefs.getPriorityName( logMsg.priority ) );

		vars.put( "timestamp", logMsg.timestamp );
		vars.put( "hostname", logMsg.hostName );
		vars.put( "processname", logMsg.processName );
		vars.put( "processid", ("" + logMsg.processId) );
		vars.put( "message", logMsg.message );

		return
			StringUtilities.stringSubstitution
				( format, vars );
		}

	private void
	emailMessage( SyslogMessage logMsg )
		{
		SimpleSMTP	smtp;

		try {
			smtp = new com.mlp.net.SimpleSMTP
					( this.parameters[0] );

			String body =
				logMsg.timestamp
					+ SimpleSMTP.EOL
				+ logMsg.hostName
					+ SimpleSMTP.EOL
				+ SyslogDefs.getFacilityName( logMsg.facility )
					+ SimpleSMTP.EOL
				+ SyslogDefs.getPriorityName( logMsg.priority )
					+ SimpleSMTP.EOL
				+ logMsg.processName
					+ SimpleSMTP.EOL
				+ logMsg.processId
					+ SimpleSMTP.EOL
				+ logMsg.message
					+ SimpleSMTP.EOL
					+ SimpleSMTP.EOL
				+ logMsg.timestamp + " " + logMsg.hostName + " "
					+ logMsg.processName
					+ "[" + logMsg.processId + "] "
					+ logMsg.message;

			smtp.sendMailMsg
				( "syslogd", this.parameters[1],
					this.parameters[2], body );
			}
		catch ( UnknownHostException ex )
			{
			this.closeAction();
			System.err.println
				( "ERROR unknown SMTP host '" + this.parameters[0]
					+ "':\n\t" + ex.getMessage() );
			}
		catch ( ProtocolException ex )
			{
			System.err.println
				( "ERROR SMTP protocol error:\n\t"
					+ ex.getMessage() );
			}
		catch ( IOException ex )
			{
			System.err.println
				( "ERROR SMTP io error:\n\t"
					+ ex.getMessage() );
			}
		}

	public void
	openClassAction()
		{
		this.action = null;
		String className = this.parameters[0];

		try {
			Class actClass = Class.forName( className );

			String interfaceName =
				"com.mlp.syslogd.SyslogActionInterface";

			if ( ! ClassUtilities.implementsInterface
					( actClass, interfaceName ) )
				{
				System.err.println
					( "ERROR class '" + className
						+ "' does not implement the interface '"
						+ interfaceName + "'." );
				}
			else
				{
				this.action = (SyslogActionInterface)
					actClass.newInstance();

				this.action.setParameters( this.parameters );

				this.action.openAction();
				}
			}
		catch ( ClassNotFoundException ex )
			{
			this.isOpen = false;
			System.err.println
				( "ERROR could not load action class '"
					+ className + "'\n\t" + ex.getMessage() );
			}
		catch ( InstantiationException ex )
			{
			this.isOpen = false;
			System.err.println
				( "ERROR could not instantiate action class '"
					+ className + "'\n\t" + ex.getMessage() );
			}
		catch ( IllegalAccessException ex )
			{
			this.isOpen = false;
			System.err.println
				( "ERROR could not load action class '"
					+ className + "'\n\t" + ex.getMessage() );
			}
		}

	public void
	closeClassAction()
		{
		if ( this.action != null )
			{
			this.action.closeAction();
			this.action = null;
			}
		}

	public void
	openForwardAction()
		{
		String hostName = this.parameters[0];

		this.forwardPort = SyslogDefs.DEFAULT_PORT;

		if ( this.parameters.length > 1 )
			{
			try {
				this.forwardPort =
					Integer.parseInt( this.parameters[1] );
				}
			catch ( NumberFormatException ex )
				{
				this.forwardPort = SyslogDefs.DEFAULT_PORT;
				System.err.println
					( "ERROR bad forward port '" + this.parameters[1]
						+ "' using '" + this.forwardPort + "'\n\t"
						+ ex.getMessage() );
				}
			}

		try
			{
			this.forwardAddr =
				InetAddress.getByName( hostName );

			this.forwardSocket =
				new DatagramSocket();

			// Check for a forwarding loop....
			InetAddress localAddr = null;
			try { localAddr =  InetAddress.getLocalHost(); }
				catch ( UnknownHostException ex ) { localAddr = null; }

			if ( localAddr != null )
				{
				if ( localAddr.getHostAddress().equalsIgnoreCase
						( this.forwardAddr.getHostAddress() ) )
					{
					System.err.println
						( "WARNING forward host '"
							+ hostName + "' is the local host.\n\t"
							+ " A forwarding loop is highly likely." );
					}
				}
			}
		catch ( UnknownHostException ex )
			{
			this.isOpen = false;
			this.forwardSocket = null;
			System.err.println
				( "ERROR getting forward host '"
					+ hostName + "' address:\n\t"
						+ ex.getMessage() );
			}
		catch ( SocketException ex )
			{
			this.isOpen = false;
			this.forwardSocket = null;
			System.err.println
				( "ERROR creating forward udp socket '"
					+ this.forwardPort + "@" + hostName
					+ "':\n\t" + ex.getMessage() );
			}
		}

	private void
	forwardMessage( SyslogMessage logMsg )
		{
		int		idx;
		byte[]	data;
		byte[]	strBytes;
		String	strObj = null;
		Integer intObj = new Integer(0);

		if ( this.forwardSocket != null )
			{
			int length =
				logMsg.message.length() + 5 + 16
					+ logMsg.processName.length() + 8 + 2;

			data = new byte[ length + 32 ];
			
			idx = 0;
			data[idx++] = '<';
			
			int pricode =
				SyslogDefs.computeCode
					( logMsg.facility, logMsg.priority );

			strBytes = intObj.toString( pricode ).getBytes();
			System.arraycopy
				( strBytes, 0, data, idx, strBytes.length );
			idx += strBytes.length;
			
			data[idx++] = '>';
			
			strBytes = logMsg.timestamp.getBytes();
			System.arraycopy
				( strBytes, 0, data, idx, strBytes.length );
			idx += strBytes.length;

			data[idx++] = ' ';

			strBytes = logMsg.processName.getBytes();
			System.arraycopy
				( strBytes, 0, data, idx, strBytes.length );
			idx += strBytes.length;
			
			data[idx++] = '[';

			strBytes = intObj.toString( logMsg.processId ).getBytes();
			System.arraycopy
				( strBytes, 0, data, idx, strBytes.length );
			idx += strBytes.length;

			data[idx++] = ']';

			data[idx++] = ':';
			data[idx++] = ' ';

			strBytes = logMsg.message.getBytes();
			System.arraycopy
				( strBytes, 0, data, idx, strBytes.length );
			idx += strBytes.length;
			
			data[idx] = 0;

			DatagramPacket packet =
				new DatagramPacket
					( data, idx, this.forwardAddr, this.forwardPort );
			
			try
				{
				this.forwardSocket.send( packet );
				}
			catch ( IOException ex )
				{
				this.closeAction();
				System.err.println
					( "ERROR forwarding message:\n\t" + ex.getMessage() );
				}
			}
		}

	static public int
	getActionType( String typeStr )
		{
		if ( typeStr.equalsIgnoreCase( "DISPLAY" ) )
			{
			return SyslogAction.DISPLAY;
			}
		else if ( typeStr.equalsIgnoreCase( "FILE" ) )
			{
			return SyslogAction.FILE;
			}
		else if ( typeStr.equalsIgnoreCase( "SOUND" ) )
			{
			return SyslogAction.SOUND;
			}
		else if ( typeStr.equalsIgnoreCase( "SYSTEM" ) )
			{
			return SyslogAction.SYSTEM;
			}
		else if ( typeStr.equalsIgnoreCase( "EMAIL" ) )
			{
			return SyslogAction.EMAIL;
			}
		else if ( typeStr.equalsIgnoreCase( "FORWARD" ) )
			{
			return SyslogAction.FORWARD;
			}

		return SyslogAction.DISPLAY;
		}

	static public String
	actionName( int type )
		{
		switch ( type )
			{
			case SyslogAction.DISPLAY:	return "DISPLAY";
			case SyslogAction.FILE:		return "FILE";
			case SyslogAction.FORWARD:	return "FORWARD";
			case SyslogAction.EMAIL:	return "EMAIL";
			case SyslogAction.SOUND:	return "SOUND";
			case SyslogAction.SYSTEM:	return "SYSTEM";
			}

		return "unknwon action type '" + type + "'";
		}

	public String
	toString()
		{
		StringBuffer result = new StringBuffer();

		result.append( SyslogAction.actionName( this.type ) );
		result.append( "(" );
		if ( this.parameters != null )
			for ( int i = 0 ; i < this.parameters.length ; ++i )
				{
				result.append( this.parameters[i] );
				if ( i < (this.parameters.length - 1) )
					result.append( ", " );
				}
		result.append( ")" );

		return result.toString();
		}
	}



