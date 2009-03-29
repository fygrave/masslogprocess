
package com.mlp.syslogd;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.ItemSelectable;
import java.awt.Label;
import java.awt.List;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.NoSuchElementException;

import com.mlp.util.AWTUtilities;
import com.mlp.util.UserProperties;
import com.mlp.widget.ImageCanvas;
import com.mlp.widget.SimpleLabel;


public class
SyslogDApplet extends Applet
		implements ActionListener, ItemListener, FeedbackDisplayer
	{
	public static final String		RCS_ID = "$Id: SyslogDApplet.java,v 1.1.1.1 1998/02/22 05:47:54 time Exp $";
	public static final String		RCS_REV = "$Revision: 1.1.1.1 $";
	public static final String		RCS_NAME = "$Name:  $";

	private SyslogServer	server;

	private List			displayList;
	private Hashtable		displays;
	private SimpleLabel		msgLabel;


	public
	SyslogDApplet( SyslogServer server )
		{
		super();
		this.server = server;
		this.msgLabel = null;
		this.displayList = null;
		this.displays = new Hashtable();
		}
	
	public void
	init()
		{
		super.init();
		this.establishContents();
		this.establishSyslogDisplays();
		this.server.setFeedbackDisplayer( this );
		}

	public void
	hideSyslogDisplays()
		{
		Enumeration enum1 = this.displays.elements();

		for ( ; enum1.hasMoreElements() ; )
			{
			try {
				SyslogDisplayInterface display =
					(SyslogDisplayInterface) enum1.nextElement();

				display.hideDisplay();
				}
			catch ( NoSuchElementException ex )
				{ break; }
			}
		}

	public void
	showSyslogDisplays()
		{
		Enumeration enum1 = this.displays.elements();

		for ( ; enum1.hasMoreElements() ; )
			{
			try {
				SyslogDisplayInterface display =
					(SyslogDisplayInterface) enum1.nextElement();

				display.showDisplay();
				}
			catch ( NoSuchElementException ex )
				{ break; }
			}
		}

	private void
	establishSyslogDisplays()
		{
		DisplayEntry			displayEntry;
		SyslogDisplayInterface	display;

		SyslogConfig config =
			this.server.getConfiguration();

		displayEntry = config.getDisplayEntry( "MAIN" );
		if ( displayEntry == null )
			{
			displayEntry =
				new DisplayEntry
					( "MAIN", "STD", "Main Syslog",
						100, 20, 30, 500, 300, null );

			config.addDisplayEntry( displayEntry );

			System.err.println
				( "Added default 'MAIN' display definition." );
			}

		Enumeration enum1 =
			config.getDisplayEnumeration();

		for ( ; enum1.hasMoreElements() ; )
			{
			displayEntry = (DisplayEntry) enum1.nextElement();

			display = displayEntry.createDisplay();
			
			if ( display != null )
				{
				this.server.registerActionDisplay
					( displayEntry.getName(), display );

				this.displays.put( displayEntry.getTitle(), display );

				this.displayList.addItem( displayEntry.getTitle() );
				}
			}
		}

	public void
	displayFeedback( String message )
		{
		this.msgLabel.setText( message );
		}

    public void
    itemStateChanged( ItemEvent event )
		{
		ItemSelectable	item =
			event.getItemSelectable();

		if ( item == this.displayList )
			{
			String title = this.displayList.getSelectedItem();

			SyslogDisplayInterface display =
				(SyslogDisplayInterface)
					this.displays.get( title );

			if ( display != null )
				{
				display.bringToFront();
				}
			}
		}

	public void
	actionPerformed( ActionEvent event )
		{
	    String command = event.getActionCommand();

	//	System.err.println( "SyslogDApplet.actionPerformed '" + command + "'" );
		if ( event.getSource() == this.displayList )
			{
			String title = this.displayList.getSelectedItem();

			SyslogDisplayInterface display =
				(SyslogDisplayInterface)
					this.displays.get( title );

			if ( display != null )
				{
				display.hideDisplay();
				}
			}
		}

	public void
	restart()
		{
		}

	public SyslogDisplayInterface
	getDisplay( String name )
		{
		return
			(SyslogDisplayInterface)
				this.displays.get( name );
		}

	public void
	establishContents()
		{
		int			row;
		Label		lbl;
		Panel		pan;
		String		title;

		Color backColor =
			UserProperties.getColor(
				"mainWindow.bg",
				new Color( 200, 215, 250 ) );
		this.setBackground( backColor );

		this.setLayout( new GridBagLayout() );

		pan = new com.mlp.widget.BorderPanel
				( 5, 2, 2, com.mlp.widget.BorderPanel.RIDGE );
		pan.setLayout( new GridBagLayout() );
		AWTUtilities.constrain(
			this, pan,
			GridBagConstraints.BOTH,
			GridBagConstraints.CENTER,
			0, 0, 1, 1, 1.0, 1.0 );

		row = 0;

		Image	img = null;
		String	imagePath =
			UserProperties.getProperty
				( "mainWindow.logo.imagePath",
					"images/logo.gif" );

		try {
			img = AWTUtilities.getImageResource
					( this.getClass(), imagePath );
			}
		catch ( IOException ex )
			{
			img = null;
			System.err.println
				( "ERROR loading image '" + imagePath + "'" );
			}

		if ( img != null )
			{ 
			ImageCanvas logo = new ImageCanvas( img );
			AWTUtilities.constrain(
				pan, logo,
				GridBagConstraints.BOTH,
				GridBagConstraints.CENTER,
				0, row++, 1, 1, 1.0, 0.0 );
			}
		else
			{
			String logoLblStr =
				UserProperties.getProperty
					( "mainWindow.logo.title", null );

			if ( logoLblStr != null )
				{
				Font logoLblFont =
					UserProperties.getFont(
						"mainWindow.logo.font",
							new Font( "Serif", Font.BOLD, 18 ) );

				lbl = new Label( logoLblStr );
				lbl.setAlignment( Label.CENTER );
				lbl.setFont( logoLblFont );

				AWTUtilities.constrain(
					pan, lbl,
					GridBagConstraints.HORIZONTAL,
					GridBagConstraints.CENTER,
					0, row++, 1, 1, 1.0, 0.0 );
				}
			}

		Font listFont =
			UserProperties.getFont(
				"mainWindow.list.font",
				new Font( "Monospaced", Font.PLAIN, 14 ) );

		this.displayList = new List( 4, false );
		this.displayList.setFont( listFont );
		this.displayList.addItemListener( this );
		this.displayList.addActionListener( this );

		AWTUtilities.constrain(
			pan, this.displayList,
			GridBagConstraints.BOTH,
			GridBagConstraints.CENTER,
			0, row++, 1, 1, 1.0, 1.0 );

		Font labelFont =
			UserProperties.getFont(
				"mainWindow.message.font",
				new Font( "Serif", Font.BOLD, 12 ) );

		this.msgLabel =
			new com.mlp.widget.SimpleLabel( "Initializing..." );
		this.msgLabel.setFont( labelFont );
		this.msgLabel.setBorderWidth( 0 );

		AWTUtilities.constrain(
			pan, this.msgLabel,
			GridBagConstraints.HORIZONTAL,
			GridBagConstraints.CENTER,
			0, row++, 1, 1, 1.0, 0.0 );
		}

	}

