
package com.mlp.syslogd;

import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.util.*;


public class
SyslogDFrame extends Frame
		implements ActionListener, ItemListener, WindowListener
	{
	public static final String		RCS_ID = "$Id: SyslogDFrame.java,v 1.1.1.1 1998/02/22 05:47:54 time Exp $";
	public static final String		RCS_REV = "$Revision: 1.1.1.1 $";
	public static final String		RCS_NAME = "$Name:  $";

	protected Applet		applet;
	protected MenuBar		mBar;
	protected Menu			mFile;


	public
	SyslogDFrame(
			String title, Applet app,
			int x, int y, int w, int h )
		{
		super( title );

		this.applet = app;

		this.establishMenuBar();

		this.setMenuBar( this.mBar );
		this.applet.setStub(null);
		this.applet.init();

		this.add( "Center", applet );

		this.pack();

		this.setLocation( x, y );

		Dimension fSz = this.getSize();

		if ( w == -1 ) w = fSz.width;
		if ( h == -1 ) h = fSz.height;

		this.setSize( w, h );

		this.show();

		this.applet.start();

		this.addWindowListener( this );
		}
	 
	public void
	shutdown()
		{
		this.applet.stop();
		this.dispose();
		System.exit( 0 );
		}

    public void
    itemStateChanged( ItemEvent event )
        {
		}

    public void
    actionPerformed( ActionEvent event )
        {
	    String command = event.getActionCommand();

		if ( command.equals( "QUIT" ) )
			{
			this.shutdown();
			}
		else if ( command.equals( "HIDE_ALL" ) )
			{
			this.getSyslogDApplet().hideSyslogDisplays();
			}
		else if ( command.equals( "SHOW_ALL" ) )
			{
			this.getSyslogDApplet().showSyslogDisplays();
			}
		else if ( command.equals( "ABOUT" ) )
			{
			this.showAboutDialog();
			}
		else if ( command.equals( "LOAD" ) )
			{
			}
		else if ( command.equals( "RELOAD" ) )
			{
			}
        }

	public SyslogDApplet
	getSyslogDApplet()
		{
		return (SyslogDApplet) this.applet;
		}

	public void
	showAboutDialog()
		{
		AboutDialog dialog = new AboutDialog( this );
		dialog.show();
		}

	private void
	establishMenuBar()
		{
		MenuItem	mItem;

		this.mBar = new MenuBar();

		this.mFile = new Menu( "File", true );
		this.mBar.add( this.mFile );

		mItem = new MenuItem( "Hide All Displays" );
		mItem.setActionCommand( "HIDE_ALL" );
		mItem.addActionListener( this );
		this.mFile.add( mItem );

		mItem = new MenuItem( "Show All Displays" );
		mItem.setActionCommand( "SHOW_ALL" );
		mItem.addActionListener( this );
		this.mFile.add( mItem );

		this.mFile.addSeparator();

		mItem = new MenuItem( "Quit" );
		mItem.setActionCommand( "QUIT" );
		mItem.addActionListener( this );
		this.mFile.add( mItem );

		this.addAdditionalMenus( mBar );

		this.setMenuBar( this.mBar );
		}

	public void
	addAdditionalMenus( MenuBar menuBar )
		{
		MenuItem	mItem;

		Menu hMenu = new Menu( "Help", true );
		this.mBar.add( hMenu );

		mItem = new MenuItem( "About..." );
		mItem.setActionCommand( "ABOUT" );
		mItem.addActionListener( this );

		hMenu.add( mItem );
		}

	public void
	windowOpened(WindowEvent e)
		{
		}

	public void
	windowClosing(WindowEvent e)
		{
		this.shutdown();
		}

	public void
	windowClosed(WindowEvent e)
		{
		}

	public void
	windowActivated(WindowEvent e)
		{
		}

	public void
	windowDeactivated(WindowEvent e)
		{
		}

	public void
	windowIconified(WindowEvent e)
		{
		}

	public void
	windowDeiconified(WindowEvent e)
		{
		}

	}
