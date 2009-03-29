

package com.mlp.syslogd;

public interface
SyslogDisplayInterface
	{
	public static final String		RCS_ID = "$Id: SyslogDisplayInterface.java,v 1.1.1.1 1998/02/22 05:47:54 time Exp $";
	public static final String		RCS_REV = "$Revision: 1.1.1.1 $";
	public static final String		RCS_NAME = "$Name:  $";


	abstract public void
		openDisplay(
			String title, int bufLength,
			int x, int y, int w, int h,
			String[] parameters );

	abstract public void closeDisplay();

	abstract public void bringToFront();

	abstract public void sendToBack();

	abstract public void hideDisplay();

	abstract public void showDisplay();

	abstract public void restart();

	abstract public void
		processMessage( SyslogMessage logMsg );
	}



