

package com.mlp.syslogd;

public class
ActionThread extends Thread
	{
	public static final String		RCS_ID = "$Id: ActionThread.java,v 1.1.1.1 1998/02/22 05:47:54 time Exp $";
	public static final String		RCS_REV = "$Revision: 1.1.1.1 $";
	public static final String		RCS_NAME = "$Name:  $";

	private SyslogAction	action;
	private SyslogMessage	logMsg;


	public
	ActionThread( SyslogAction action, SyslogMessage logMsg )
		{
		super();

		this.action = action;
		this.logMsg = logMsg;
		}

	public void
	run()
		{
		this.action.processMessage( this.logMsg );
		}

	}
