
package com.mlp.syslogd;

import java.io.Serializable;


public class
SyslogMessage
		implements Cloneable, Serializable
	{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7207085721774690593L;
	public static final String		RCS_ID = "$Id: SyslogMessage.java,v 1.1.1.1 1998/02/22 05:47:54 time Exp $";
	public static final String		RCS_REV = "$Revision: 1.1.1.1 $";
	public static final String		RCS_NAME = "$Name:  $";

	public boolean		isRepeat;
	public int			facility;
	public int			priority;
	public String		timestamp;
	public String		hostName;
	public String		processName;
	public int			processId;
	public String		message;
	public String[]		matchVars;


	private
	SyslogMessage()
		{
		super();

		this.isRepeat = false;
		this.facility = 0;
		this.priority = 0;
		this.timestamp = null;
		this.hostName = null;
		this.processName = null;
		this.processId = 0;
		this.message = null;
		this.matchVars = null;
		}

	public
	SyslogMessage(
			int facility, int priority,
			String timestamp, String hostName,
			String processName, int processId, String message )
		{
		super();

		this.isRepeat = false;
		this.facility = facility;
		this.priority = priority;
		this.timestamp = timestamp;
		this.hostName = hostName;
		this.processName = processName;
		this.processId = processId;
		this.message = message;
		this.matchVars = null;
		}

	public Object
	clone()
		{
		SyslogMessage cObj = null;

		try { cObj = (SyslogMessage) super.clone(); }
			catch ( CloneNotSupportedException ex )
				{ cObj = new SyslogMessage(); }

		cObj.message = new String( this.message );
		cObj.hostName = new String( this.hostName );
		cObj.timestamp = new String( this.timestamp );
		cObj.processName = new String( this.processName );
	
		if ( this.matchVars != null )
			{
			cObj.matchVars =
				new String[ this.matchVars.length ];

			for ( int i = 0 ; i < this.matchVars.length ; ++i )
				cObj.matchVars[i] =
					new String( this.matchVars[i] );
			}

		return cObj;
		}

	}


