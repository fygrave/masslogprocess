
package com.mlp.syslogd;

import java.io.*;
import java.net.*;
import java.lang.*;

import com.mlp.rexec.RExec;


public class
SpammerAction
	{
	public static final String		RCS_ID = "$Id: SpammerAction.java,v 1.2 1998/02/24 03:39:36 time Exp $";
	public static final String		RCS_REV = "$Revision: 1.2 $";
	public static final String		RCS_NAME = "$Name:  $";

	private String			expr;
	private SyslogMatch		matcher;

	public
	SpammerAction()
		{
		this.expr = expr;
		}

	public void
	setParameters( String[] parameters )
		{
		this.expr = parameters[0];
		}

	public void
	restart()
		{
		}

	public void
	openAction()
		{
		this.matcher = new SyslogMatch();

		try {
			this.matcher.compile( this.expr );
			}
		catch ( MatchCompileException ex )
			{
			this.matcher = null;
			}
		}

	public void
	closeAction()
		{
		}

	public void
	processMessage( SyslogMessage logMsg )
		{
		if ( this.matcher.matchMessage( logMsg.message ) )
			{
			String ipAddr = this.matcher.getMatchSubExpr( 1 );

			RExec exec =
				new RExec( "stylus.ice.com", "time", "timebomb",
							"/home/time/killspam " + ipAddr );

			try { exec.open(); }
			catch ( UnknownHostException ex )
				{
				System.err.println
					( "UNKNOWN HOST opening killspam: "
						+ ex.getMessage() );
				}
			catch ( IOException ex )
				{
				System.err.println
					( "ERROR opening killspam: "
						+ ex.getMessage() );
				}

			exec.close();
			}
		}

	}



