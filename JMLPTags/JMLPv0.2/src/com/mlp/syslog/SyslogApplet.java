
package com.mlp.syslog;

import java.applet.Applet;
import java.awt.Button;
import java.awt.Choice;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.NoSuchElementException;


class
SyslogApplet extends Applet
		implements ActionListener
	{
	private Syslog		syslog;
	private Choice		facChoice;
	private Choice		priChoice;
	private TextField	hostname;
	private TextField	message;
	
	private Hashtable	facHash;
	private Hashtable	priHash;


	public
	SyslogApplet()
		{
		super();

		this.priHash = new Hashtable();

		this.priHash.put( "Emergency",	new Integer( SyslogDefs.LOG_EMERG) );
		this.priHash.put( "Alert",		new Integer( SyslogDefs.LOG_ALERT) );
		this.priHash.put( "Critical",	new Integer( SyslogDefs.LOG_CRIT) );
		this.priHash.put( "Error",		new Integer( SyslogDefs.LOG_ERR) );
		this.priHash.put( "Warning",	new Integer( SyslogDefs.LOG_WARNING) );
		this.priHash.put( "Notice",		new Integer( SyslogDefs.LOG_NOTICE) );
		this.priHash.put( "Info",		new Integer( SyslogDefs.LOG_INFO) );
		this.priHash.put( "Debug",		new Integer( SyslogDefs.LOG_DEBUG) );

		this.facHash = new Hashtable();

		this.facHash.put( "Kernel",		new Integer( SyslogDefs.LOG_KERN) );
		this.facHash.put( "User",		new Integer( SyslogDefs.LOG_USER) );
		this.facHash.put( "Mail",		new Integer( SyslogDefs.LOG_MAIL) );
		this.facHash.put( "Daemon",		new Integer( SyslogDefs.LOG_DAEMON) );
		this.facHash.put( "Authority",	new Integer( SyslogDefs.LOG_AUTH) );
		this.facHash.put( "Syslog",		new Integer( SyslogDefs.LOG_SYSLOG) );
		this.facHash.put( "Lpr",		new Integer( SyslogDefs.LOG_LPR) );
		this.facHash.put( "News",		new Integer( SyslogDefs.LOG_NEWS) );
		this.facHash.put( "Uucp",		new Integer( SyslogDefs.LOG_UUCP) );
		this.facHash.put( "Cron",		new Integer( SyslogDefs.LOG_CRON) );

		this.facHash.put( "Local0",		new Integer( SyslogDefs.LOG_LOCAL0) );
		this.facHash.put( "Local1",		new Integer( SyslogDefs.LOG_LOCAL1) );
		this.facHash.put( "Local2",		new Integer( SyslogDefs.LOG_LOCAL2) );
		this.facHash.put( "Local3",		new Integer( SyslogDefs.LOG_LOCAL3) );
		this.facHash.put( "Local4",		new Integer( SyslogDefs.LOG_LOCAL4) );
		this.facHash.put( "Local5",		new Integer( SyslogDefs.LOG_LOCAL5) );
		this.facHash.put( "Local6",		new Integer( SyslogDefs.LOG_LOCAL6) );
		this.facHash.put( "Local7",		new Integer( SyslogDefs.LOG_LOCAL7) );
		}
	
	public void
	init()
		{
		super.init();

		try {
			this.syslog =
				new Syslog
					( "SyslogTest", SyslogDefs.LOG_PERROR );
			}
		catch ( SyslogException ex )
			{
			System.err.println
				( "FATAL creating Syslog instance: '"
					+  ex.getMessage() + "'" );
			}

		this.establishContents();
		}

	public void
	runTestOne()
		{
		String host = this.hostname.getText();
		try {
			InetAddress addr = InetAddress.getByName( host );

			this.syslog.syslog
				( addr, SyslogDefs.LOG_LOCAL0, SyslogDefs.LOG_ERR,
					"Test Syslog Message #1 (LOCAL0.ERR)" );
			}
		catch ( UnknownHostException ex )
			{
			String message =
				"error locating host named '" + host
					+ "': " + ex.getMessage();
			}
		catch ( SyslogException ex )
			{
			System.err.println
				( "ERROR sending syslog message: '"
					+  ex.getMessage() + "'" );
			}
		}

	public void
	runTestTwo()
		{
		String host = this.hostname.getText();
		try {
			InetAddress addr = InetAddress.getByName( host );

			this.syslog.syslog
				( addr, SyslogDefs.LOG_LOCAL0, SyslogDefs.LOG_ERR,
					"Test Syslog Message #2 (LOCAL0.ERR)" );
			}
		catch ( UnknownHostException ex )
			{
			String message =
				"error locating host named '" + host
					+ "': " + ex.getMessage();
			}
		catch ( SyslogException ex )
			{
			System.err.println
				( "ERROR sending syslog message: '"
					+  ex.getMessage() + "'" );
			}
		}

	public void
	runFloodTest()
		{
		String host = this.hostname.getText();
		try {
			InetAddress addr = InetAddress.getByName( host );

			for ( int j = 0 ; j < 25 ; ++j )
				{
				this.syslog.syslog
					( addr, SyslogDefs.LOG_LOCAL0, SyslogDefs.LOG_ERR,
						"Test Syslog Message #" + j + "  (LOCAL0.ERR)" );
				}
			}
		catch ( UnknownHostException ex )
			{
			String message =
				"error locating host named '" + host
					+ "': " + ex.getMessage();
			}
		catch ( SyslogException ex )
			{
			System.err.println
				( "ERROR sending syslog message: '"
					+  ex.getMessage() + "'" );
			}
		}

	public void
	sendMessage()
		{
		String facStr = this.facChoice.getSelectedItem();
		String priStr = this.priChoice.getSelectedItem();
		
		Integer facInt = (Integer)facHash.get( facStr );
		Integer priInt = (Integer)priHash.get( priStr );
		
		if ( facInt == null || priInt == null )
			{
			System.err.println
				( "ERROR "
					+ (facInt == null
						? "facility" : "level")
					+ " is null! fac '" + facStr
					+ "' lvl '" + priStr + "'" );
			}
		else
			{
			String host = this.hostname.getText();
			try {
				InetAddress addr = InetAddress.getByName( host );

				this.syslog.syslog
					( addr, facInt.intValue(), priInt.intValue(),
						this.message.getText() );
				}
			catch ( UnknownHostException ex )
				{
				String message =
					"error locating host named '" + host
						+ "': " + ex.getMessage();
				}
			catch ( SyslogException ex )
				{
				System.err.println
					( "ERROR logging: " + ex.getMessage() );
				}
			}
		}

	public void
	actionPerformed( ActionEvent event )
		{
	    String command = event.getActionCommand();
		
		if ( command.equals( "SEND" ) )
			{
			this.sendMessage();
			}
		}

	public void
	establishContents()
		{
		int row = 0;
		Enumeration enum1;

		Panel pan = new Panel();
		pan.setLayout( new GridBagLayout() );
		
		Panel hpan = new Panel();
		hpan.setLayout( new GridBagLayout() );
		this.constrain(
			pan, hpan,
			GridBagConstraints.HORIZONTAL,
			GridBagConstraints.CENTER,
			0, row++, 2, 1, 1.0, 0.0 );
		
		Label lbl = new Label( "Syslog Server Host:" );
		this.constrain(
			hpan, lbl,
			GridBagConstraints.NONE,
			GridBagConstraints.WEST,
			0, row, 1, 1, 0.0, 0.0 );
		
 		this.hostname = new TextField( 60 );
		this.hostname.setEditable( true );

		this.constrain(
			hpan, this.hostname,
			GridBagConstraints.HORIZONTAL,
			GridBagConstraints.CENTER,
			1, row, 1, 1, 1.0, 0.0 );
		
		this.priChoice = new Choice();
		enum1 = this.priHash.keys();
		for ( ; enum1.hasMoreElements() ; )
			{
			String keyStr = null;

			try { keyStr = (String)enum1.nextElement(); }
				catch ( NoSuchElementException ex )
					{ break; }

			if ( keyStr != null )
				this.priChoice.addItem( keyStr );
			}

		this.constrain(
			pan, this.priChoice,
			GridBagConstraints.HORIZONTAL,
			GridBagConstraints.CENTER,
			0, row, 1, 1, 1.0, 0.0 );
		
		this.facChoice = new Choice();
		enum1 = this.facHash.keys();
		for ( ; enum1.hasMoreElements() ; )
			{
			String keyStr = null;

			try { keyStr = (String)enum1.nextElement(); }
				catch ( NoSuchElementException ex )
					{ break; }

			if ( keyStr != null )
				this.facChoice.addItem( keyStr );
			}
		
		this.constrain(
			pan, this.facChoice,
			GridBagConstraints.HORIZONTAL,
			GridBagConstraints.CENTER,
			1, row, 1, 1, 1.0, 0.0 );
		
		++row;

 		this.message = new TextField( 80 );
		this.message.setEditable( true );

		this.constrain(
			pan, this.message,
			GridBagConstraints.BOTH,
			GridBagConstraints.CENTER,
			0, row, 2, 1, 1.0, 1.0 );
		
		++row;

		Button send_it = new Button( "Syslog Message" );
		send_it.setActionCommand( "SEND" );
		send_it.addActionListener( this );
		this.constrain(
			pan, send_it,
			GridBagConstraints.HORIZONTAL,
			GridBagConstraints.CENTER,
			0, row, 2, 1, 1.0, 0.0 );
		
		this.setLayout( new GridBagLayout() );
		this.constrain(
			this, pan,
			GridBagConstraints.BOTH,
			GridBagConstraints.CENTER,
			0, 0, 1, 1, 1.0, 1.0 );
		}


	public void
	constrain(
			Container container, Component component,
			int fill, int anchor,
			int gx, int gy, int gw, int gh,
			double wx, double wy )
		{
		GridBagConstraints	c =
			new GridBagConstraints();
		
		c.fill = fill;
		c.anchor = anchor;
		c.gridx = gx;
		c.gridy = gy;
		c.gridwidth = gw;
		c.gridheight = gh;
		c.weightx = wx;
		c.weighty = wy;
		
		( (GridBagLayout)container.getLayout() ).
			setConstraints( component, c );
		
		container.add( component );
		}
	}
