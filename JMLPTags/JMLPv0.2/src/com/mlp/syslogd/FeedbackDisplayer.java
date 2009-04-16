
package com.mlp.syslogd;

public interface
FeedbackDisplayer
	{
	public static final String		RCS_ID = "$Id: FeedbackDisplayer.java,v 1.1.1.1 1998/02/22 05:47:54 time Exp $";
	public static final String		RCS_REV = "$Revision: 1.1.1.1 $";
	public static final String		RCS_NAME = "$Name:  $";


	abstract public void
		displayFeedback( String message );
	}

