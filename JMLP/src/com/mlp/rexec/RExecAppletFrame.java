
package com.mlp.rexec;

import java.io.*;
import java.awt.*;
import java.util.*;
import java.applet.*;

class RExecAppletFrame extends Frame
	{
	private Applet		applet;

	private Button		send_it;
	private Checkbox	send_stdin;
	private TextField	hostname;
	private TextField	username;
	private TextField	password;
	private TextField	command;
	private TextArea	stdInText;

	private RExec		exec;

	public RExecAppletFrame( String title, Applet app, int w, int h )
		{
		super( title );

		applet = app;

		MenuBar mbar = new MenuBar();
		Menu file = new Menu( "File", true );
		mbar.add( file );
		file.add( "TestMsg1" );
		file.add( "TestMsg2" );
		file.add( "Flood 25" );
		file.add( "Quit" );

		this.setMenuBar( mbar );

		Panel pan = new Panel();
		pan.setLayout( new GridBagLayout() );

		int		row = 0;
	/*
		level_choice = new Choice();
		level_choice.addItem( "Hmmm..." );
		level_choice.addItem( "Well..." );
		level_choice.addItem( "Uh..." );

		constrain( pan, level_choice,
			GridBagConstraints.NONE,
			GridBagConstraints.NORTHWEST,
			0, row, 1, 1, 1.0, 0.0 );
	*/
 		Label lblHostname = new Label( "Host Name:" );

		constrain( pan, lblHostname,
			GridBagConstraints.HORIZONTAL,
			GridBagConstraints.CENTER,
			0, row, 2, 1, 1.0, 1.0 );

 		Label lblUsername = new Label( "User Name:" );

		constrain( pan, lblUsername,
			GridBagConstraints.HORIZONTAL,
			GridBagConstraints.CENTER,
			2, row, 1, 1, 1.0, 1.0 );

 		Label lblPassword = new Label( "Password:" );

		constrain( pan, lblPassword,
			GridBagConstraints.HORIZONTAL,
			GridBagConstraints.CENTER,
			3, row, 1, 1, 1.0, 1.0 );

		row++;

 		hostname = new TextField( 24 );
		hostname.setEditable( true );

		constrain( pan, hostname,
			GridBagConstraints.HORIZONTAL,
			GridBagConstraints.CENTER,
			0, row, 2, 1, 1.0, 1.0 );

 		username = new TextField( 12 );
		username.setEditable( true );

		constrain( pan, username,
			GridBagConstraints.HORIZONTAL,
			GridBagConstraints.CENTER,
			2, row, 1, 1, 1.0, 1.0 );

 		password = new TextField( 12 );
		password.setEditable( true );

		constrain( pan, password,
			GridBagConstraints.HORIZONTAL,
			GridBagConstraints.CENTER,
			3, row, 1, 1, 1.0, 1.0 );

		row++;

 		Label lblCommand = new Label( "Command:" );

		constrain( pan, lblCommand,
			GridBagConstraints.NONE,
			GridBagConstraints.WEST,
			0, row, 1, 1, 0.0, 1.0 );

 		command = new TextField( 72 );
		command.setEditable( true );

		constrain( pan, command,
			GridBagConstraints.HORIZONTAL,
			GridBagConstraints.CENTER,
			1, row, 3, 1, 1.0, 1.0 );

		send_it = new Button( "Send It" );

		constrain( pan, send_it,
			GridBagConstraints.BOTH,
			GridBagConstraints.CENTER,
			4, 1, 1, 1, 1.0, 1.0 );

		send_stdin = new Checkbox( "StdIn" );

		constrain( pan, send_stdin,
			GridBagConstraints.BOTH,
			GridBagConstraints.CENTER,
			4, 2, 1, 1, 1.0, 1.0 );

		this.setLayout( new GridBagLayout() );

		constrain( this, pan,
			GridBagConstraints.HORIZONTAL,
			GridBagConstraints.CENTER,
			0, 0, 1, 1, 1.0, 0.0 );

		stdInText = new TextArea( 8, 48 );

		constrain( this, stdInText,
			GridBagConstraints.BOTH,
			GridBagConstraints.CENTER,
			0, 1, 1, 1, 1.0, 1.0 );

		app.init();
		app.start();
		}

	// Handle system and user events...
	public boolean handleEvent( Event evt )
		{
		boolean		event_handled = false;

		switch ( evt.id )
			{
			case Event.WINDOW_DESTROY:
				event_handled = true;
				dispose();
				break;

			case Event.ACTION_EVENT:
				if ( evt.target == send_it )
					{
					String host = this.hostname.getText();
					String user = this.username.getText();
					String pass = this.password.getText();
					String cmd = this.command.getText();

					System.out.println
						( "Host '" + host + "' user '" + user +
							"' pass '" + pass + "'" );
					System.out.println( "Command '" + cmd + "'" );

					this.exec = new
						RExec( host, user, pass, cmd );

					try { exec.open(); }
					catch ( Exception ex )
						{
						System.err.println
							( "FATAL opening exec: '"
								+  ex.getMessage() + "'" );
						}

					try
						{
						int		b, readResult;
						String	line;
						byte[]	buffer;

						DataInputStream in = exec.getInputStream();
						DataOutputStream out = exec.getOutputStream();

						if ( this.send_stdin.getState() )
							{
							System.out.println( "Sending StdIn!" );
							String stdin = this.stdInText.getText();
							StringTokenizer toke =
								new StringTokenizer( stdin, "\r" );
							String tokestr;

							for ( ; ; )
								{
								try { tokestr = toke.nextToken(); }
									catch ( NoSuchElementException ex )
									{ break; }

		System.out.println( "Sending '" + tokestr + "'" );

								byte[] buf = new byte[ tokestr.length() + 1 ];
								tokestr.getBytes
									( 0, tokestr.length(), buf, 0 );
								buf[ tokestr.length() ] = 10;

		System.out.println( "Sending BUF '" + buf.toString() + "'" );
								out.write( buf );
								}

							out.flush();
							}

	//=====================================================================
	// LOOK HERE
	//
	// HERE IS THE BUG:
	// 
	// If I remove the following line, then a command that does not expect
	// stdin will work and we will get the results from the command.
	// However, if you leave the following line in place, which is _proper_
	// rexec protocol (close input when finished with stdin), then the
	// "in.read()" below will fail complaining that the socket is closed!
	//
	// In other words, with the following line commented out, the command
	// "ls -l" will work perfectly. However, "cat" will not work, since it
	// expects stdin until EOF, which is broken in Java sockets. HOW?
	// First, the process on the other end of the socket NEVER receives
	// an EOF on its socket, so the close is not done properly. Secondly,
	// once I close one side of the socket, the other side no longer works
	// in Java.
	//=====================================================================

						exec.closeOutput();

						buffer = new byte[256];

						for ( ; ; )
							{
							readResult = in.read( buffer );
							if ( readResult == -1 )
								break;

							line = new String( buffer, 0, 0, readResult );

							System.out.print( line );
							}

						exec.closeInput();
						}
					catch ( Exception ex )
						{
						System.err.println
							( "ERROR reading exec results: '"
								+ ex.getMessage() + "'" );
						}

					exec.close();

					System.err.println( "All Done." );
					}
				else if ( evt.target instanceof MenuItem )
					{
					String menu_name = evt.arg.toString();

					if ( menu_name.equals( "Quit" ) )
						{
						event_handled = true;
						dispose();
						}
					else if ( menu_name.equals( "TestMsg1" ) )
						{
						}
					else if ( menu_name.equals( "TestMsg2" ) )
						{
						}
					}
				break;
			}

		if ( event_handled )
			return true;
		else
			return super.handleEvent( evt );
		}

	public void
	constrain(
			Container container, Component component,
			int fill, int anchor,
			int gx, int gy, int gw, int gh, double wx, double wy )
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
