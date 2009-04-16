package com.mlp.rexec;

import java.io.*;
import java.net.*;

/**
 * The Syslog class implements the UNIX syslog protocol allowing Java
 * to log messages to a specified UNIX host. Care has been taken to
 * preserve as much of the UNIX implementation as possible.
 * <br>
 * To use Syslog, simply create an instance, and use the Syslog() method
 * to log your message. The class provides all the expected syslog constants.
 * For example, LOG_ERR is Syslog.LOG_ERR.
 * <br>
 *
 * Written: Tim Endres<br>
 * Version: 1.0 - August 14, 1996<br>
 * @see DatagramSocket
 * @see	InetAddress
 */

public class RExec extends Object
	{
	// CLASS CONSTANTS

	public static final int DEFAULT_REXEC_PORT = 512;
	public static final int DEFAULT_LOCAL_PORT = 300;

	// CLASS VARIABLES

	// INSTANCE VARIABLES

	private String			hostName;
	private String			userName;
	private String			userPass;
	private String			command;

	private int				flags;
    private boolean         inIsOpen;
    private boolean         outIsOpen;

	private InetAddress		address;
	private Socket			socket;
	private DataInputStream		in;
	private DataOutputStream	out;

	// CLASS METHODS

	// INSTANCE VARIABLES

	/**
	 * Creates a rexec object instance
	 */
	public
	RExec( String hostName, String userName, String userPass, String command )
		{
		super();

		this.hostName = hostName;
		this.userName = userName;
		this.userPass = userPass;
		this.command = command;
		this.inIsOpen = false;
		this.outIsOpen = false;
		this.in = null;
		this.out = null;
		this.socket = null;
		}

	public void
	open()
		throws IOException, UnknownHostException
		{

		try { this.socket = new Socket( this.hostName, DEFAULT_REXEC_PORT ); }
			catch ( UnknownHostException ex )
				{ throw ex; }
			catch ( IOException ex )
				{ throw ex; }

		try {
		    this.out = new DataOutputStream( this.socket.getOutputStream() );
		    }
		catch ( IOException ex )
			{ throw ex; }

        this.outIsOpen = true;
		System.err.println
			( "socket outstream class '" + out.getClass().getName() + "'" );

		try {
		    this.in = new DataInputStream( this.socket.getInputStream() );
		    }
		catch ( IOException ex )
			{ throw ex; }

        this.inIsOpen = true;

		int		len;
		byte[]	buf;

		buf = new byte[1];
		buf[0] = 0;

		System.err.println( "sending initial zero" );

		try { this.out.write( buf ); }
			catch ( IOException ex )
				{ throw ex; }

		len = this.userName.length();
		buf = new byte[ len + 1 ];
		this.userName.getBytes( 0, len, buf, 0 );
		buf[len] = 0;

		System.err.println( "sending user name" );

		try { this.out.write( buf ); }
			catch ( IOException ex )
				{ throw ex; }

		len = this.userPass.length();
		buf = new byte[ len + 1 ];
		this.userPass.getBytes( 0, len, buf, 0 );
		buf[len] = 0;

		System.err.println( "sending user password" );

		try { this.out.write( buf ); }
			catch ( IOException ex )
				{ throw ex; }

		len = this.command.length();
		buf = new byte[ len + 1 ];
		this.command.getBytes( 0, len, buf, 0 );
		buf[len] = 0;

		System.err.println( "sending command" );

		try { this.out.write( buf ); }
			catch ( IOException ex )
				{ throw ex; }

		System.err.println( "open complete!" );

		int b = this.in.read();
		System.err.println( "first byte = '" + b + "'" );

		if ( b != 0 )
			throw new IOException
				( "error, non-zero start byte '"
					+ b + "' from rexecd" );
		}

    public void
    closeInput()
        {
        try {
    		if ( this.inIsOpen )
    		    this.in.close();
    	    }
    	catch ( IOException ex )
    	    {
    	    System.err.println
    	        ( "ERROR closing input stream: " + ex.getMessage() );
    	    }

		this.inIsOpen = false;
        }

    public void
    closeOutput()
        {
        try {
    		if ( this.outIsOpen )
    		    this.out.close();
    	    }
    	catch ( IOException ex )
    	    {
    	    System.err.println
    	        ( "ERROR closing output stream: " + ex.getMessage() );
    	    }

		this.outIsOpen = false;
        }

	public void
	close()
		{
		this.closeInput();
		this.closeOutput();

		try { this.socket.close(); }
		catch ( IOException ex )
			{
			System.err.println
				( "RExec.close() - socket close fails - "
				    + ex.getMessage() );
			}
		}

	public DataInputStream
	getInputStream()
		throws IOException
		{
		if ( ! this.inIsOpen )
		    throw new IOException( "IN is not open" );

		return this.in;
		}

	public DataOutputStream
	getOutputStream()
		throws IOException
		{
		if ( ! this.outIsOpen )
		    throw new IOException( "OUT is not open" );

		return this.out;
		}
	}













