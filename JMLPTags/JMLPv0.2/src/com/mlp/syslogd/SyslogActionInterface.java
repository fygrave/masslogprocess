

package com.mlp.syslogd;

public interface
SyslogActionInterface
	{
	public static final String		RCS_ID = "$Id: SyslogActionInterface.java,v 1.1.1.1 1998/02/22 05:47:54 time Exp $";
	public static final String		RCS_REV = "$Revision: 1.1.1.1 $";
	public static final String		RCS_NAME = "$Name:  $";


	abstract public void
		setParameters( String[] parameters );

	abstract public void restart();

	abstract public void openAction();

	abstract public void closeAction();

	abstract public void
		processMessage( SyslogMessage logMsg );
	}



