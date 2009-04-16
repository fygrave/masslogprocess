
package com.mlp.syslog;

import java.awt.Frame;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


class
SyslogFrame extends Frame
		implements ActionListener
	{
	private SyslogApplet	applet;
	

	public
	SyslogFrame(
			String title, SyslogApplet app,
			int x, int y, int w, int h )
		{
		super( title );
		
		this.applet = app;

		this.establishMenuBar();

		this.applet.init();

		this.add( "Center", applet );

		this.pack();

		this.setLocation( x, y );

		this.setSize( w, h );

		this.show();

		this.applet.start();

		this.addWindowListener(
			new WindowAdapter()
				{
				public void
				windowClosing(WindowEvent e)
					{ shutdown(); }
				}
			);
		}
	
	private void
	establishMenuBar()
		{
        MenuItem	mItem;
		MenuBar mbar = new MenuBar();

		Menu file = new Menu( "File", true );
		mbar.add( file );

        mItem = new MenuItem( "Test Message 1" );
        mItem.setActionCommand( "TEST1" );
        mItem.addActionListener( this );
	    file.add( mItem );

        mItem = new MenuItem( "Test Message 2" );
        mItem.setActionCommand( "TEST2" );
        mItem.addActionListener( this );
	    file.add( mItem );

        mItem = new MenuItem( "Flood 25 Messages " );
        mItem.setActionCommand( "FLOOD25" );
        mItem.addActionListener( this );
	    file.add( mItem );

	    file.addSeparator();

        mItem = new MenuItem( "Quit " );
        mItem.setActionCommand( "QUIT" );
        mItem.addActionListener( this );
	    file.add( mItem );
		
		this.setMenuBar( mbar );
		}

	public void
	shutdown()
		{
		this.applet.stop();
		this.dispose();
		System.exit( 0 );
		}

    public void
    actionPerformed( ActionEvent event )
        {
	    String command = event.getActionCommand();

		if ( command.equals( "QUIT" ) )
			{
			this.shutdown();
			}
		else if ( command.equals( "SEND" ) )
			{
			this.applet.sendMessage();
			}
		else if ( command.equals( "TEST1" ) )
			{
			this.applet.runTestOne();
			}
		else if ( command.equals( "TEST2" ) )
			{
			this.applet.runTestTwo();
			}
		else if ( command.equals( "FLOOD25" ) )
			{
			this.applet.runFloodTest();
			}
		}

	}

